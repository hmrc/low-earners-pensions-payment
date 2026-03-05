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

package models.bars

import base.SpecBase
import play.api.libs.json.{JsError, JsResult, JsSuccess, JsValue, Json}

class BarsRequestSpec extends SpecBase {

  private val testJson: JsValue = Json.parse(
    """
      |{
      | "subject": {
      |   "title": "Mr",
      |   "name": "Taxwell Payer",
      |   "firstName": "Taxwell",
      |   "lastName": "Payer"
      | },
      | "account": {
      |   "sortCode": "11-22-33",
      |   "accountNumber": "12345678"
      | }
      |}
    """.stripMargin)

  private val testModel: BarsRequest = BarsRequest(
    account = BarsAccount(
      sortCode = "11-22-33",
      accountNumber = "12345678"
    ),
    subject = BarsSubject(
      title = Some("Mr"),
      name = Some("Taxwell Payer"),
      firstName = Some("Taxwell"),
      lastName = Some("Payer")
    )
  )

  "BarsRequest" - {
    "when read from JSON" - {
      "should return a JsSuccess for valid JSON" in {
        val testFillerModel: BarsRequest = BarsRequest(
          account = BarsAccount(
            sortCode = "N/A",
            accountNumber = "N/A"
          ),
          subject = BarsSubject(
            title = None,
            name = None,
            firstName = None,
            lastName = None
          )
        )
        val jsResult: JsResult[BarsRequest] = testJson.validate[BarsRequest]
        jsResult shouldBe a[JsSuccess[_]]
        jsResult.getOrElse(testFillerModel) shouldBe testModel
      }

      "should return a JsError for invalid JSON" in {
        val jsResult: JsResult[BarsRequest] = Json.parse(
          """
            |{
            | "title": false
            |}
          """.stripMargin).validate[BarsRequest]
        jsResult shouldBe a[JsError]
      }
    }

    "when written to JSON" - {
      "should return expected JSON" in {
        val json: JsValue = Json.toJson(testModel)
        json shouldBe testJson
      }
    }
  }
}
