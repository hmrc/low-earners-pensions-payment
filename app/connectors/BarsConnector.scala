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

import models.bars.{ValidatedBarsRequest, BarsResponse}
import com.google.inject.{Inject, Singleton}
import config.AppConfig
import models.{CorrelationId, ResponseWrapper}
import models.ResponseWrapper.{ErrorWrapper, SuccessWrapper}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import play.api.libs.ws.WSBodyWritables.writeableOf_JsValue
import cats.data.EitherT
import connectors.httpHandlers.BarsHttpHandler

import java.net.URI
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BarsConnector @Inject()(config: AppConfig,
                              httpClient: HttpClientV2) extends BarsHttpHandler {

  def checkBankAccountDetails(request: ValidatedBarsRequest, correlationId: CorrelationId)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext): ConnectorResponse[BarsResponse] =
    EitherT(
      httpClient
        .post(URI.create(config.barsUrl).toURL)
        .withBody(Json.toJson(request))
        .execute[DownstreamResponse[BarsResponse]] 
    )
}
