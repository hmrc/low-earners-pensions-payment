package models.response

import play.api.libs.json.{Json, Writes}

import java.time.LocalDate

case class PaymentDetailsItem(calculationDate: Option[LocalDate],
                              claimDate: Option[LocalDate],
                              entitlementAmount: Option[BigDecimal])

object PaymentDetailsItem {
  implicit val writes: Writes[PaymentDetailsItem] = Json.writes[PaymentDetailsItem]
}