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
import models.errors.{ErrorWrapper, FormatRequestBodyError, FormatTaxYearError, LeppError, MissingRequestBodyError, RequestBodyNotJsonError}
import models.nps.accept.{AcceptLeppPaymentRequest, AcceptLeppPaymentRequestBody}
import models.requests.IdentifierRequest
import play.api.libs.json.{JsError, JsSuccess, JsValue}
import play.api.mvc.AnyContent

import scala.util.matching.Regex

@Singleton
class AcceptLeppPaymentRequestValidator {
  private type ValidationResult[R] = Either[LeppError, R]
  
  def validate[A](taxYear: String)
                 (implicit request: IdentifierRequest[AnyContent]): Either[ErrorWrapper, AcceptLeppPaymentRequest] = {
    
    val taxYearFormat: Regex = "^[0-9]{4}$".r
    val taxYearOrError: ValidationResult[BigInt] = if (taxYearFormat.matches(taxYear)) {
      Right(BigInt(taxYear))
    } else {
      Left(FormatTaxYearError)
    }
    
    val jsonOrError: ValidationResult[JsValue] = if (request.request.hasBody) {
      request.body.asJson.fold(Left(RequestBodyNotJsonError))(json => Right(json))
    } else {
      Left(MissingRequestBodyError)
    }
    
    def requestBodyModelOrError(json: JsValue) = json.validate[AcceptLeppPaymentRequestBody] match {
      case JsSuccess(value, path) => Right(value)
      case JsError(errors) => Left(FormatRequestBodyError(errors.map(_._1.toString).toSet))
    }

    def fieldErrorOpt(fieldPath: String, fieldValue: String, format: Regex): Option[String] =
      if (format.matches(fieldValue)) {
        None
      } else {
        Some(fieldPath)
      }
    
    def validateRequestBodyModel(model: AcceptLeppPaymentRequestBody): ValidationResult[AcceptLeppPaymentRequestBody] = {
      import model.lowEarnersAccountDetails._
      val accountDetailsPath: String = "/lowEarnersAccountDetails"
      val errPaths: Seq[String] = Seq(
        fieldErrorOpt(s"$accountDetailsPath/accountName", accountName, "^[0-9A-Za-z'&,\\\\=()/ -]{1,18}$".r),
        fieldErrorOpt(s"$accountDetailsPath/accountNumber", accountNumber, "^[0-9]{6,8}$".r),
        fieldErrorOpt(s"$accountDetailsPath/sortCode", sortCode, "^[0-9]{6}$".r),
        rollNumber.flatMap(rn => fieldErrorOpt(s"$accountDetailsPath/rollNumber", rn, "^[A-Z0-9]{1,18}$".r))
      ).flatten
      
      if (errPaths.isEmpty) Right(model) else Left(FormatRequestBodyError(errPaths.toSet))
    }
    
    val result: Either[LeppError, AcceptLeppPaymentRequest] = for {
      taxYear <- taxYearOrError
      json <- jsonOrError
      requestBody <- requestBodyModelOrError(json)
      validatedBody <- validateRequestBodyModel(requestBody)
    } yield AcceptLeppPaymentRequest(request.user.nino, taxYear, validatedBody)

    result.fold(
      err => Left(ErrorWrapper(request.correlationId, err)),
      success => Right(success)
    )
  }
}
