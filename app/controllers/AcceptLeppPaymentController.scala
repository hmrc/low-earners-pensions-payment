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

package controllers

import cats.data.EitherT
import com.google.inject.Singleton
import connectors.AcceptLeppPaymentConnector
import controllers.actions.IdentifierAction
import controllers.validators.AcceptLeppPaymentRequestValidator
import models.CorrelationId
import models.errors.{ErrorWrapper, ValidationError}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.HeaderKey.correlationIdKey
import utils.Logging
import utils.ErrorCodes._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AcceptLeppPaymentController @Inject()(cc: ControllerComponents,
                                            identify: IdentifierAction,
                                            validator: AcceptLeppPaymentRequestValidator,
                                            connector: AcceptLeppPaymentConnector)
                                           (implicit ec: ExecutionContext) extends BackendController(cc) with Logging {

  def acceptPayment(taxYear: String): Action[AnyContent] = identify.async { implicit request =>
    val methodLoggingContext: String = "acceptPayment"

    implicit val requestCorrelationId: CorrelationId = request.correlationId

    val result: EitherT[Future, ErrorWrapper, Result] =
      for {
        request <- EitherT.fromEither[Future](validator.validate(taxYear))
        response <- connector.acceptPayment(request)
      } yield {
        infoLog(s"[AcceptLeppPaymentController - $methodLoggingContext] ",
          s"Successfully accepted payment with correlationId $requestCorrelationId")
        Created(Json.toJson(response.responseData)).withHeaders(correlationIdKey -> response.correlationId.value)
      }

    result.leftMap { errorWrapper =>
      val errorResponse: Result = errorWrapper.error match {
        case error: ValidationError => BadRequest(Json.toJson(error))
        case error if error.code == CONFLICT_ERROR => Conflict(Json.toJson(error))
        case error => InternalServerError(Json.toJson(error))
      }

      errorResponse.withHeaders(correlationIdKey -> errorWrapper.correlationId.value)
    }.merge
  }
}
