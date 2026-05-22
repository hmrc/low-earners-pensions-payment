package models.response

import models.response.SummaryStatus.PAYMENTS_AVAILABLE

import java.time.LocalDate

trait LeppSummaryFixtures {
  val paymentDetailsItem: PaymentDetailsItem = PaymentDetailsItem(
    calculationDate = Some(LocalDate.MIN),
    claimDate = Some(LocalDate.MAX),
    entitlementAmount = Some(100.11)
  )

  val summaryDetailsItem: SummaryDetailsItem = SummaryDetailsItem(
    taxYear = 2025,
    availableItems = Some(Seq(paymentDetailsItem)),
    suspendedItems = Some(Seq(paymentDetailsItem)),
    paidItems = Some(Seq(paymentDetailsItem)),
    cancelledItems = Some(Seq(paymentDetailsItem))
  )
  
  val getSummaryResponse: GetSummaryResponse = GetSummaryResponse(
    status = PAYMENTS_AVAILABLE, 
    details = Seq(summaryDetailsItem)
  )
}
