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

package controllers.actions

import com.google.inject.{ImplementedBy, Inject, Singleton}
import config.AppConfig
import models.auth.{AuthorisedRequest, Nino, UserDetails}
import models.errors.ErrorResult
import models.errors.ErrorResult.ServiceErrorResult
import play.api.Logging
import play.api.http.Status.{INTERNAL_SERVER_ERROR, UNAUTHORIZED}
import play.api.mvc.*
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.{CorrelationIdHandler, CorrelationIdOptional, PtaEnrolmentKey}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[IdentifierActionImpl])
trait IdentifierAction extends ActionBuilder[AuthorisedRequest, AnyContent]

@Singleton
class IdentifierActionImpl @Inject()(override val authConnector: AuthConnector,
                                     config: AppConfig,
                                     val parser: BodyParsers.Default)
                                    (implicit override val executionContext: ExecutionContext)
  extends IdentifierAction with AuthorisedFunctions with Logging {
  val correlationIdHandler: CorrelationIdHandler = CorrelationIdOptional()
  
  def invokeBlock[A](request: Request[A],
                     block: AuthorisedRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

    def errorResponse(status: Int, code: String): Future[Result] = Future.successful(
      ServiceErrorResult(status = status, code = code).toResult
    )

    // TODO - We might need to check here that the 'nino' isn't a TRN or determine a way to check for TRN users otherwise
    correlationIdHandler.handleCorrelationId(request) { correlationId =>
      authorised(Enrolment(PtaEnrolmentKey.value))
        .retrieve(Retrievals.nino and Retrievals.confidenceLevel) {
          case Some(nino) ~ confidenceLevel =>
            if (confidenceLevel >= config.confidenceLevelMinimum) {
              block(AuthorisedRequest(request, correlationId, UserDetails(Nino(nino))))
            } else {
              errorResponse(UNAUTHORIZED, "INSUFFICIENT_CONFIDENCE_LEVEL")
            }
          case _ => errorResponse(UNAUTHORIZED, "NO_NINO_FOUND_FOR_USER")
        } recoverWith {
        case err: NoActiveSession =>
          logger.error(request.headers.headers.mkString(";"))
          logger.error(err.toString)
          errorResponse(UNAUTHORIZED, "NO_ACTIVE_SESSION")
        case _: InsufficientEnrolments => errorResponse(UNAUTHORIZED, "MISSING_PTA_ENROLMENT")
        case _: AuthorisationException => errorResponse(INTERNAL_SERVER_ERROR, "AUTHORISATION_FAILED")
      }
    }
  }
}
