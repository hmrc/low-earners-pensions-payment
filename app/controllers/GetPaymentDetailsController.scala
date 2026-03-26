/*
 * Copyright 2024 HM Revenue & Customs
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

import connectors.GetPaymentDetailsConnector
import controllers.actions.IdentifierAction
import controllers.requests.CorrelationId
import play.api.libs.json.*
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.ErrorCodes.*
import utils.HeaderKey.correlationIdKey
import utils.Logging

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class GetPaymentDetailsController @Inject()(
  cc: ControllerComponents,
  identify: IdentifierAction,
  connector: GetPaymentDetailsConnector
)(implicit ec: ExecutionContext)
    extends BackendController(cc)
    with Logging {

  def getPaymentDetails: Action[AnyContent] = identify.async { implicit request =>
    val methodLoggingContext: String = "getPaymentDetails"

    implicit val requestCorrelationId: CorrelationId = request.correlationId

    def idLogString: String = correlationIdLogString(requestCorrelationId)

    val result =
      for {
        response <- connector.retrieveDetails(request.user.nino.value)
      } yield {
        infoLog(s"[GetPaymentDetailsController - $methodLoggingContext] ", s"Successfully received payment details with correlationId $idLogString")
        Ok(Json.toJson(response.responseData)).withHeaders(correlationIdKey -> response.correlationId.value)
      }

    result.leftMap { errorWrapper =>

      val errorResponse = errorWrapper.error.code match {
        case BAD_REQUEST_ERROR => BadRequest(Json.toJson(errorWrapper.error))
        case NOT_FOUND_ERROR | NO_MATCH_ERROR | EMPTY_DATA_ERROR => NotFound(Json.toJson(errorWrapper.error))
        case FORBIDDEN_ERROR => Forbidden(Json.toJson(errorWrapper.error))
        case _ => InternalServerError(Json.toJson(errorWrapper.error))
      }

      errorResponse.withHeaders(correlationIdKey -> errorWrapper.correlationId.value)
    }.merge
  }
}
