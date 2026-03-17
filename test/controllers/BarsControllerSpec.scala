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

import base.SpecBase
import cats.data.EitherT
import connectors.ConnectorResponse
import controllers.actions.{IdentifierAction, MockIdentifierAction}
import controllers.validators.BarsRequestValidator
import models.CorrelationId
import models.ResponseWrapper.ErrorWrapper
import models.bars.{BarsResponse, ValidatedBarsRequest}
import models.errors.ErrorResult.ServiceErrorResult
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import play.api.libs.json.{JsObject, JsValue}
import play.api.mvc.{ControllerComponents, Headers, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, stubControllerComponents}
import services.{BarsService, CorrelationIdService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BarsControllerSpec extends SpecBase {

  trait Test {
    val mockIdentifierAction: IdentifierAction = new MockIdentifierAction
    val mockValidator: BarsRequestValidator = mock[BarsRequestValidator]
    val mockService: BarsService = mock[BarsService]
    val mockControllerComponents: ControllerComponents = stubControllerComponents()

    val testController: BarsController = new BarsController(
      identifierAction = mockIdentifierAction,
      validator = mockValidator,
      service = mockService,
      cc = mockControllerComponents
    )

    val request: FakeRequest[JsValue] = FakeRequest[JsValue](
      method = POST,
      uri = "some-uri",
      headers = Headers("correlationId" -> testCorrelationId.value),
      body = JsObject.empty
    )

    lazy val result: Future[Result] = testController.checkBankAccountDetails()(request)
    
    def validatorSuccess(): OngoingStubbing[Either[ErrorWrapper, ValidatedBarsRequest]] = when(
      mockValidator.validate(
        json = ArgumentMatchers.eq(JsObject.empty),
        correlationId = ArgumentMatchers.eq(CorrelationId("some-id"))
      )
    ).thenReturn(
      Right(testValidatedBarsRequest)
    )
    
    def serviceSuccess(): OngoingStubbing[ConnectorResponse[BarsResponse]] = when(
      mockService.checkBankAccountDetails(
        ArgumentMatchers.eq(testValidatedBarsRequest),
        ArgumentMatchers.eq(testCorrelationId)
      )(
        ArgumentMatchers.any(),
        ArgumentMatchers.any()
      )
    ).thenReturn(
      EitherT(Future.successful(Right(testSuccessResponse)))
    )
  }

  "BarsController" - {
    "for error scenarios" - {
      "should return error result when request validation fails" in new Test {
        when(
          mockValidator.validate(
            json = ArgumentMatchers.eq(JsObject.empty),
            correlationId = ArgumentMatchers.eq(CorrelationId("some-id"))
          )
        ).thenReturn(
          Left(ErrorWrapper(
            value = ServiceErrorResult(IM_A_TEAPOT, "TEST_ERROR"),
            correlationId = testCorrelationId
          ))
        )
        
        status(result) shouldBe IM_A_TEAPOT
        contentAsJson(result).toString should include("TEST_ERROR")
      }

      "should return error result when BarsService returns an error response" in new Test {
        validatorSuccess()
        
        when(
          mockService.checkBankAccountDetails(
            ArgumentMatchers.eq(testValidatedBarsRequest),
            ArgumentMatchers.eq(testCorrelationId)
          )(
            ArgumentMatchers.any(),
            ArgumentMatchers.any()
          )
        ).thenReturn(
          EitherT(Future.successful(Left(testDownstreamErrorWrapper)))
        )

        status(result) shouldBe IM_A_TEAPOT
        contentAsJson(result).toString should include("TEST_ERROR")
      }
    }

    "for success scenarios" - {
      "should return the expected result" in new Test {
        validatorSuccess()
        serviceSuccess()
        
        status(result) shouldBe OK
        contentAsJson(result).toString should include("banky bank")
      }
    }
  }
}
