/*
 * Copyright 2023 HM Revenue & Customs
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

import base.SpecBase
import cats.data.EitherT
import connectors.{ConnectorResult, GetPaymentDetailsConnector}
import controllers.actions.FakeIdentifierAction
import controllers.requests.CorrelationId
import models.errors.{ErrorWrapper, LeppError}
import models.nps.retrieve.RetrieveClaimsResponse
import models.requests.{AuthUser, IdentifierRequest}
import models.response.ResponseWrapper
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import play.api.libs.json.{JsObject, JsString}
import play.api.mvc.Results.{BadRequest, ImATeapot}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.domain.Nino
import utils.ErrorCodes.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class GetPaymentDetailsControllerSpec extends SpecBase {

  private lazy val mockGetPaymentDetailsConnector: GetPaymentDetailsConnector = mock[GetPaymentDetailsConnector]

  private val controller = new GetPaymentDetailsController(
    stubMessagesControllerComponents(),
    new FakeIdentifierAction(parsers),
    mockGetPaymentDetailsConnector
  )

  val responseModel: RetrieveClaimsResponse = retrieveResponse

  val correlationId = "X-123"
  
  private trait Test {
    def mockConnectorSuccess(): OngoingStubbing[ConnectorResult[RetrieveClaimsResponse]] = when(
      mockGetPaymentDetailsConnector.retrieveDetails(any())(any(), any(), any())
    ).thenReturn(
      EitherT(Future.successful(Right(
        ResponseWrapper(correlationId = correlationId, responseData = responseModel)
      )))
    )

    def mockConnectorFailure(error: String): OngoingStubbing[ConnectorResult[RetrieveClaimsResponse]] = when(
      mockGetPaymentDetailsConnector.retrieveDetails(any())(any(), any(), any())
    ).thenReturn(
      EitherT(Future.successful(Left(
        ErrorWrapper(correlationId = correlationId, error = LeppError(error, "error message"))
      )))
    )
  }

  "/getPaymentDetails" - {
    val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
      method = "GET",
      path = "/low-earners-pensions-payment/get-payment-details"
    )
    
    "return 200" in new Test {
      mockConnectorSuccess()
      val response: Future[Result] = controller.getPaymentDetails(request)
      status(response) mustBe OK
    }

    "return error" - {
      def serviceErrors(error: String, expectedStatus: Int): Unit =
        s"with code $error from backend" in new Test {
          mockConnectorFailure(error)
          val response: Future[Result] = controller.getPaymentDetails(request)
          status(response) mustBe expectedStatus
        }

      val input: Seq[(String, Int)] = Seq(
        (BAD_REQUEST_ERROR, BAD_REQUEST),
        (NOT_FOUND_ERROR, NOT_FOUND),
        (INTERNAL_ERROR, INTERNAL_SERVER_ERROR),
        (SERVICE_UNAVAILABLE_ERROR, INTERNAL_SERVER_ERROR)
      )

      input.foreach(args => (serviceErrors _).tupled(args))
    }
  }

  "/getLeppSummary" - {
    val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(
      method = "GET",
      path = "/low-earners-pensions-payment/get-lepp-summary"
    )

    "should return a 200 response for a success" in new Test {
      mockConnectorSuccess()
      val result: Future[Result] = controller.getLeppSummary(request)
      status(result) mustBe OK
      contentAsString(result) must include("NO_ACTIONS")
    }
    
    "should handle for a 404 response from NPS" in new Test {
      mockConnectorFailure(NOT_FOUND_ERROR)
      val result: Future[Result] = controller.getLeppSummary(request)
      status(result) mustBe OK
      contentAsString(result) must include("NOT_ELIGIBLE")
    }

    "handle when connector returns an error status" - {
      def serviceErrors(error: String, expectedStatus: Int): Unit =
        s"with code $error from backend" in new Test {
          mockConnectorFailure(error)
          val result: Future[Result] = controller.getLeppSummary(request)
          status(result) mustBe expectedStatus
        }

      val input: Seq[(String, Int)] = Seq(
        (BAD_REQUEST_ERROR, BAD_REQUEST),
        (INTERNAL_ERROR, INTERNAL_SERVER_ERROR),
        (SERVICE_UNAVAILABLE_ERROR, INTERNAL_SERVER_ERROR)
      )

      input.foreach(args => (serviceErrors _).tupled(args))
    }
  }
  
  "getLeppData" - {
    implicit val request: IdentifierRequest[AnyContentAsEmpty.type] = IdentifierRequest(
      request = FakeRequest(),
      user = AuthUser(userId = "some-user-id", nino = Nino(generateNino())),
      correlationId = CorrelationId("some-id")
    )

    "should handle appropriately for a connector failure" in new Test {
      mockConnectorFailure("an error")
      
      val result: Future[Result] = controller.getLeppData("context")(
        dataMap = _ => ImATeapot("Teapot time!"),
        errorMap = err => BadRequest(err.error.code)
      )(request)
      
      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe "an error"
    }
    
    "should handle appropriately for a success response" in new Test {
      mockConnectorSuccess()

      val result: Future[Result] = controller.getLeppData("context")(
        dataMap = _ => ImATeapot("Teapot time!"),
        errorMap = err => BadRequest(err.error.code)
      )(request)

      status(result) mustBe IM_A_TEAPOT
      contentAsString(result) mustBe "Teapot time!"
    }
  }
  
  "successResponseToResult" - {
    "should return the expected result for a success response" in {
      val result: Future[Result] = Future.successful(
        controller.successResponseToResult(
          ResponseWrapper(
            correlationId = CorrelationId("some-id"),
            responseData = JsObject(Seq("field" -> JsString("value")))
          )
        )
      )
      
      headers(result).get("correlationId") must not be empty
      status(result) mustBe OK
    }
  }
}
