package models.response

import play.api.libs.json.{Json, Writes}

case class SummaryDetailsItem(taxYear: Int,
                              availableItems: Option[Seq[PaymentDetailsItem]],
                              suspendedItems: Option[Seq[PaymentDetailsItem]],
                              paidItems: Option[Seq[PaymentDetailsItem]],
                              cancelledItems: Option[Seq[PaymentDetailsItem]])

object SummaryDetailsItem {
  implicit val writes: Writes[SummaryDetailsItem] = Json.writes[SummaryDetailsItem]
}