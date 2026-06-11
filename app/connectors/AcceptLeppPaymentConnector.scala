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

import com.google.inject.Singleton
import config.AppConfig
import models.errors.ErrorWrapper
import models.nps.accept.{AcceptLeppPaymentRequest, AcceptLeppPaymentResponse}
import models.{CorrelationId, ResponseWrapper}
import play.api.http.HeaderNames.AUTHORIZATION
import play.api.http.Status.*
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import utils.ErrorCodes.*
import utils.HeaderKey.{ENVIRONMENT, correlationIdKey, govUkOriginatorIdKey}
import utils.Logging

import java.net.URI
import javax.inject.Inject
import scala.concurrent.ExecutionContext

@Singleton
class AcceptLeppPaymentConnector @Inject()(val config: AppConfig, val http: HttpClientV2)
  extends BaseNpsConnector[AcceptLeppPaymentResponse]
    with Logging {

  override val successStatus: Int = CREATED

  def acceptPayment(request: AcceptLeppPaymentRequest)
                   (implicit hc: HeaderCarrier,
                    ec: ExecutionContext,
                    correlationId: CorrelationId): ConnectorResult[AcceptLeppPaymentResponse] = {
    val methodLoggingContext: String = "acceptPayment"
    val acceptPaymentUrl = s"${config.npsUrl}/${request.identifier}/tax-year/${request.taxYear}/payment-claims"
    
    handleConnectorResult(methodLoggingContext)(
      http
        .post(URI.create(acceptPaymentUrl).toURL)
        .setHeader(
          (correlationIdKey, correlationId.value),
          (govUkOriginatorIdKey, config.govUkOriginatorId),
          (AUTHORIZATION, authorization()),
          (ENVIRONMENT, config.npsEnv)
        )
        .withBody(Json.toJson(request.body))
        .execute[Either[ErrorWrapper, ResponseWrapper[AcceptLeppPaymentResponse]]]
    )
  }

  override protected[connectors] val errorMap: Map[Int, String] = Map(
    BAD_REQUEST -> INTERNAL_ERROR,
    FORBIDDEN -> INTERNAL_ERROR,
    NOT_FOUND -> INTERNAL_ERROR,
    CONFLICT -> CONFLICT_ERROR,
    UNPROCESSABLE_ENTITY -> INTERNAL_ERROR,
    INTERNAL_SERVER_ERROR -> INTERNAL_ERROR,
    SERVICE_UNAVAILABLE -> INTERNAL_ERROR
  )
}
