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

import com.google.inject.Singleton
import controllers.validators.ValidationUtils.*
import models.CorrelationId
import models.ResponseWrapper.ErrorWrapper
import models.bars.*
import models.errors.ErrorResult.ServiceErrorResult
import models.errors.{ErrorResult, ValidationError}
import play.api.http.Status.BAD_REQUEST
import play.api.libs.json.{JsError, JsSuccess, JsValue}

import scala.util.matching.Regex

@Singleton
class BarsRequestValidator {
  def validate(json: JsValue, correlationId: CorrelationId): Either[ErrorWrapper, ValidatedBarsRequest] = {
    json.validate[RawBarsRequest] match {
      case JsSuccess(value, path) =>
        val result: Either[ValidationError, BarsRequestWithMandatoryFields] = for {
          req <- assertFieldsExist(value)
          req2 <- assertFieldsFormat(req)
        } yield req2
        result.fold(
          error => Left(ErrorWrapper(error.toServiceErrorResult, correlationId)),
          validatedRequest => Right(ValidatedBarsRequest(
            account = BarsAccount(
              sortCode = validatedRequest.sortCode,
              accountNumber = validatedRequest.accountNumber,
              rollNumber = validatedRequest.rollNumber
            ),
            subject = BarsSubject(
              name = Some(validatedRequest.name)
            )
          ))
        )
      case JsError(errors) =>
        Left(ErrorWrapper(
          value = ServiceErrorResult(
            status = BAD_REQUEST,
            code = "COULD_NOT_PARSE_REQUEST_JSON",
            pathsOpt = Some(errors.map(_._1.toString).toSet)
          ),
          correlationId = correlationId
        ))
    }
  }

  private def assertFieldsExist(request: RawBarsRequest): Either[ValidationError, BarsRequestWithMandatoryFields] = {
    val nameOrError = assertMandatoryFieldExists(request.name, "/name")
    val accountNumberOrError = assertMandatoryFieldExists(request.accountNumber, "/accountNumber")
    val sortCodeOrError = assertMandatoryFieldExists(request.sortCode, "/sortCode")

    (nameOrError, accountNumberOrError, sortCodeOrError) match {
      case (Right(name), Right(accountNumber), Right(sortCode)) =>
        Right(BarsRequestWithMandatoryFields(
          name = name.trim,
          sortCode = stripCharacters(sortCode, Seq("-")),
          accountNumber = accountNumber.trim,
          rollNumber = request.rollNumber.map(_.trim)
        ))
      case _ => Left(
        Seq(nameOrError, accountNumberOrError, sortCodeOrError).collect {
          case Left(err) => err
        }.reduce((err1, err2) => err1.add(err2))
      )
    }
  }

  private def assertFieldsFormat(request: BarsRequestWithMandatoryFields): Either[ValidationError, BarsRequestWithMandatoryFields] = {
    val errs: Seq[ValidationError] = Seq(
      validateNameFormat(request.name),
      validateAccountNumberFormat(request.accountNumber),
      validateSortCodeFormat(request.sortCode),
      request.rollNumber.flatMap(validateRollNumberFormat)
    ).flatten

    errs match {
      case Nil => Right(request)
      case _ => Left(errs.reduce((err1, err2) => err1.add(err2)))
    }
  }

  private def validateNameFormat(name: String): Option[ValidationError] = {
    val path: String = "/name"
    val nameRegex: Regex = """^[a-zA-Z\-' ]{1,99}+$""".r
    assertStringFormatValid(field = name, maxLength = 99, minLength = 1, regex = nameRegex, path = path)
  }

  private def validateSortCodeFormat(sortCode: String): Option[ValidationError] = {
    val path: String = "/sortCode"
    val sortCodeRegex: Regex = """^[0-9]{6}+$""".r
    assertStringFormatValid(field = sortCode, maxLength = 99, minLength = 1, regex = sortCodeRegex, path = path)
  }

  private def validateAccountNumberFormat(accountNumber: String): Option[ValidationError] = {
    val path: String = "/accountNumber"
    val accountNumberRegex: Regex = """^[0-9]{6,8}+$""".r
    assertStringFormatValid(field = accountNumber, minLength = 1, maxLength = 99, regex = accountNumberRegex, path = path)
  }

  private def validateRollNumberFormat(rollNumber: String): Option[ValidationError] = {
    val path: String = "/rollNumber"
    val rollNumberRegex: Regex = """^[0-9a-zA-Z/\- ]{1,99}+$""".r
    assertStringFormatValid(field = rollNumber, minLength = 1, maxLength = 99, regex = rollNumberRegex, path = path)
  }
}
