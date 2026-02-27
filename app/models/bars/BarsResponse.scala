package models.bars

import play.api.libs.json.{Json, OFormat}

case class BarsResponse()

object BarsResponse {
  implicit val format: OFormat[BarsResponse] = Json.format[BarsResponse]
}
