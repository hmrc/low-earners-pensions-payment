package models.response

import base.SpecBase
import play.api.libs.json.Json

class GetSummaryResponseSpec extends SpecBase with LeppSummaryFixtures {
  "writes" - {
    "should produce the expected JSON when all fields are present" in {
      Json.toJson(getSummaryResponse) mustBe Json.parse(
        """
          |{
          | "status": "PAYMENTS_AVAILABLE",
          | "details": [{
          |  "taxYear": 2025,
          |  "availableItems": [{
          |    "calculationDate": "-999999999-01-01",
          |    "claimDate": "+999999999-12-31",
          |    "entitlementAmount": 100.11
          |  }],
          |  "suspendedItems": [{
          |    "calculationDate": "-999999999-01-01",
          |    "claimDate": "+999999999-12-31",
          |    "entitlementAmount": 100.11
          |  }],
          |  "paidItems": [{
          |    "calculationDate": "-999999999-01-01",
          |    "claimDate": "+999999999-12-31",
          |    "entitlementAmount": 100.11
          |  }],
          |  "cancelledItems": [{
          |    "calculationDate": "-999999999-01-01",
          |    "claimDate": "+999999999-12-31",
          |    "entitlementAmount": 100.11
          |  }]
          | }]
          |}
        """.stripMargin
      )
    }
  }

}
