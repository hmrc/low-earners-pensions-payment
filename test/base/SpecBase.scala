/*
 * Copyright 2026 HM Revenue & Customs
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

import controllers.actions.{FakeIdentifierAction, IdentifierAction}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues, TryValues}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.{Application, inject}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.mvc.BodyParsers
import play.api.test.Helpers.running
import models.nps.retrieve._

import java.net.URLEncoder
import scala.reflect.ClassTag

trait SpecBase
    extends AnyFreeSpec
    with Matchers
    with TryValues
    with OptionValues
    with ScalaFutures
    with IntegrationPatience
    with MockitoSugar
    with BeforeAndAfterEach
    with GuiceOneServerPerSuite {

  val parsers: BodyParsers.Default = app.injector.instanceOf[BodyParsers.Default]

  private val fakeIdentifierAction: FakeIdentifierAction = new FakeIdentifierAction(parsers)

  protected def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        inject.bind[IdentifierAction].toInstance(fakeIdentifierAction)
      )

  def runningApplication(block: Application => Unit): Unit =
    running(_ => applicationBuilder())(block)

  def urlEncode(input: String): String = URLEncoder.encode(input, "utf-8")

  protected def injected[A: ClassTag](implicit app: Application): A = app.injector.instanceOf[A]

  lazy val client: WSClient = app.injector.instanceOf[WSClient]

  val dataDetails: LowEarnersDataDetails = LowEarnersDataDetails(
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

  val claimDetails: LowEarnersClaimDetails = LowEarnersClaimDetails(
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

  val calculation: LowEarnersCalculation = LowEarnersCalculation(
    lowEarnersClaimDetails = claimDetails,
    lowEarnersDataDetails = dataDetails
  )

  val details: LowEarnersDetails = LowEarnersDetails(
    taxYear = 11,
    lowEarnersCalculations = Seq(calculation)
  )

  val retrieveResponse: RetrieveClaimsResponse = RetrieveClaimsResponse(
    currentLowEarnersOptimisticLock = 123,
    identifier = "id",
    lowEarnersDetailsList = Seq(details)
  )

}
