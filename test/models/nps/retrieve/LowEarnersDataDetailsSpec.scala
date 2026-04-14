/*
 * Copyright 2026 HM Revenue & Customs
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

package models.nps.retrieve

import base.SpecBase
import play.api.libs.json.{JsError, JsObject, JsSuccess, Json}

class LowEarnersDataDetailsSpec extends SpecBase {
  "LowEarnersDataDetails" - {
    "reads" - {
      "should return a JsError when reading from invalid JSON" in {
        val json: JsObject = JsObject.empty
        json.validate[LowEarnersDataDetails] mustBe a[JsError]
      }
      
      "should return the expected model when reading from valid JSON" in {
        val json = Json.parse(
          """
            |{
            | "calculationSequenceNumber": 123,
            | "basicRatePercentage": 10.56,
            | "dataSourceMaster": "CESA",
            | "netPayContributionsTotal": 10.56,
            | "responseTimestamp": "2023-06-27 09:12:28",
            | "totalAllowances": 10.56,
            | "totalDeductions": 10.56,
            | "totalIncome": 10.56,
            | "totalTaxDue": 10.56
            |}
          """.stripMargin
        )
        
        json.validate[LowEarnersDataDetails] mustBe a[JsSuccess[_]]
        json.as[LowEarnersDataDetails] mustBe dataDetails
      }
      
    }
    
    "writes" - {
      "should produce the expected JSON" in {
        val json = Json.parse(
          """
            |{
            | "calculationSequenceNumber": 123,
            | "basicRatePercentage": 10.56,
            | "dataSourceMaster": "CESA",
            | "netPayContributionsTotal": 10.56,
            | "responseTimestamp": "2023-06-27 09:12:28",
            | "totalAllowances": 10.56,
            | "totalDeductions": 10.56,
            | "totalIncome": 10.56,
            | "totalTaxDue": 10.56
            |}
          """.stripMargin
        )
        
        Json.toJson(dataDetails) mustBe json
      }
    }
  }
}
