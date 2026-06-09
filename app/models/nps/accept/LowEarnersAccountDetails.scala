package models.nps.accept

import play.api.libs.json.{Json, OFormat}

case class LowEarnersAccountDetails(accountName: String,
                                    accountNumber: String,
                                    sortCode: String,
                                    rollNumber: Option[String])

object LowEarnersAccountDetails {
  implicit val format: OFormat[LowEarnersAccountDetails] = Json.format[LowEarnersAccountDetails]
}
