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

package models.response

import base.SpecBase
import models.nps.retrieve.*
import play.api.libs.json.Json
import models.response.SummaryStatus.*

class GetSummaryResponseSpec extends SpecBase {
  "writes" - {
    "should produce the expected JSON when all fields are present" in {
      Json.toJson(getSummaryResponse) mustBe Json.parse(
        """
          |{
          | "status": "NO_ACTIONS",
          | "data": {
          |   "currentLowEarnersOptimisticLock":123,
          |   "identifier":"id",
          |   "lowEarnersDetailsList":[
          |     {
          |       "taxYear":11,
          |       "lowEarnersCalculations":[
          |         {
          |           "lowEarnersClaimDetails": {
          |             "claimSequenceNumber":123,
          |             "entitlementAmount":10.56,
          |             "claimStatus":"CANCELLED",
          |             "inSelfAssessment":true,
          |             "calculationDate":"2023-06-27",
          |             "claimDate":"2023-06-27",
          |             "reminderOutputSent":true,
          |             "reissueClaimOutput":true,
          |             "originalAmount":10.56
          |           },
          |           "lowEarnersDataDetails":{
          |             "responseTimestamp":"2023-06-27 09:12:28",
          |             "calculationSequenceNumber":123,
          |             "dataSourceMaster":"CESA",
          |             "netPayContributionsTotal":10.56,
          |             "basicRatePercentage":10.56,
          |             "totalAllowances":10.56,
          |             "totalIncome":10.56,
          |             "totalDeductions":10.56,
          |             "totalTaxDue":10.56
          |           }
          |         }
          |       ]
          |     }
          |   ]
          | }
          |}
        """.stripMargin
      )
    }
    
    "should produce the expected JSON for the minimum response" in {
      val model = GetSummaryResponse(status = NOT_ELIGIBLE, data = None)
      Json.toJson(model) mustBe Json.parse(
        """
          |{
          | "status": "NOT_ELIGIBLE"
          |}
        """.stripMargin
      )
    }
  }
  
  "apply" - {
    "should produce the expected model when no data exists" in {
      val response: RetrieveClaimsResponse = RetrieveClaimsResponse(1, "id", Nil)
      GetSummaryResponse.apply(response) mustBe GetSummaryResponse(NOT_ELIGIBLE, Some(response)) 
    }

    "should produce the expected model when only historic data exists" in {
      GetSummaryResponse.apply(retrieveResponse) mustBe getSummaryResponse
    }

    "should produce the expected model when available payments exist" in {
      val claimDetails: LowEarnersClaimDetails = LowEarnersClaimDetails(
        claimSequenceNumber = 123,
        entitlementAmount = Some(10.56),
        claimStatus = "PENDING",
        inSelfAssessment = true,
        calculationDate = Some("2023-06-27"),
        claimDate = Some("2023-06-27"),
        reminderOutputSent = true,
        reissueClaimOutput = true,
        originalAmount = Some(10.56)
      )

      val details: LowEarnersDetails = LowEarnersDetails(
        taxYear = 11,
        lowEarnersCalculations = Seq(
          calculation.copy(lowEarnersClaimDetails = claimDetails),
          calculation
        )
      )

      val availableResponse: RetrieveClaimsResponse = retrieveResponse.copy(
        lowEarnersDetailsList = Seq(details)
      )
      
      GetSummaryResponse.apply(availableResponse) mustBe GetSummaryResponse(
        status = PAYMENTS_AVAILABLE,
        data = Some(availableResponse)
      )
    }
    
    "should produce the expected model when suspended payments exist" in {
      val claimDetails: LowEarnersClaimDetails = LowEarnersClaimDetails(
        claimSequenceNumber = 123,
        entitlementAmount = Some(10.56),
        claimStatus = "SUSPENDED - RLS",
        inSelfAssessment = true,
        calculationDate = Some("2023-06-27"),
        claimDate = Some("2023-06-27"),
        reminderOutputSent = true,
        reissueClaimOutput = true,
        originalAmount = Some(10.56)
      )

      val details: LowEarnersDetails = LowEarnersDetails(
        taxYear = 11,
        lowEarnersCalculations = Seq(calculation.copy(lowEarnersClaimDetails = claimDetails))
      )

      val availableResponse: RetrieveClaimsResponse = retrieveResponse.copy(
        lowEarnersDetailsList = Seq(details)
      )

      GetSummaryResponse.apply(availableResponse) mustBe GetSummaryResponse(
        status = CHECK,
        data = Some(availableResponse)
      )
    }
  }

}
