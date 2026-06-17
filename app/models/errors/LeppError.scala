/*
 * Copyright 2025 HM Revenue & Customs
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

package models.errors

import play.api.libs.json.{JsArray, JsObject, JsString, Json, OWrites}

sealed case class LeppError(
  code: String,
  message: String,
  pathsOpt: Option[Set[String]] = None
)

sealed class ValidationError(code: String,
                             message: String,
                             pathsOpt: Option[Set[String]] = None) extends LeppError(code, message, pathsOpt)

object LeppError {
  implicit def writes[T <: LeppError]: OWrites[T] = (o: T) => {
    val pathsJson: JsObject = o.pathsOpt.fold(JsObject.empty)(
      paths => Json.obj("paths" -> JsArray(paths.map(JsString(_)).toSeq))
    )
    
    Json.obj(
      "code" -> JsString(o.code),
      "message" -> JsString(o.message)
    ) ++ pathsJson
  }

}

object UnauthorisedError
    extends LeppError(
      code = "USER_NOT_AUTHORISED",
      message = "The client and/or agent is not authorised"
    )

object InvalidBearerTokenError
    extends LeppError(
      code = "UNAUTHORIZED",
      message = "Bearer token is missing or not authorized"
    )

object InternalLeppError
    extends LeppError(
      code = "INTERNAL_SERVER_ERROR",
      message = "An internal server error occurred"
    )

object MissingCorrelationIdError
    extends LeppError(
      code = "MISSING_CORRELATION_ID",
      message = "CorrelationId header could not be found in request"
    )

object UnexpectedStatusError
    extends LeppError(
      code = "UNEXPECTED_STATUS_ERROR",
      message = "An unexpected status code was returned from downstream"
    )

object FormatTaxYearError extends ValidationError(
  code = "TAX_YEAR_FORMAT_ERROR",
  message = "Tax year parameter failed request body validation"
)

object MissingRequestBodyError extends ValidationError(
  code = "MISSING_REQUEST_BODY_ERROR",
  message = "No request body was supplied"
)

object RequestBodyNotJsonError extends ValidationError(
  code = "REQUEST_BODY_NOT_JSON_ERROR",
  message = "Supplied request body could not be parsed to JSON"
)

class FormatRequestBodyError(paths: Set[String]) extends ValidationError(
  code = "REQUEST_BODY_FORMAT_ERROR",
  message = "Request body failed validation",
  pathsOpt = Some(paths)
)