package models.nps.accept

import play.api.libs.json.{Json, OFormat, OWrites}

case class AcceptLeppPaymentRequestBody(currentLowEarnersOptimisticLock: BigInt,
                                        lowEarnersAccountDetails: LowEarnersAccountDetails)

object AcceptLeppPaymentRequestBody {
  implicit val format: OFormat[AcceptLeppPaymentRequestBody] = Json.format[AcceptLeppPaymentRequestBody]
}
