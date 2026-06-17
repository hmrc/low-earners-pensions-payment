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

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import common.ItBaseSpec
import config.AppConfig
import models.nps.accept.AcceptLeppPaymentResponse
import org.mockito.Mockito.reset
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.Play.materializer
import play.api.http.Status.*
import play.api.http.Writeable
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsJson, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, route, status, writeableOf_AnyContentAsEmpty, writeableOf_AnyContentAsJson, writeableOf_AnyContentAsText}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.http.HeaderCarrier
import utils.ErrorCodes.*

import scala.concurrent.Future

class AcceptLeppPaymentControllerISpec extends ItBaseSpec {

  private val mockAuthConnector: AuthConnector = mock[AuthConnector]
  private val mockAppConfig: AppConfig = mock[AppConfig]

  override def beforeEach(): Unit = {
    reset(mockAuthConnector)
    reset(mockAppConfig)
  }

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val nino: String = "AA123456C"
    val taxYear: String = "2025"
    val requestJson: JsValue = Json.toJson(acceptRequestBodyModel)
    val responseJson: JsValue = acceptResponseJson
    val responseModel: AcceptLeppPaymentResponse = acceptResponseModel
    
    lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
      method = "POST",
      path = s"/low-earners-pensions-payment/accept-payment/$taxYear"
    )

    def fakeRequestWithBody(requestBody: JsValue = requestJson): FakeRequest[AnyContentAsJson] = {
      fakeRequest.withJsonBody(requestBody)
    }

    lazy val postUrl: String = s"/paye/low-earners/$nino/tax-year/$taxYear/payment-claims"

    def setupStubs(requestBody: String = acceptRequestBodyJson,
                   postStatus: Int,
                   postBody: String): StubMapping = {
      def stub: StubMapping = stubPost(
        url = postUrl,
        requestBody = requestBody,
        response = aResponse.withStatus(postStatus).withBody(postBody)
      )

      stub
    }

    def createResult[A: Writeable](request: FakeRequest[A]): Future[Result] = route(application, request).getOrElse(
      Future.failed(new RuntimeException("TEST_ERROR"))
    )
  }

  "AcceptLeppPaymentController" when {
    "acceptPayment" when {
      "NPS errors occur" should {
        def npsErrorTest(errorStatus: Int, expectedErrorCode: String, expectedErrorStatus: Int): Unit =
          s"handle appropriately when get API returns error status: $errorStatus" in new Test {
            setupStubs(
              postStatus = errorStatus,
              postBody = ""
            )

            val result: Future[Result] = createResult(fakeRequestWithBody())

            status(result) mustBe expectedErrorStatus
            val content: String = contentAsJson(result).toString
            content must include(expectedErrorCode)
          }

        val errorCases: Seq[(Int, String, Int)] = Seq(
          (BAD_REQUEST, BAD_REQUEST_ERROR, INTERNAL_SERVER_ERROR),
          (FORBIDDEN, FORBIDDEN_ERROR, INTERNAL_SERVER_ERROR),
          (NOT_FOUND, NOT_FOUND_ERROR, INTERNAL_SERVER_ERROR),
          (CONFLICT, CONFLICT_ERROR, CONFLICT),
          (UNPROCESSABLE_ENTITY, UNPROCESSABLE_ERROR, INTERNAL_SERVER_ERROR),
          (INTERNAL_SERVER_ERROR, INTERNAL_ERROR, INTERNAL_SERVER_ERROR),
          (SERVICE_UNAVAILABLE, SERVICE_UNAVAILABLE_ERROR, INTERNAL_SERVER_ERROR),
          (IM_A_TEAPOT, UNEXPECTED_STATUS_ERROR, INTERNAL_SERVER_ERROR)
        )

        errorCases.foreach(npsErrorTest)
      }

      "request errors occur" should {
        "should return the expected error when tax year parameter is invalid" in new Test {
          override val taxYear: String = "ABCD"
          val result: Future[Result] = createResult(fakeRequestWithBody())

          status(result) mustBe BAD_REQUEST
          val content: String = contentAsJson(result).toString
          content must include("TAX_YEAR_FORMAT_ERROR")
        }

        "should return the expected error when request body is missing" in new Test {
          val result: Future[Result] = createResult(fakeRequest)

          status(result) mustBe BAD_REQUEST
          val content: String = contentAsJson(result).toString
          content must include("MISSING_REQUEST_BODY_ERROR")
        }

        "should return the expected error when request body isn't valid JSON" in new Test {
          val result: Future[Result] = createResult(fakeRequest.withTextBody("NOT_JSON"))

          status(result) mustBe BAD_REQUEST
          val content: String = contentAsJson(result).toString
          content must include("REQUEST_BODY_NOT_JSON_ERROR")
        }

        "should return the expected error when request body JSON has invalid format" in new Test {
          val result: Future[Result] = createResult(fakeRequestWithBody(JsObject.empty))

          status(result) mustBe BAD_REQUEST
          val content: String = contentAsJson(result).toString
          content must include("REQUEST_BODY_FORMAT_ERROR")
        }
      }

      "NPS returns a success response should handle as expected" in new Test {
        setupStubs(
          postStatus = CREATED,
          postBody = responseJson.toString
        )

        val result: Future[Result] = createResult(fakeRequestWithBody())

        status(result) mustBe CREATED
        contentAsJson(result) mustBe Json.toJson(responseModel)
      }
    }
  }
}