package models.bars

import play.api.libs.json.{Json, Reads}

case class RawBarsRequest(name: Option[String],
                          sortCode: Option[String],
                          accountNumber: Option[String],
                          rollNumber: Option[String])

object RawBarsRequest {
  implicit val reads: Reads[RawBarsRequest] = Json.reads[RawBarsRequest]
}
