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

import base.ItBaseSpec
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import models.errors.{ErrorWrapper, LeppError}
import models.nps.accept.{AcceptLeppPaymentRequest, AcceptLeppPaymentRequestBody, AcceptLeppPaymentResponse, LowEarnersAccountDetails}
import models.nps.retrieve.RetrieveClaimsResponse
import models.{CorrelationId, ResponseWrapper}
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import org.scalactic.Prettifier.default
import play.api.Application
import play.api.http.Status.*
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class AcceptLeppPaymentConnectorISpec extends ItBaseSpec {

  trait Test {
    val application: Application = new GuiceApplicationBuilder()
      .configure(
        "microservice.services.nps.port" -> wireMockPort,
        "urls.npsContext" -> ""
      )
      .build()

    val connector: AcceptLeppPaymentConnector = application.injector.instanceOf[AcceptLeppPaymentConnector]

    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val correlationId: CorrelationId = "X-123"
    implicit lazy val actorSystem: ActorSystem = app.actorSystem
    implicit lazy val materializer: Materializer = app.materializer

  }

  "AcceptLeppPaymentConnector" -> {
    val nino: String = "AA123456C"
    val taxYear: BigInt = 2025

    val accountDetails: LowEarnersAccountDetails = LowEarnersAccountDetails(
      accountName = "Name", accountNumber = "12345678", sortCode = "123456", rollNumber = Some("roll")
    )
    val requestBody: AcceptLeppPaymentRequestBody = AcceptLeppPaymentRequestBody(
      currentLowEarnersOptimisticLock = 1234, lowEarnersAccountDetails = accountDetails
    )
    val requestBodyJson: String = Json.toJson(requestBody).toString
    
    val request: AcceptLeppPaymentRequest = AcceptLeppPaymentRequest(
      identifier = Nino(nino),
      taxYear = taxYear,
      body = requestBody
    )

    "acceptPayment" -> {
      val npsUrl = s"/paye/low-earners/$nino/tax-year/$taxYear/payment-claims"

      "[acceptPayment] should return the expected result when NPS returns an unrecognised error code" in new Test {
        stubPost(
          url = npsUrl,
          requestBody = requestBodyJson,
          response = aResponse().withStatus(IM_A_TEAPOT).withHeader(correlationId.value, "X-123")
        )

        val result: Either[ErrorWrapper, ResponseWrapper[AcceptLeppPaymentResponse]] =
          await(connector.acceptPayment(request).value)

        WireMock.verify(postRequestedFor(urlEqualTo(npsUrl)))

        result mustBe a[Left[_, _]]
        result.swap
          .getOrElse(ErrorWrapper(correlationId, LeppError("N/A", "N/A")))
          .error
          .code mustBe "UNEXPECTED_STATUS_ERROR"
      }
      
      "[acceptPayment] should return NOT_FOUND_ERROR when NPS returns an NOT_FOUND status code" in new Test {
        stubPost(
          url = npsUrl,
          requestBody = requestBodyJson,
          response = aResponse().withStatus(NOT_FOUND).withHeader(correlationId.value, "X-123")
        )

        val result: Either[ErrorWrapper, ResponseWrapper[AcceptLeppPaymentResponse]] =
          await(connector.acceptPayment(request).value)

        WireMock.verify(postRequestedFor(urlEqualTo(npsUrl)))

        result mustBe a[Left[_, _]]
        result.swap
          .getOrElse(ErrorWrapper(correlationId, LeppError("N/A", "N/A")))
          .error
          .code mustBe "NOT_FOUND"
      }

      "[acceptPayment] should return the expected result when NPS returns an unparsable CREATED response" in new Test {
        stubPost(
          url = npsUrl,
          requestBody = requestBodyJson,
          response = created().withBody("").withHeader(correlationId.value, "X-123")
        )

        val result: Either[ErrorWrapper, ResponseWrapper[AcceptLeppPaymentResponse]] =
          await(connector.acceptPayment(request).value)

        WireMock.verify(postRequestedFor(urlEqualTo(npsUrl)))

        result mustBe a[Left[_, _]]
        result.swap
          .getOrElse(ErrorWrapper(correlationId, LeppError("N/A", "N/A")))
          .error
          .code mustBe "INTERNAL_SERVER_ERROR"
      }

      "[acceptPayment] should return the expected result when NPS returns an invalid CREATED response" in new Test {
        stubPost(
          url = npsUrl,
          requestBody = requestBodyJson,
          response = created().withBody(
            """
              |{
              | "updatedLowEarnersOptimisticLock": "one"
              |}
            """.stripMargin
          )
        )

        val result: Either[ErrorWrapper, ResponseWrapper[AcceptLeppPaymentResponse]] =
          await(connector.acceptPayment(request).value)

        WireMock.verify(postRequestedFor(urlEqualTo(npsUrl)))

        result mustBe a[Left[_, _]]
        result.swap
          .getOrElse(ErrorWrapper(correlationId, LeppError("N/A", "N/A")))
          .error
          .code mustBe "INTERNAL_SERVER_ERROR"
      }

      val responseJsonString: String = acceptResponseJson.toString

      "[acceptPayment] should return the expected result when NPS returns a valid CREATED response" in new Test {
        stubPost(
          url = npsUrl,
          requestBody = requestBodyJson,
          response = created().withBody(responseJsonString).withHeader(correlationId.value, "X-123")
        )

        val result: Either[ErrorWrapper, ResponseWrapper[AcceptLeppPaymentResponse]] =
          await(connector.acceptPayment(request).value)

        WireMock.verify(postRequestedFor(urlEqualTo(npsUrl)))

        result mustBe a[Right[_, _]]
        result.getOrElse(ResponseWrapper(correlationId, dummyAcceptResponse))
          .responseData mustBe acceptResponse
      }

      "[acceptPayment] should handle appropriately when correlation ID is missing for a success" in new Test {
        stubPost(
          url = npsUrl,
          requestBody = requestBodyJson,
          response = created().withBody(responseJsonString)
        )

        val result: Either[ErrorWrapper, ResponseWrapper[AcceptLeppPaymentResponse]] =
          await(connector.acceptPayment(request).value)

        WireMock.verify(postRequestedFor(urlEqualTo(npsUrl)))

        result mustBe a[Right[_, _]]
        result.getOrElse(ResponseWrapper(correlationId, dummyAcceptResponse))
          .responseData mustBe acceptResponse
      }

      "[acceptPayment] should handle appropriately when correlation ID is non-matching for a success" in new Test {
        stubPost(
          url = npsUrl,
          requestBody = requestBodyJson,
          response = created().withBody(responseJsonString).withHeader("correlationId", "NotMatching")
        )

        val result: Either[ErrorWrapper, ResponseWrapper[AcceptLeppPaymentResponse]] =
          await(connector.acceptPayment(request).value)

        WireMock.verify(postRequestedFor(urlEqualTo(npsUrl)))

        result mustBe a[Right[_, _]]
        result.getOrElse(ResponseWrapper(correlationId, dummyAcceptResponse))
          .responseData mustBe acceptResponse
      }
    }
  }
}
