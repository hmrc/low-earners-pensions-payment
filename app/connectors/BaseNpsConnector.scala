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
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import config.AppConfig
import models.{CorrelationId, ResponseWrapper}
import models.errors.*
import play.api.http.Status.*
import play.api.libs.json.*
import uk.gov.hmrc.http.{HttpErrorFunctions, HttpReads, HttpResponse}
import utils.Logging
import utils.HeaderKey.correlationIdKey

import java.util.Base64
import scala.concurrent.{ExecutionContext, Future}

abstract class BaseNpsConnector[Resp: Reads] extends HttpErrorFunctions { this: Logging =>
  val config: AppConfig
  val successStatus: Int = OK

  private def retrieveCorrelationId(response: HttpResponse): CorrelationId = CorrelationId(
    response.header(correlationIdKey).getOrElse("N/A")
  )

  protected[connectors] def authorization(): String = {
    val clientId = config.npsClientId
    val secret = config.npsSecret

    val encoded = Base64.getEncoder.encodeToString(s"$clientId:$secret".getBytes("UTF-8"))
    s"Basic $encoded"
  }

  private type ReadsResponse[Wrapped] = Either[ErrorWrapper, ResponseWrapper[Wrapped]]

  protected[connectors] val errorMap: Map[Int, String]

  protected[connectors] def checkIdsMatch(
    requestCorrelationId: CorrelationId,
    responseCorrelationId: CorrelationId,
    extraLoggingContext: Option[String]
  ): CorrelationId = {
    if (requestCorrelationId.value != responseCorrelationId.value) {
      logger.error(s"checkIdsMatch - $extraLoggingContext : Correlation ID was either missing from response, or did not match ID from request. " +
          "Reverting to ID from request for consistency in logs. Be aware of potential ID inconsistencies. " +
          s"Request C-ID: ${requestCorrelationId.value}, Response C-ID: ${responseCorrelationId.value}"
      )
    }

    requestCorrelationId
  }

  implicit def httpReads: HttpReads[ReadsResponse[Resp]] = (method: String, url: String, response: HttpResponse) => {
    val methodLoggingContext: String = "httpReads"
    val correlationId: CorrelationId = retrieveCorrelationId(response)

    if (response.status == successStatus) {
        jsonValidation[Resp](response.body, correlationId, Some(methodLoggingContext))
    } else {
      Left(handleErrorResponse(method, url, response, correlationId, Some(methodLoggingContext)))
    }
  }

  protected[connectors] def jsonValidation[Rds: Reads](
    body: String,
    correlationId: CorrelationId,
    extraContext: Option[String]
  ): ReadsResponse[Rds] = {
    val methodLoggingContext: String = "[jsonValidation]"

    try {
      val responseJson: JsValue = Json.parse(body)

      responseJson.validate[Rds] match {
        case JsSuccess(value, _) =>
          Right[ErrorWrapper, ResponseWrapper[Rds]](ResponseWrapper(correlationId, value)).withLeft
        case JsError(errors) =>
          logger.error(s"$extraContext - $methodLoggingContext: Json validation failed")
          Left(ErrorWrapper(correlationId, InternalLeppError))
      }
    } catch {
      case _: JsonParseException =>
        Left(ErrorWrapper(correlationId, InternalLeppError))
      case _: JsonMappingException =>
        Left(ErrorWrapper(correlationId, InternalLeppError))
    }
  }

  protected[connectors] def handleErrorResponse(
    httpMethod: String,
    url: String,
    response: HttpResponse,
    correlationId: CorrelationId,
    extraContext: Option[String]
  ): ErrorWrapper = {
    val methodLoggingContext: String = "handleErrorResponse"

    errorMap.get(response.status) match {
      case Some(errorCode) =>
        val errorMessage: String = upstreamResponseMessage(
          verbName = httpMethod,
          url = url,
          status = response.status,
          responseBody = ""
        )
        logger.error(s"$methodLoggingContext - $extraContext :: $errorMessage with correlationId - ${correlationId.value}")
        ErrorWrapper(correlationId, LeppError(errorCode, errorMessage))
      case None =>
        ErrorWrapper(correlationId, UnexpectedStatusError)
    }
  }
  
  def handleConnectorResult(methodLoggingContext: String)
                           (result: Future[Either[ErrorWrapper, ResponseWrapper[Resp]]])
                           (implicit reqCid: CorrelationId, ec: ExecutionContext): ConnectorResult[Resp] = {
    EitherT(result).bimap(
      err => {
        val resultCorrelationId: CorrelationId = checkIdsMatch(
          requestCorrelationId = reqCid,
          responseCorrelationId = err.correlationId,
          extraLoggingContext = Some(methodLoggingContext)
        )
        err.copy(correlationId = resultCorrelationId)
      },
      resp => {
        val resultCorrelationId = checkIdsMatch(reqCid, resp.correlationId, Some(methodLoggingContext))
        resp.copy(correlationId = resultCorrelationId)
      }
    )
  }
}
