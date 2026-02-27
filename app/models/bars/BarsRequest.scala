package models.bars

import play.api.libs.json.{Json, OFormat}

case class BarsRequest()

object BarsRequest {
  implicit val format: OFormat[BarsRequest] = Json.format[BarsRequest]
}
