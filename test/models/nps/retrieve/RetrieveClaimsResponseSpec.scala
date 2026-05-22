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

class RetrieveClaimsResponseSpec extends SpecBase {
  "RetrieveClaimsResponse" - {
    "reads" - {
      "should return a JsError when reading from invalid JSON" in {
        val json: JsObject = JsObject.empty
        json.validate[RetrieveClaimsResponse] mustBe a[JsError]
      }

      "should return the expected model when reading from valid JSON" in {
        val json = Json.parse(
          """
            |{
            | "currentLowEarnersOptimisticLock": 123,
            | "identifier": "id",
            | "lowEarnersDetailsList": [
            |   {
            |     "taxYear": 11,
            |     "lowEarnersCalculations": [
            |       {
            |         "lowEarnersClaimDetails": {
            |           "claimSequenceNumber": 123,
            |           "calculationDate": "2023-06-27",
            |           "claimDate": "2023-06-27",
            |           "claimStatus": "CANCELLED",
            |           "entitlementAmount": 10.56,
            |           "inSelfAssessment": true,
            |           "originalAmount": 10.56,
            |           "reissueClaimOutput": true,
            |           "reminderOutputSent": true
            |         },
            |         "lowEarnersDataDetails": {
            |           "calculationSequenceNumber": 123,
            |           "basicRatePercentage": 10.56,
            |           "dataSourceMaster": "CESA",
            |           "netPayContributionsTotal": 10.56,
            |           "responseTimestamp": "2023-06-27 09:12:28",
            |           "totalAllowances": 10.56,
            |           "totalDeductions": 10.56,
            |           "totalIncome": 10.56,
            |           "totalTaxDue": 10.56
            |         }
            |       }
            |     ]
            |   }
            | ]
            |}
           """.stripMargin
        )

        json.validate[RetrieveClaimsResponse] mustBe a[JsSuccess[_]]
        json.as[RetrieveClaimsResponse] mustBe retrieveResponse
      }

    }

    "writes" - {
      "should produce the expected JSON" in {
        val json = Json.parse(
          """
            |{
            | "currentLowEarnersOptimisticLock": 123,
            | "identifier": "id",
            | "lowEarnersDetailsList": [
            |   {
            |     "taxYear": 11,
            |     "lowEarnersCalculations": [
            |       {
            |         "lowEarnersClaimDetails": {
            |           "claimSequenceNumber": 123,
            |           "calculationDate": "2023-06-27",
            |           "claimDate": "2023-06-27",
            |           "claimStatus": "CANCELLED",
            |           "entitlementAmount": 10.56,
            |           "inSelfAssessment": true,
            |           "originalAmount": 10.56,
            |           "reissueClaimOutput": true,
            |           "reminderOutputSent": true
            |         },
            |         "lowEarnersDataDetails": {
            |           "calculationSequenceNumber": 123,
            |           "basicRatePercentage": 10.56,
            |           "dataSourceMaster": "CESA",
            |           "netPayContributionsTotal": 10.56,
            |           "responseTimestamp": "2023-06-27 09:12:28",
            |           "totalAllowances": 10.56,
            |           "totalDeductions": 10.56,
            |           "totalIncome": 10.56,
            |           "totalTaxDue": 10.56
            |         }
            |       }
            |     ]
            |   }
            | ]
            |}
           """.stripMargin
        )

        Json.toJson(retrieveResponse) mustBe json
      }
    }
    
    "filterByStatus" - {
      "should return None when no items exist for a given status" in {
        retrieveResponse.filterByStatus("PENDING") mustBe None 
      }

      "should return a non-empty list when items exist for a given status" in {
        retrieveResponse.filterByStatus("CANCELLED") mustBe Some(List(calculation))
      }
    }
  }
}
