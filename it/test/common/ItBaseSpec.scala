/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import com.google.inject.{AbstractModule, Provides}
import controllers.actions.{FakeIdentifierAction, IdentifierAction}
import models.nps.accept.{AcceptLeppPaymentRequestBody, AcceptLeppPaymentResponse, LowEarnersAccountDetails}
import models.nps.retrieve.{LowEarnersCalculation, LowEarnersClaimDetails, LowEarnersDataDetails, LowEarnersDetails, RetrieveClaimsResponse}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.{Millis, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.{Application, inject}
import play.api.http.HeaderNames
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.BodyParsers
import play.api.test.FakeRequest
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.http.test.{HttpClientV2Support, WireMockSupport}
import utils.FrozenTime
import utils.HeaderKey.correlationIdKey

import java.time.Clock
import java.util.UUID
import scala.annotation.unused
import scala.jdk.CollectionConverters.MapHasAsJava
import scala.reflect.ClassTag

abstract class ItBaseSpec
    extends AnyWordSpec
    with WireMockSupport
    with HttpClientV2Support
    with ScalaFutures
    with Matchers
    with BeforeAndAfterAll
    with BeforeAndAfterEach
    with GuiceOneServerPerSuite {

  val parsers: BodyParsers.Default = app.injector.instanceOf[BodyParsers.Default]

  val fakeIdentifierAction: FakeIdentifierAction = new FakeIdentifierAction(parsers)

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = scaled(Span(500, Millis)), interval = scaled(Span(50, Millis)))

  implicit val queryParamsToJava: Map[String, String] => java.util.Map[String, StringValuePattern] = _.map {
    case (k, v) =>
      k -> equalTo(v)
  }.asJava

  def rndSessionId: String = s"session-${UUID.randomUUID.toString}"
  
  implicit class AuthRequest[+A](request: FakeRequest[A]) {
    def withAuthToken(authToken: String = "Bearer authToken"): FakeRequest[A] =
      request.withHeaders(HeaderNames.AUTHORIZATION -> authToken, correlationIdKey -> "X-Id")

    def withJsonContentType(): FakeRequest[A] =
      request.withHeaders(HeaderNames.CONTENT_TYPE -> "text/json")

    def withSessionId(sessionId: String = rndSessionId): FakeRequest[A] =
      request.withSession(SessionKeys.sessionId -> sessionId)
  }

  lazy val overridingModule: AbstractModule = new AbstractModule {
    @Provides
    @unused
    def clock: Clock = {
      FrozenTime.reset()
      FrozenTime.getClock
    }
  }

  val overridingGuiceableModule: Seq[GuiceableModule] = Seq(GuiceableModule.fromGuiceModules(List(overridingModule)))

  val application: Application = new GuiceApplicationBuilder()
    .configure(
      "microservice.services.nps.port" -> wireMockPort,
      "urls.npsContext" -> ""
    )
    .overrides(
      inject.bind[IdentifierAction].toInstance(fakeIdentifierAction)
    ).overrides(overridingGuiceableModule: _*)
    .build()
  
  def stubGet(url: String, response: ResponseDefinitionBuilder): StubMapping =
    wireMockServer.stubFor(
      get(urlEqualTo(url))
        .willReturn(response)
    )

  def stubGet(url: String, queryParams: Map[String, String], response: ResponseDefinitionBuilder): StubMapping =
    wireMockServer.stubFor(
      get(urlPathTemplate(url))
        .withQueryParams(queryParamsToJava(queryParams))
        .willReturn(response)
    )

  def stubPost(url: String, requestBody: String, response: ResponseDefinitionBuilder): StubMapping =
    wireMockServer.stubFor(
      post(urlEqualTo(url))
        .withHeader("Content-Type", equalTo("application/json"))
        .withRequestBody(equalTo(requestBody))
        .willReturn(response)
    )

  private val dataDetails: LowEarnersDataDetails = LowEarnersDataDetails(
    responseTimestamp = Some("2023-06-27 09:12:28"),
    calculationSequenceNumber = 123,
    dataSourceMaster = "CESA",
    netPayContributionsTotal = Some(10.56),
    basicRatePercentage = Some(10.56),
    totalAllowances = Some(10.56),
    totalIncome = Some(10.56),
    totalDeductions = Some(10.56),
    totalTaxDue = Some(10.56)
  )

  private val claimDetails: LowEarnersClaimDetails = LowEarnersClaimDetails(
    claimSequenceNumber = 123,
    entitlementAmount = Some(10.56),
    claimStatus = "CANCELLED",
    inSelfAssessment = true,
    calculationDate = Some("2023-06-27"),
    claimDate = Some("2023-06-27"),
    reminderOutputSent = true,
    reissueClaimOutput = true,
    originalAmount = Some(10.56)
  )

  private val leppCalculation: LowEarnersCalculation = LowEarnersCalculation(
    lowEarnersClaimDetails = claimDetails,
    lowEarnersDataDetails = dataDetails
  )

  private val leppDetails: LowEarnersDetails = LowEarnersDetails(
    taxYear = 11,
    lowEarnersCalculations = Seq(leppCalculation)
  )

  val retrieveResponse: RetrieveClaimsResponse = RetrieveClaimsResponse(
    currentLowEarnersOptimisticLock = 123,
    identifier = "id",
    lowEarnersDetailsList = Seq(leppDetails)
  )
  
  val dummyRetrieveResponse: RetrieveClaimsResponse = RetrieveClaimsResponse(0, "Zero", Nil)
  
  val retrieveResponseJson: JsValue = Json.parse(
    """
      |{
      | "currentLowEarnersOptimisticLock": 123,
      | "identifier": "id",
      | "lowEarnersDetailsList": [
      |   {
      |     "taxYear": 11,
      |     "lowEarnersCalculations": [
      |       {
      |         "lowEarnersClaimDetails": {
      |           "claimSequenceNumber": 123,
      |           "calculationDate": "2023-06-27",
      |           "claimDate": "2023-06-27",
      |           "claimStatus": "CANCELLED",
      |           "entitlementAmount": 10.56,
      |           "inSelfAssessment": true,
      |           "originalAmount": 10.56,
      |           "reissueClaimOutput": true,
      |           "reminderOutputSent": true
      |         },
      |         "lowEarnersDataDetails": {
      |           "calculationSequenceNumber": 123,
      |           "basicRatePercentage": 10.56,
      |           "dataSourceMaster": "CESA",
      |           "netPayContributionsTotal": 10.56,
      |           "responseTimestamp": "2023-06-27 09:12:28",
      |           "totalAllowances": 10.56,
      |           "totalDeductions": 10.56,
      |           "totalIncome": 10.56,
      |           "totalTaxDue": 10.56
      |         }
      |       }
      |     ]
      |   }
      | ]
      |}
    """.stripMargin
  )

  val accountDetails: LowEarnersAccountDetails = LowEarnersAccountDetails(
    accountName = "Name",
    accountNumber = "12345678",
    sortCode = "123456",
    rollNumber = Some("ROLL")
  )

  val acceptRequestBodyModel: AcceptLeppPaymentRequestBody = AcceptLeppPaymentRequestBody(
    currentLowEarnersOptimisticLock = 1234,
    lowEarnersAccountDetails = accountDetails
  )
  val acceptRequestBodyJson: String = Json.toJson(acceptRequestBodyModel).toString

  val acceptResponseModel: AcceptLeppPaymentResponse = AcceptLeppPaymentResponse(updatedLowEarnersOptimisticLock = 124)

  val dummyAcceptResponse: AcceptLeppPaymentResponse = AcceptLeppPaymentResponse(updatedLowEarnersOptimisticLock = 999)
  
  val acceptResponseJson: JsValue = Json.parse(
    """
      |{
      | "updatedLowEarnersOptimisticLock": 124
      |}
    """.stripMargin
  )
  
}
