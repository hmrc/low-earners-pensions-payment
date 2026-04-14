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

package base

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import controllers.actions.FakeIdentifierAction
import models.nps.retrieve.{LowEarnersCalculation, LowEarnersClaimDetails, LowEarnersDataDetails, LowEarnersDetails, RetrieveClaimsResponse}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.{Millis, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.BodyParsers
import uk.gov.hmrc.http.test.{HttpClientV2Support, WireMockSupport}

import scala.jdk.CollectionConverters.MapHasAsJava
import scala.reflect.ClassTag

abstract class ItBaseSpec
    extends AnyWordSpec
    with WireMockSupport
    with HttpClientV2Support
    with ScalaFutures
    with Matchers
    with GuiceOneServerPerSuite {

  val parsers: BodyParsers.Default = app.injector.instanceOf[BodyParsers.Default]

  val fakeIdentifierAction: FakeIdentifierAction = new FakeIdentifierAction(parsers)

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = scaled(Span(500, Millis)), interval = scaled(Span(50, Millis)))

  implicit val queryParamsToJava: Map[String, String] => java.util.Map[String, StringValuePattern] = _.map {
    case (k, v) =>
      k -> equalTo(v)
  }.asJava

  protected def applicationBuilder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .configure(
        "auditing.enabled" -> false,
        "metric.enabled" -> false
      )

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

  private val calculation: LowEarnersCalculation = LowEarnersCalculation(
    lowEarnersClaimDetails = claimDetails,
    lowEarnersDataDetails = dataDetails
  )

  private val details: LowEarnersDetails = LowEarnersDetails(
    taxYear = 11,
    lowEarnersCalculations = Seq(calculation)
  )

  val retrieveResponse: RetrieveClaimsResponse = RetrieveClaimsResponse(
    currentLowEarnersOptimisticLock = 123,
    identifier = "id",
    lowEarnersDetailsList = Seq(details)
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
}
