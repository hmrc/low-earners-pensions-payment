package models.nps.accept

import base.SpecBase
import play.api.libs.json.{JsError, JsObject, JsSuccess, JsValue, Json}

class AcceptLeppPaymentResponseSpec extends SpecBase {
  "AcceptLeppPaymentResponse" - {
    val model: AcceptLeppPaymentResponse = AcceptLeppPaymentResponse(123)
    val validJson: JsValue = Json.parse("""{"updatedLowEarnersOptimisticLock": 123}""")
    "reads" - {
      "should return the expected model for valid JSON" in {
        validJson.validate[AcceptLeppPaymentResponse] mustBe a[JsSuccess[_]]
        validJson.as[AcceptLeppPaymentResponse] mustBe model
      }
      
      "should return a JsError for invalid JSON" in {
        JsObject.empty.validate[AcceptLeppPaymentResponse] mustBe a[JsError]
      }
    }
    
    "writes" - {
      "should produce the expected JSON" in {
        Json.toJson(model) mustBe validJson
      }
    }
  }

}
