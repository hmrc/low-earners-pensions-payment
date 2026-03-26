/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models.response

import base.UnitBaseSpec
import play.api.libs.json.*

class LeppPaymentDetailsSpec extends UnitBaseSpec {
  val testModel: LeppPaymentDetails = LeppPaymentDetails(
    currentLowEarnersOptimisticLock = 1,
    identifier = "One"
  )

  val testJson: JsValue = Json.parse(
    """
      |{
      | "currentLowEarnersOptimisticLock": 1,
      | "identifier": "One"
      |}
    """.stripMargin
  )

  "writes" -> {
    "should return the expected JSON" in {
      Json.toJson(testModel) mustBe testJson
    }
  }

  "reads" -> {
    "return a JsError when reading from invalid JSON" in {
      JsObject.empty.validate[LeppPaymentDetails] mustBe a[JsError]
    }

    "return a JsSuccess when reading from valid JSON" in {
      val result = testJson.validate[LeppPaymentDetails]
      result mustBe a[JsSuccess[_]]
      result.get mustBe testModel
    }
  }
}
