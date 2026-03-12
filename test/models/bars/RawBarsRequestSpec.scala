package models.bars

import base.SpecBase
import play.api.libs.json.{JsError, JsObject, JsResult, JsSuccess, JsValue, Json}

class RawBarsRequestSpec extends SpecBase {
  "RawBarsRequest" - {
    "should return a JsSuccess when read from valid JSON" in {
      val json: JsValue = Json.parse(
        """
          |{
          | "name": "Taxwell Payer",
          | "sortCode": "11-22-33",
          | "accountNumber": "12345678",
          | "rollNumber": "rollNumber"
          |}
        """.stripMargin 
      )
      
      val result: JsResult[RawBarsRequest] = json.validate[RawBarsRequest] 
      result shouldBe a[JsSuccess[_]]
      result.getOrElse(RawBarsRequest(None, None, None, None))
    }

    "should return a JsError when read from invalid JSON" in {
      val json: JsValue = JsObject.empty
      val result: JsResult[RawBarsRequest] = json.validate[RawBarsRequest]
      result shouldBe a[JsError]
    }
  }
}
