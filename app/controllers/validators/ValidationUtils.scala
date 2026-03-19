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

import models.errors.{SingleValidationError, ValidationError}

import scala.annotation.tailrec
import scala.util.matching.Regex

object ValidationUtils {

  @tailrec
  def stripCharacters(field: String, characters: Seq[String]): String = characters match {
    case Nil => field.strip()
    case head :: tail => stripCharacters(field.replace(head, ""), tail)
  }
  
  protected[validators] def assertMandatoryFieldExists[T](fieldOpt: Option[T],
                                                          path: String): Either[ValidationError, T] =
    fieldOpt.fold(
      Left[ValidationError, T](
        SingleValidationError(
          code = "REQUEST_MISSING_MANDATORY_FIELD",
          path = path
        )
      ).withRight
    )(field => Right[ValidationError, T](field))

  protected[validators] def assertStringLengthValid(field: String,
                                                    minLength: Int,
                                                    maxLength: Int,
                                                    path: String): Option[SingleValidationError] = {
    val fieldLengthMessage: String = "REQUEST_PARAMETER_FAILED_LENGTH_VALIDATION"
    val length = field.length
    if (length >= minLength && length <= maxLength) None else Some(SingleValidationError(fieldLengthMessage, path))
  }
  
  protected[validators] def assertStringMatchesPattern(field: String,
                                                       format: Regex,
                                                       path: String): Option[SingleValidationError] = {
    val fieldFormatMessage: String = "REQUEST_PARAMETER_FAILED_PATTERN_VALIDATION"
    if (format.matches(field)) None else Some(SingleValidationError(fieldFormatMessage, path))
  }

  def assertStringFormatValid(field: String,
                              minLength: Int,
                              maxLength: Int,
                              regex: Regex,
                              path: String): Option[ValidationError] =
    assertStringLengthValid(field, minLength, maxLength, path) orElse
      assertStringMatchesPattern(field, regex, path)
}
