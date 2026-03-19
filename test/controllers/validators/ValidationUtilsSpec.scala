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

package controllers.validators

import base.SpecBase
import controllers.validators.ValidationUtils.*
import models.errors.SingleValidationError

class ValidationUtilsSpec extends SpecBase {
  "stripCharacters" - {
    "should remove any supplied characters from a string if they exist" in {
      stripCharacters("ABCDEFG", Seq("A")) shouldBe "BCDEFG"
    }
    
    "should remove all supplied characters from a string when multiple are supplied" in {
      stripCharacters("ABCDEFG", Seq("A", "B")) shouldBe "CDEFG"
    }
    
    "should do nothing if supplied characters are not in a string" in {
      stripCharacters("ABCDEFG", Seq("3", "4")) shouldBe "ABCDEFG"
    }

    "should remove leading and trailing whitespace" in {
      stripCharacters("    ABCDEFG    ", Nil) shouldBe "ABCDEFG"
    }
  }
  
  "assertMandatoryFieldExists" - {
    "should return field value when it exists" in {
      assertMandatoryFieldExists(Some("exists"), "/field") shouldBe Right("exists")
    }
    
    "should return an error when field does not exist" in {
      assertMandatoryFieldExists(None, "/field") shouldBe Left(SingleValidationError(
        code = "REQUEST_MISSING_MANDATORY_FIELD",
        path = "/field"
      ))
    }
  }
  
  "assertStringLengthValid" - {
    "should return 'None' when string length is within limits" in {
      assertStringLengthValid("value", 0, 99, "/field") shouldBe None
    }
    
    "return an error when field is not within expected limits" in {
      assertStringLengthValid("valueeeee", 0, 5, "/field") shouldBe Some(
        SingleValidationError(
          code = "REQUEST_PARAMETER_FAILED_LENGTH_VALIDATION",
          path = "/field"
        )
      )
    }
  }
  
  "assertStringMatchesPattern" - {
    "should return 'None' when field format is valid" in {
      assertStringMatchesPattern("value", "^[a-zA-Z]{5}$".r, "/field") shouldBe None
    }

    "should return an error when field format is invalid" in {
      assertStringMatchesPattern("12345", "^[a-zA-Z]{5}$".r, "/field") shouldBe
        Some(SingleValidationError("REQUEST_PARAMETER_FAILED_PATTERN_VALIDATION", "/field"))
    }
  }
  
  "assertStringFormatValid" - {
    "should return 'None' for a valid string" in {
      assertStringFormatValid("12345", 0, 99, "^[0-9]{0,99}$".r, "/field") shouldBe None
    }
    
    "should return an error if string length is invalid" in {
      assertStringFormatValid("12345", 0, 4, "^[0-9]{0,4}$".r, "/field") shouldBe Some(
        SingleValidationError(
          code = "REQUEST_PARAMETER_FAILED_LENGTH_VALIDATION",
          path = "/field"
        )
      )
    }

    "should return an error if string does not match pattern" in {
      assertStringFormatValid("12345", 0, 99, "^[a-z]{99}$".r, "/field") shouldBe Some(
        SingleValidationError(
          code = "REQUEST_PARAMETER_FAILED_PATTERN_VALIDATION",
          path = "/field"
        )
      )
    }
  }
}
