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

package models.errors

import base.SpecBase
import play.api.libs.json.Json

class LeppErrorSpec extends SpecBase {
  "LeppError" -> {
    "writes should return the expected JSON" in {
      LeppError.writes.writes(LeppError("CODE", "Message")) mustBe Json.parse(
        """
          |{
          | "code": "CODE",
          | "message": "Message"
          |}
        """.stripMargin
      )
    }

    "genericWrites should return the expected JSON" in {
      LeppError.writes.writes(InternalLeppError) mustBe Json.parse(
        """
          |{
          | "code": "INTERNAL_SERVER_ERROR",
          | "message": "An internal server error occurred"
          |}
        """.stripMargin
      )
    }
  }

}
