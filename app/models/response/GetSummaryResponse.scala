package models.response

import play.api.libs.json.{Json, Writes}

case class GetSummaryResponse(status: SummaryStatus, details: Seq[SummaryDetailsItem])

object GetSummaryResponse {
  implicit val writes: Writes[GetSummaryResponse] = Json.writes[GetSummaryResponse]
}
