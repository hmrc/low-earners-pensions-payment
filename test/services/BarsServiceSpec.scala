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

package services

import base.SpecBase
import cats.data.EitherT
import connectors.{BarsConnector, ConnectorResponse}
import models.ResponseWrapper
import models.bars.{BarsResponse, ValidatedBarsRequest}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import uk.gov.hmrc.http.GatewayTimeoutException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BarsServiceSpec extends SpecBase {

  trait Test {
    val mockConnector: BarsConnector = mock[BarsConnector]
    val testService: BarsService = new BarsService(connector = mockConnector)

    def mockConnectorResponse(req: ValidatedBarsRequest,
                              resp: ConnectorResponse[BarsResponse]): OngoingStubbing[ConnectorResponse[BarsResponse]] = {
      when(
        mockConnector.checkBankAccountDetails(
          request = ArgumentMatchers.eq(req),
          correlationId = ArgumentMatchers.any()
        )(
          hc = ArgumentMatchers.any(), 
          ec = ArgumentMatchers.any()
        )
      ).thenReturn(resp)
    }

    lazy val result: Either[ResponseWrapper.ErrorWrapper, ResponseWrapper.SuccessWrapper[BarsResponse]] = await(
      testService.checkBankAccountDetails(
      barsRequest = testValidatedBarsRequest,
      correlationId = testCorrelationId
      ).value
    )
  }

  "BarsService" - {
    "checkBankAccountDetails" - {
      "when connector throws an exception should surface exception" in new Test {
        mockConnectorResponse(
          req = testValidatedBarsRequest,
          resp = EitherT(Future.failed(new GatewayTimeoutException("msg")))
        )

        assertThrows[GatewayTimeoutException](result)
      }
      
      "when connector returns an error response should surface it" in new Test {
        mockConnectorResponse(
          testValidatedBarsRequest,
          EitherT(Future.successful(Left(testDownstreamErrorWrapper)))
        )
        
        result shouldBe a[Left[_, _]]
        result.swap.getOrElse(dummyErrorWrapper) shouldBe testDownstreamErrorWrapper
      }

      "when connector returns a success should surface it" in new Test {
        mockConnectorResponse(
          testValidatedBarsRequest,
          EitherT(Future.successful(Right(testSuccessResponse)))
        )
        
        result shouldBe a[Right[_, _]]
        result.getOrElse(dummySuccessResponse) shouldBe testSuccessResponse
      }
    }
  }
}
