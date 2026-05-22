package models.response

import base.SpecBase
import play.api.libs.json.Json

class PaymentDetailsItemSpec extends SpecBase with LeppSummaryFixtures {
  "writes" - {
    "should produce the expected JSON when all fields are present" in {
      Json.toJson(paymentDetailsItem) mustBe Json.parse(
        """
          |{
          | "calculationDate": "-999999999-01-01",
          | "claimDate": "+999999999-12-31",
          | "entitlementAmount": 100.11
          |}
        """.stripMargin
      )
    }
  }
}
