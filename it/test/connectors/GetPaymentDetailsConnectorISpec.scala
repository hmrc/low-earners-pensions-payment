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
import controllers.requests.CorrelationId
import models.errors.{ErrorWrapper, LeppError}
import models.response.{LeppPaymentDetails, ResponseWrapper}
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import org.scalactic.Prettifier.default
import play.api.Application
import play.api.http.Status.*
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class GetPaymentDetailsConnectorISpec extends ItBaseSpec {

  trait Test {
    val application: Application = new GuiceApplicationBuilder()
      .configure(
        "microservice.services.nps.port" -> wireMockPort,
        "urls.npsContext" -> ""
      )
      .build()

    val connector: GetPaymentDetailsConnector = application.injector.instanceOf[GetPaymentDetailsConnector]

    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val correlationId: CorrelationId = "X-123"
    implicit lazy val actorSystem: ActorSystem = app.actorSystem
    implicit lazy val materializer: Materializer = app.materializer

  }

  "RetrievePaymentDetailsConnector" -> {
    val nino: String = "AA123456C"

    "retrieveDetails" -> {
      val npsUrl = s"/paye/low-earners/$nino/calculation-results"

      "[retrieveDetails] should return the expected result when NPS returns an unrecognised error code" in new Test {
        stubGet(
          url = npsUrl,
          response = aResponse().withStatus(IM_A_TEAPOT).withHeader(correlationId.value, "X-123")
        )

        val result: Either[ErrorWrapper, ResponseWrapper[LeppPaymentDetails]] =
          await(connector.retrieveDetails(nino).value)

        WireMock.verify(getRequestedFor(urlEqualTo(npsUrl)))

        result mustBe a[Left[_, _]]
        result.swap
          .getOrElse(ErrorWrapper(correlationId, LeppError("N/A", "N/A")))
          .error
          .code mustBe "UNEXPECTED_STATUS_ERROR"
      }

      "[retrieveDetails] should return the expected result when NPS returns an unparsable OK response" in new Test {
        stubGet(
          url = npsUrl,
          response = okJson("").withHeader(correlationId.value, "X-123")
        )

        val result: Either[ErrorWrapper, ResponseWrapper[LeppPaymentDetails]] =
          await(connector.retrieveDetails(nino).value)

        WireMock.verify(getRequestedFor(urlEqualTo(npsUrl)))

        result mustBe a[Left[_, _]]
        result.swap
          .getOrElse(ErrorWrapper(correlationId, LeppError("N/A", "N/A")))
          .error
          .code mustBe "INTERNAL_SERVER_ERROR"
      }

      "[retrieveDetails] should return the expected result when NPS returns an invalid OK response" in new Test {
        stubGet(
          url = npsUrl,
          response = okJson(
            """
              |{
              | "identifier": "one"
              |}
            """.stripMargin
          )
        )

        val result: Either[ErrorWrapper, ResponseWrapper[LeppPaymentDetails]] =
          await(connector.retrieveDetails(nino).value)

        WireMock.verify(getRequestedFor(urlEqualTo(npsUrl)))

        result mustBe a[Left[_, _]]
        result.swap
          .getOrElse(ErrorWrapper(correlationId, LeppError("N/A", "N/A")))
          .error
          .code mustBe "INTERNAL_SERVER_ERROR"
      }

      val responseJsonString: String =
        """
          |{
          | "currentLowEarnersOptimisticLock": 1,
          | "identifier": "One"
          |}
        """.stripMargin

      "[retrieveDetails] should return the expected result when NPS returns a valid OK response" in new Test {
        stubGet(
          url = npsUrl,
          response = okJson(responseJsonString).withHeader(correlationId.value, "X-123")
        )

        val result: Either[ErrorWrapper, ResponseWrapper[LeppPaymentDetails]] =
          await(connector.retrieveDetails(nino).value)

        WireMock.verify(getRequestedFor(urlEqualTo(npsUrl)))

        result mustBe a[Right[_, _]]
        result.getOrElse(ResponseWrapper(correlationId, LeppPaymentDetails(0, "Zero")))
          .responseData mustBe LeppPaymentDetails(1, "One")
      }

      "[retrieveDetails] should handle appropriately when correlation ID is missing for a success" in new Test {
        stubGet(
          url = npsUrl,
          response = okJson(responseJsonString)
        )

        val result: Either[ErrorWrapper, ResponseWrapper[LeppPaymentDetails]] =
          await(connector.retrieveDetails(nino).value)

        WireMock.verify(getRequestedFor(urlEqualTo(npsUrl)))

        result mustBe a[Right[_, _]]
        result.getOrElse(ResponseWrapper(correlationId, LeppPaymentDetails(0, "Zero")))
          .responseData mustBe LeppPaymentDetails(1, "One")
      }

      "[retrieveDetails] should handle appropriately when correlation ID is non-matching for a success" in new Test {
        stubGet(
          url = npsUrl,
          response = okJson(responseJsonString).withHeader("correlationId", "nonMatching")
        )

        val result: Either[ErrorWrapper, ResponseWrapper[LeppPaymentDetails]] =
          await(connector.retrieveDetails(nino).value)

        WireMock.verify(getRequestedFor(urlEqualTo(npsUrl)))

        result mustBe a[Right[_, _]]
        result.getOrElse(ResponseWrapper(correlationId, LeppPaymentDetails(0, "Zero")))
          .responseData mustBe LeppPaymentDetails(1, "One")
      }
    }
  }
}
