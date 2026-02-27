package models.errors

import play.api.libs.json.{JsString, Json, OWrites}

enum ErrorResult(val source: String) {
  val status: Int
  val code: String
  
  case ServiceErrorResult(status: Int, code: String) extends ErrorResult("SERVICE")
  case DownstreamErrorResult(status: Int, code: String) extends ErrorResult("DOWNSTREAM")
}

object ErrorResult {
  implicit val writes: OWrites[ErrorResult] = (o: ErrorResult) => Json.obj(
    "code" -> JsString(o.code),
    "source" -> JsString(o.source)
  )
}
