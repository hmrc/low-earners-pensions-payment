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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.client.{ResponseDefinitionBuilder, WireMock}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import common.ItBaseSpec
import models.errors.{ErrorWrapper, LeppError}
import models.nps.accept.{AcceptLeppPaymentRequest, AcceptLeppPaymentRequestBody, AcceptLeppPaymentResponse, LowEarnersAccountDetails}
import models.{CorrelationId, ResponseWrapper}
import org.scalactic.Prettifier.default
import play.api.Application
import play.api.http.Status.*
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import utils.ErrorCodes.{BAD_REQUEST_ERROR, CONFLICT_ERROR, FORBIDDEN_ERROR, INTERNAL_ERROR, NOT_FOUND_ERROR, SERVICE_UNAVAILABLE_ERROR, UNPROCESSABLE_ERROR}

import scala.concurrent.ExecutionContext.Implicits.global

class AcceptLeppPaymentConnectorISpec extends ItBaseSpec {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val correlationId: CorrelationId = "X-123"

    val nino: String = "AA123456C"
    val taxYear: BigInt = 2025
    val npsUrl = s"/paye/low-earners/$nino/tax-year/$taxYear/payment-claims"

    val accountDetails: LowEarnersAccountDetails = LowEarnersAccountDetails(
      accountName = "Name",
      accountNumber = "12345678",
      sortCode = "123456",
      rollNumber = Some("roll")
    )

    val requestBody: AcceptLeppPaymentRequestBody = AcceptLeppPaymentRequestBody(
      currentLowEarnersOptimisticLock = 1234,
      lowEarnersAccountDetails = accountDetails
    )
    val requestBodyJson: String = Json.toJson(requestBody).toString

    val request: AcceptLeppPaymentRequest = AcceptLeppPaymentRequest(
      identifier = Nino(nino),
      taxYear = taxYear,
      body = requestBody
    )

    val responseJsonString: String = acceptResponseJson.toString

    val application: Application = new GuiceApplicationBuilder()
      .configure(
        "microservice.services.nps.port" -> wireMockPort,
        "urls.npsContext" -> ""
      )
      .build()

    val connector: AcceptLeppPaymentConnector = application.injector.instanceOf[AcceptLeppPaymentConnector]
    
    def mockNpsResult(npsStatus: Int,
                      bodyOpt: Option[String],
                      cidOpt: Option[CorrelationId] = Some("X-123")): StubMapping = {
      val response: ResponseDefinitionBuilder = aResponse().withStatus(npsStatus)
      val responseWithBodyOpt = bodyOpt.map(response.withBody).getOrElse(response)
      
      val responseWithCidOpt = cidOpt.map(
        cid => response.withHeader("correlationId", cid.value)
      ).getOrElse(responseWithBodyOpt)
      
      stubPost(
        url = npsUrl,
        requestBody = requestBodyJson,
        response = responseWithCidOpt
      )
    }
    
    lazy val result: Either[ErrorWrapper, ResponseWrapper[AcceptLeppPaymentResponse]] =
      await(connector.acceptPayment(request).value)
  }

  "AcceptLeppPaymentConnector" -> {
    "acceptPayment" -> {
      def handleForErrorScenario(npsStatus: Int, expectedError: String): Unit = {
        s"[acceptPayment] should return an error with code: $expectedError when NPS returns status: $npsStatus" in new Test {
          mockNpsResult(npsStatus = npsStatus, bodyOpt = None)
          
          result mustBe a[Left[_, _]]
          result.swap
            .getOrElse(ErrorWrapper(correlationId, LeppError("N/A", "N/A")))
            .error
            .code mustBe expectedError

          WireMock.verify(postRequestedFor(urlEqualTo(npsUrl)))
        }
      }

      Seq(
        (BAD_REQUEST, BAD_REQUEST_ERROR),
        (FORBIDDEN, FORBIDDEN_ERROR),
        (NOT_FOUND, NOT_FOUND_ERROR),
        (CONFLICT, CONFLICT_ERROR),
        (UNPROCESSABLE_ENTITY, UNPROCESSABLE_ERROR),
        (INTERNAL_SERVER_ERROR, INTERNAL_ERROR),
        (SERVICE_UNAVAILABLE, SERVICE_UNAVAILABLE_ERROR)
      ).foreach(handleForErrorScenario)

      "[acceptPayment] should return the expected result when NPS returns an unrecognised error code" in new Test {
        mockNpsResult(npsStatus = IM_A_TEAPOT, bodyOpt = None)

        result mustBe a[Left[_, _]]
        result.swap
          .getOrElse(ErrorWrapper(correlationId, LeppError("N/A", "N/A")))
          .error
          .code mustBe "UNEXPECTED_STATUS_ERROR"

        WireMock.verify(postRequestedFor(urlEqualTo(npsUrl)))
      }
      
      "[acceptPayment] should return the expected result when NPS returns an unparsable CREATED response" in new Test {
        mockNpsResult(npsStatus = CREATED, bodyOpt = Some(""))
        
        result mustBe a[Left[_, _]]
        result.swap
          .getOrElse(ErrorWrapper(correlationId, LeppError("N/A", "N/A")))
          .error
          .code mustBe "INTERNAL_SERVER_ERROR"

        WireMock.verify(postRequestedFor(urlEqualTo(npsUrl)))
      }

      "[acceptPayment] should return the expected result when NPS returns an invalid CREATED response" in new Test {
        mockNpsResult(
          npsStatus = CREATED,
          bodyOpt = Some(
            """
              |{
              | "updatedLowEarnersOptimisticLock": "one"
              |}
            """.stripMargin
          )
        )
        
        result mustBe a[Left[_, _]]
        result.swap
          .getOrElse(ErrorWrapper(correlationId, LeppError("N/A", "N/A")))
          .error
          .code mustBe "INTERNAL_SERVER_ERROR"

        WireMock.verify(postRequestedFor(urlEqualTo(npsUrl)))
      }
      
      "[acceptPayment] should return the expected result when NPS returns a valid CREATED response" in new Test {
        mockNpsResult(npsStatus = CREATED, bodyOpt = Some(responseJsonString))
        
        result mustBe a[Right[_, _]]
        result
          .getOrElse(ResponseWrapper(correlationId, dummyAcceptResponse))
          .responseData mustBe acceptResponseModel
        
        WireMock.verify(postRequestedFor(urlEqualTo(npsUrl)))
      }

      "[acceptPayment] should handle appropriately when correlation ID is missing for a success" in new Test {
        mockNpsResult(npsStatus = CREATED, bodyOpt = Some(responseJsonString), cidOpt = None)
        
        result mustBe a[Right[_, _]]
        result
          .getOrElse(ResponseWrapper(correlationId, dummyAcceptResponse))
          .responseData mustBe acceptResponseModel
        
        WireMock.verify(postRequestedFor(urlEqualTo(npsUrl)))
      }

      "[acceptPayment] should handle appropriately when correlation ID is non-matching for a success" in new Test {
        mockNpsResult(npsStatus = CREATED, bodyOpt = Some(responseJsonString), cidOpt = Some("not-matching"))
        
        result mustBe a[Right[_, _]]
        result
          .getOrElse(ResponseWrapper(correlationId, dummyAcceptResponse))
          .responseData mustBe acceptResponseModel
        
        WireMock.verify(postRequestedFor(urlEqualTo(npsUrl)))
      }
    }
  }
}
