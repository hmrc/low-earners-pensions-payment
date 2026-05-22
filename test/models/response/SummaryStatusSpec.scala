package models.response

import base.SpecBase
import play.api.libs.json.{JsString, Json}
import models.response.SummaryStatus._

class SummaryStatusSpec extends SpecBase {
  "writes" - {
    def enumTest(status: SummaryStatus, expectedJsString: String): Unit = s"must map correctly for status: $status" - {
      Json.toJson(status) mustBe JsString(expectedJsString)
    }
      
    Seq(
      NOT_ELIGIBLE -> "NOT_ELIGIBLE",
      NO_ACTIONS -> "NO_ACTIONS",
      CHECK -> "CHECK",
      PAYMENTS_AVAILABLE -> "PAYMENTS_AVAILABLE"
    ).foreach(enumTest)
  }
}
