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

import play.api.libs.json.{JsString, Json, OWrites}

sealed case class LeppError(
  code: String,
  message: String
)

object LeppError {
  implicit def writes[T <: LeppError]: OWrites[T] = (o: T) =>
    Json.obj(
      "code" -> JsString(o.code),
      "message" -> JsString(o.message)
    )
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

object NotFoundError
  extends LeppError(
    code = "NOT_FOUND",
    message = "Details not found"
  )