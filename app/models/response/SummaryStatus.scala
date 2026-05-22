package models.response

import play.api.libs.json.{JsString, Writes}

enum SummaryStatus {
  case NOT_ELIGIBLE, NO_ACTIONS, CHECK, PAYMENTS_AVAILABLE
}

object SummaryStatus {
  implicit val writes: Writes[SummaryStatus] = (o: SummaryStatus) => JsString(o.toString)
}