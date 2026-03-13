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
import models.ResponseWrapper
import models.ResponseWrapper.ErrorWrapper
import models.bars.ValidatedBarsRequest
import models.errors.ErrorResult.ServiceErrorResult
import play.api.http.Status.BAD_REQUEST
import play.api.libs.json.{JsObject, JsValue, Json}

class BarsRequestValidatorSpec extends SpecBase {
  
  private trait Test {
    private val testValidator: BarsRequestValidator = new BarsRequestValidator
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
    
    lazy val result: Either[ResponseWrapper.ErrorWrapper, ValidatedBarsRequest] =
      testValidator.validate(json, testCorrelationId)
  }
  
  "BarsRequestValidator" - {
    "should return an error when required fields are missing from request body" in new Test {
      override val json: JsValue = JsObject.empty
      result shouldBe a[Left[_, _]]
      val error = ServiceErrorResult(
        BAD_REQUEST,
        "REQUEST_MISSING_MANDATORY_FIELD",
        Some(Set("/name", "/accountNumber", "/sortCode"))
      )
      result.swap.getOrElse(dummyServiceErrorWrapper) shouldBe ErrorWrapper(error, testCorrelationId)
    }
    
    "should return an error when account number field has incorrect format" in new Test {
      override val json: JsValue = Json.parse(
        """
          |{
          | "name": "Sir Taxwell Payer",
          | "sortCode": "11-11-11",
          | "accountNumber": "incorrect",
          | "rollNumber": "dunno"
          |}
      """.stripMargin
      )
      result shouldBe a[Left[_, _]]
      val error = ServiceErrorResult(BAD_REQUEST, "REQUEST_PARAMETER_FAILED_PATTERN_VALIDATION", Some(Set("/accountNumber")))
      result.swap.getOrElse(dummyServiceErrorWrapper) shouldBe ErrorWrapper(error, testCorrelationId)
    }
    
    "should return an error when sort code field has incorrect format" in new Test {
      override val json: JsValue = Json.parse(
        """
          |{
          | "name": "Sir Taxwell Payer",
          | "sortCode": "wrong",
          | "accountNumber": "12345678",
          | "rollNumber": "dunno"
          |}
        """.stripMargin
      )
      result shouldBe a[Left[_, _]]
      val error = ServiceErrorResult(BAD_REQUEST, "REQUEST_PARAMETER_FAILED_PATTERN_VALIDATION", Some(Set("/sortCode")))
      result.swap.getOrElse(dummyServiceErrorWrapper) shouldBe ErrorWrapper(error, testCorrelationId)
    }
    
    "should return an error when roll number field has incorrect format" in new Test {
      override val json: JsValue = Json.parse(
        """
          |{
          | "name": "Sir Taxwell Payer",
          | "sortCode": "11-11-11",
          | "accountNumber": "12345678",
          | "rollNumber": "!@£$%"
          |}
        """.stripMargin
      )
      result shouldBe a[Left[_, _]]
      val error = ServiceErrorResult(BAD_REQUEST, "REQUEST_PARAMETER_FAILED_PATTERN_VALIDATION", Some(Set("/rollNumber")))
      result.swap.getOrElse(dummyServiceErrorWrapper) shouldBe ErrorWrapper(error, testCorrelationId)
    }
    
    "should return an error when name field has incorrect format" in new Test {
      override val json: JsValue = Json.parse(
        """
          |{
          | "name": "1234",
          | "sortCode": "11-11-11",
          | "accountNumber": "12345678",
          | "rollNumber": "dunno"
          |}
        """.stripMargin
      )
      result shouldBe a[Left[_, _]]
      val error = ServiceErrorResult(BAD_REQUEST, "REQUEST_PARAMETER_FAILED_PATTERN_VALIDATION", Some(Set("/name")))
      result.swap.getOrElse(dummyServiceErrorWrapper) shouldBe ErrorWrapper(error, testCorrelationId)
    }
    
    "should handle multiple format errors" in new Test {
      override val json: JsValue = Json.parse(
        """
          |{
          | "name": "1",
          | "sortCode": "wrong",
          | "accountNumber": "2",
          | "rollNumber": "!@£$"
          |}
        """.stripMargin
      )
      result shouldBe a[Left[_, _]]
      val error = ServiceErrorResult(
        status = BAD_REQUEST,
        code = "REQUEST_PARAMETER_FAILED_PATTERN_VALIDATION",
        pathsOpt = Some(Set("/name", "/sortCode", "/accountNumber", "/rollNumber"))
      )
      result.swap.getOrElse(dummyServiceErrorWrapper) shouldBe ErrorWrapper(error, testCorrelationId)
    }
    
    "should return ValidatedBarsRequest for a valid request" in new Test {
      result shouldBe a[Right[_, _]]
      result.getOrElse(dummyValidatedBarsRequest) shouldBe testValidatedBarsRequest
    }
  }
}
