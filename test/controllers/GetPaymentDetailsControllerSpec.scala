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
import connectors.GetPaymentDetailsConnector
import controllers.actions.FakeIdentifierAction
import models.ResponseWrapper
import models.errors.{ErrorWrapper, LeppError}
import models.nps.retrieve.RetrieveClaimsResponse
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.test.FakeRequest
import play.api.test.Helpers.*
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

  "/getPaymentDetails" - {
    "return 200" in {

      when(mockGetPaymentDetailsConnector.retrieveDetails(any())(any(), any(), any()))
        .thenReturn(EitherT(Future.successful(Right(ResponseWrapper(correlationId, responseModel)))))

      val request = FakeRequest(
        method = "GET",
        path = "/low-earners-pensions-payment/get-payment-details"
      )

      val response = controller.getPaymentDetails(request)
      status(response) mustBe OK
    }

    "return error" - {
      def serviceErrors(error: String, expectedStatus: Int): Unit =
        s"with code $error from backend" in {

          when(mockGetPaymentDetailsConnector.retrieveDetails(any())(any(), any(), any()))
            .thenReturn(EitherT(Future.successful(Left(ErrorWrapper(correlationId, LeppError(error, "error message"))))))

          val request = FakeRequest(
            method = "GET",
            path = "/low-earners-pensions-payment/get-payment-details"
          )

          val response = controller.getPaymentDetails(request)
          status(response) mustBe expectedStatus
        }

      val input = Seq(
        (BAD_REQUEST_ERROR, BAD_REQUEST),
        (NOT_FOUND_ERROR, NOT_FOUND),
        (INTERNAL_ERROR, INTERNAL_SERVER_ERROR),
        (SERVICE_UNAVAILABLE_ERROR, INTERNAL_SERVER_ERROR)
      )

      input.foreach(args => (serviceErrors _).tupled(args))
    }
  }
}
