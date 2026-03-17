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

package models.errors

import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.libs.json.*

enum ErrorResult(val source: String) {
  val status: Int
  val code: String
  val pathsOpt: Option[Set[String]]
  val errorsOpt: Option[Seq[ErrorResult]]

  case ServiceErrorResult(status: Int,
                          code: String,
                          pathsOpt: Option[Set[String]] = None,
                          errorsOpt: Option[Seq[ServiceErrorResult]] = None) extends ErrorResult("SERVICE")

  case BarsErrorResult(status: Int,
                       code: String,
                       pathsOpt: Option[Set[String]] = None,
                       errorsOpt: Option[Seq[BarsErrorResult]] = None) extends ErrorResult("BARS")

  case NpsErrorResult(status: Int,
                      code: String,
                      apiName: String,
                      pathsOpt: Option[Set[String]] = None,
                      errorsOpt: Option[Seq[NpsErrorResult]] = None) extends ErrorResult(s"NPS - $apiName")
}

object ErrorResult {
  protected[errors] def baseWrites(o: ErrorResult): JsObject = Json.obj(
    "code" -> JsString(o.code),
    "source" -> JsString(o.source)
  ) ++ o.pathsOpt
    .fold(JsObject.empty)(somePaths =>
      if (somePaths.isEmpty) {
        JsObject.empty
      } else {
        Json.obj("paths" -> JsArray(somePaths.map(JsString(_)).toSeq))
      }
    )

  implicit val writes: OWrites[ErrorResult] = (o: ErrorResult) =>
    baseWrites(o) ++
      o.errorsOpt
        .fold(JsObject.empty)(someErrors => {
          if (someErrors.isEmpty) {
            JsObject.empty
          } else {
            Json.obj("errors" -> JsArray(someErrors.map(baseWrites)))
          }
        })

  val failedToParseError: ErrorResult = ServiceErrorResult(INTERNAL_SERVER_ERROR, "FAILED_TO_PARSE_DOWNSTREAM_RESPONSE")
  val internalError: ErrorResult = ServiceErrorResult(INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR")
}
