package models.response

import models.nps.retrieve.{LowEarnersCalculation, RetrieveClaimsResponse}
import play.api.libs.json.{Json, Writes}

case class GetSummaryResponse(status: SummaryStatus, data: Option[RetrieveClaimsResponse])

object GetSummaryResponse {
  def apply(data: RetrieveClaimsResponse): GetSummaryResponse = {
    val availableItemsOpt: Option[Seq[LowEarnersCalculation]] = data.filterByStatus("PENDING")
    val suspendedItemsOpt: Option[Seq[LowEarnersCalculation]] = data.filterByStatus("SUSPENDED - RLS")
    val paidItemsOpt: Option[Seq[LowEarnersCalculation]] = data.filterByStatus("PAID")
    val cancelledItemsOpt: Option[Seq[LowEarnersCalculation]] = data.filterByStatus("CANCELLED")
    
    val status: SummaryStatus = (paidItemsOpt, cancelledItemsOpt, availableItemsOpt, suspendedItemsOpt) match {
      case (None, None, None, None) => SummaryStatus.NOT_ELIGIBLE
      case (_, _, None, None) => SummaryStatus.NO_ACTIONS
      case (_, _, Some(_), _) => SummaryStatus.PAYMENTS_AVAILABLE
      case (_, _, None, Some(_)) => SummaryStatus.CHECK
    }

    GetSummaryResponse(
      status = status,
      data = Some(data)
    )
  }
  
  implicit val writes: Writes[GetSummaryResponse] = Json.writes[GetSummaryResponse]
}
