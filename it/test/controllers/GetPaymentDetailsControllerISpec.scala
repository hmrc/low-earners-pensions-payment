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

package controllers

import base.ItBaseSpec
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import config.AppConfig
import controllers.actions.IdentifierAction
import models.nps.retrieve.RetrieveClaimsResponse
import org.mockito.Mockito.reset
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.Application
import play.api.Play.materializer
import play.api.http.Status.*
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.http.HeaderCarrier
import utils.ErrorCodes.*

import scala.concurrent.Future

class GetPaymentDetailsControllerISpec extends ItBaseSpec {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
      method = "GET",
      path = "/low-earners-pensions-payment/get-payment-details"
    )

    val nino: String = "AA123456C"
    val getResponseJson: String = retrieveResponseJson.toString
    val responseModel: RetrieveClaimsResponse = retrieveResponse
    val getUrl: String = s"/paye/low-earners/$nino/calculation-results"

    val controller: GetPaymentDetailsController = app.injector.instanceOf[GetPaymentDetailsController]

    def setupStubs(
                    getStatus: Int,
                    getResponse: String
                  ): StubMapping = {
      def stub: StubMapping = stubGet(
        url = getUrl,
        response = aResponse.withStatus(getStatus).withBody(getResponse)
      )

      stub
    }
  }

  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val mockAppConfig: AppConfig = mock[AppConfig]

  override def beforeEach(): Unit = {
    reset(mockAuthConnector)
    reset(mockAppConfig)
  }

  "GetPaymentDetailsController" when {
    "getPaymentDetails" should {
      "[getPaymentDetails] return 200 for a valid nino" in new Test {
        setupStubs(
          getStatus = OK,
          getResponse = getResponseJson
        )

        val result: Future[Result] = controller.getPaymentDetails(fakeRequest)

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(responseModel)
      }

      def handleRetrieveErrors(errorStatus: Int, errorCode: String, expectedStatus: Int): Unit =
        s"[getPaymentDetails] handle appropriately when get API returns error status: $errorStatus" in new Test {
          setupStubs(
            getStatus = errorStatus,
            getResponse = "N/A"
          )

          val result: Future[Result] = controller.getPaymentDetails(fakeRequest)

          status(result) mustBe expectedStatus
          val content: String = contentAsJson(result).toString
          content must include(errorCode)
        }

      val getErrorCases: Seq[(Int, String, Int)] = Seq(
        (BAD_REQUEST, BAD_REQUEST_ERROR, BAD_REQUEST),
        (FORBIDDEN, FORBIDDEN_ERROR, FORBIDDEN),
        (NOT_FOUND, NOT_FOUND_ERROR, NOT_FOUND),
        (UNPROCESSABLE_ENTITY, NOT_FOUND_ERROR, NOT_FOUND),
        (INTERNAL_SERVER_ERROR, INTERNAL_ERROR, INTERNAL_SERVER_ERROR),
        (SERVICE_UNAVAILABLE, SERVICE_UNAVAILABLE_ERROR, INTERNAL_SERVER_ERROR),
        (IM_A_TEAPOT, UNEXPECTED_STATUS_ERROR, INTERNAL_SERVER_ERROR)
      )

      getErrorCases.foreach(errorCase => handleRetrieveErrors(errorCase._1, errorCase._2, errorCase._3))
    }
  }
}