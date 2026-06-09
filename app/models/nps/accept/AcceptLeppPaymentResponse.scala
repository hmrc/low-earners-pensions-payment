package models.nps.accept

import play.api.libs.json.{Json, OFormat}

case class AcceptLeppPaymentResponse(updatedLowEarnersOptimisticLock: BigInt)

object AcceptLeppPaymentResponse {
  implicit val format: OFormat[AcceptLeppPaymentResponse] = Json.format[AcceptLeppPaymentResponse]
}
