
package models.nps.accept

import base.SpecBase
import play.api.libs.json.*

class AcceptLeppPaymentRequestBodySpec extends SpecBase {
  "AcceptLeppPaymentRequestBody" - {
    val accountDetails: LowEarnersAccountDetails = LowEarnersAccountDetails(
      accountName = "Name", accountNumber = "12345678", sortCode = "123456", rollNumber = Some("roll")
    )
    val model = AcceptLeppPaymentRequestBody(
      currentLowEarnersOptimisticLock = 1234, lowEarnersAccountDetails = accountDetails
    )
    val validJson: JsValue = Json.parse(
      """{
        | "currentLowEarnersOptimisticLock": 1234,
        | "lowEarnersAccountDetails": {    
        |   "accountName": "Name",
        |   "accountNumber": "12345678",
        |   "sortCode": "123456",
        |   "rollNumber": "roll"
        | }
        |}""".stripMargin
    )

    "reads" - {
      "should return the expected model for valid JSON" in {
        validJson.validate[AcceptLeppPaymentRequestBody] mustBe a[JsSuccess[_]]
        validJson.as[AcceptLeppPaymentRequestBody] mustBe model
      }

      "should return a JsError for invalid JSON" in {
        JsObject.empty.validate[AcceptLeppPaymentRequestBody] mustBe a[JsError]
      }
    }


    "writes" - {
      "should produce the expected JSON" in {
        Json.toJson(model) mustBe validJson
      }
    }
  }

}
