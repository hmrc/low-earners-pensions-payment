package models.nps.accept

import base.SpecBase
import play.api.libs.json.*

class LowEarnersAccountDetailsSpec extends SpecBase {
  "LowEarnersAccountDetails" - {
    val model: LowEarnersAccountDetails = LowEarnersAccountDetails(
      accountName = "Name", accountNumber = "12345678", sortCode = "123456", rollNumber = Some("roll")
    )
    val validJson: JsValue = Json.parse(
      """{
        | "accountName": "Name",
        | "accountNumber": "12345678",
        | "sortCode": "123456",
        | "rollNumber": "roll"
        |}""".stripMargin
    )
    "reads" - {
      "should return the expected model for valid JSON" in {
        validJson.validate[LowEarnersAccountDetails] mustBe a[JsSuccess[_]]
        validJson.as[LowEarnersAccountDetails] mustBe model
      }

      "should return a JsError for invalid JSON" in {
        JsObject.empty.validate[LowEarnersAccountDetails] mustBe a[JsError]
      }
    }

    "writes" - {
      "should produce the expected JSON" in {
        Json.toJson(model) mustBe validJson
      }
    }
  }

}
