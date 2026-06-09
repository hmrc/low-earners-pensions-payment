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

import cats.data.EitherT
import connectors.GetPaymentDetailsConnector
import controllers.actions.IdentifierAction
import controllers.requests.CorrelationId
import models.errors.ErrorWrapper
import models.nps.retrieve.RetrieveClaimsResponse
import models.requests.IdentifierRequest
import models.response.{GetSummaryResponse, ResponseWrapper}
import models.response.SummaryStatus.NOT_ELIGIBLE
import play.api.libs.json.*
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.ErrorCodes.*
import utils.HeaderKey.correlationIdKey
import utils.Logging

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

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
    
    getLeppData(methodLoggingContext)(
      dataMap = response => successResponseToResult(response),
      errorMap = errorWrapper => {
        val errorResponse: Result = errorWrapper.error.code match {
          case BAD_REQUEST_ERROR => BadRequest(Json.toJson(errorWrapper.error))
          case NOT_FOUND_ERROR => NotFound(Json.toJson(errorWrapper.error))
          case _ => InternalServerError(Json.toJson(errorWrapper.error))
        }
        errorResponse.withHeaders(correlationIdKey -> errorWrapper.correlationId.value)
      }
    )
  }

  def getLeppSummary: Action[AnyContent] = identify.async { implicit request =>
    val methodLoggingContext: String = "getPtaSummary"

    getLeppData(methodLoggingContext)(
      dataMap = response => successResponseToResult(response.map(GetSummaryResponse(_))),
      errorMap = errorWrapper => {
        val errorResponse: Result = errorWrapper.error.code match {
          case BAD_REQUEST_ERROR => BadRequest(Json.toJson(errorWrapper.error))
          case NOT_FOUND_ERROR => Ok(Json.toJson(GetSummaryResponse(NOT_ELIGIBLE, None)))
          case _ => InternalServerError(Json.toJson(errorWrapper.error))
        }
        errorResponse.withHeaders(correlationIdKey -> errorWrapper.correlationId.value)
      }
    )
  }

  protected[controllers] def getLeppData[A](methodLoggingContext: String)
                                           (dataMap: ResponseWrapper[RetrieveClaimsResponse] => Result, 
                                            errorMap: ErrorWrapper => Result)
                                           (implicit request: IdentifierRequest[A]): Future[Result] = {
    implicit val requestCorrelationId: CorrelationId = request.correlationId

    val result: EitherT[Future, ErrorWrapper, Result] = for {
      response <- connector.retrieveDetails(request.user.nino.value)
    } yield {
      infoLog(
        s"[GetPaymentDetailsController - $methodLoggingContext] ",
        s"Successfully received payment details with correlationId $requestCorrelationId"
      )
      dataMap(response)
    }

    result.leftMap(errorMap).merge
  }
  
  protected[controllers] def successResponseToResult[A: Writes](wrappedResponse: ResponseWrapper[A]): Result = 
    Ok(
      Json.toJson(wrappedResponse.responseData)
    ).withHeaders(
      correlationIdKey -> wrappedResponse.correlationId.value
    )
}
