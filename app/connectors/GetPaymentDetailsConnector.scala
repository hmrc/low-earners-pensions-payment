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

import cats.data.EitherT
import config.AppConfig
import models.{CorrelationId, ResponseWrapper}
import models.errors.ErrorWrapper
import models.nps.retrieve.RetrieveClaimsResponse
import play.api.http.HeaderNames.AUTHORIZATION
import play.api.http.Status.*
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import utils.ErrorCodes.*
import utils.HeaderKey.*
import utils.Logging

import java.net.URI
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class GetPaymentDetailsConnector @Inject()(val config: AppConfig, val http: HttpClientV2)
    extends BaseNpsConnector[RetrieveClaimsResponse]
    with Logging {

  def retrieveDetails(nino: String)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext,
    correlationId: CorrelationId
  ): ConnectorResult[RetrieveClaimsResponse] = {
    val retrieveUrl = s"${config.getPaymentDetailsUrl}/$nino/calculation-results"

    val methodLoggingContext: String = "retrieveDetails"

    EitherT(
      http
        .get(URI.create(retrieveUrl).toURL)
        .setHeader(
          (correlationIdKey, correlationId.value),
          (govUkOriginatorIdKey, config.govUkOriginatorId),
          (AUTHORIZATION, authorization()),
          (ENVIRONMENT, config.npsEnv)
        )
        .execute[Either[ErrorWrapper, ResponseWrapper[RetrieveClaimsResponse]]]
    ).bimap(
      err => {
        val resultCorrelationId: CorrelationId = checkIdsMatch(
          requestCorrelationId = correlationId,
          responseCorrelationId = err.correlationId,
          extraLoggingContext = Some(methodLoggingContext)
        )
        err.copy(correlationId = resultCorrelationId)
      },
      resp => {
        val resultCorrelationId = checkIdsMatch(correlationId, resp.correlationId, Some(methodLoggingContext))
        resp.copy(correlationId = resultCorrelationId)
      }
    )
  }

  override protected[connectors] val errorMap: Map[Int, String] = Map(
    BAD_REQUEST -> BAD_REQUEST_ERROR,
    FORBIDDEN -> NOT_FOUND_ERROR,
    NOT_FOUND -> NOT_FOUND_ERROR,
    UNPROCESSABLE_ENTITY -> NOT_FOUND_ERROR,
    INTERNAL_SERVER_ERROR -> INTERNAL_ERROR,
    SERVICE_UNAVAILABLE -> SERVICE_UNAVAILABLE_ERROR
  )
}
