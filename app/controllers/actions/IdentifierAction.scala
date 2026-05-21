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
import controllers.requests.{CorrelationId, RequestWithCorrelationId}
import models.errors.{InvalidBearerTokenError, MissingCorrelationIdError, UnauthorisedError}
import models.requests.{AuthUser, IdentifierRequest}
import play.api.libs.json.Json
import play.api.mvc.*
import play.api.mvc.Results.{InternalServerError, Unauthorized}
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.{Constants, HeaderKey, Logging}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[AuthIdentifierAction])
trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent]

@Singleton
class AuthIdentifierAction @Inject()(override val authConnector: AuthConnector,
                                     config: AppConfig,
                                     playBodyParsers: BodyParsers.Default)
                                    (implicit override val executionContext: ExecutionContext)
  extends IdentifierAction with AuthorisedFunctions with Logging:

  override def parser: BodyParser[AnyContent] = playBodyParsers

  private[actions] def handleWithCorrelationId[A](
                                                   request: Request[A],
                                                   extraContext: String
                                                 )(block: RequestWithCorrelationId[A] => Future[Result]): Future[Result] = {
    val methodLoggingContext: String = "handleWithCorrelationId"

    infoLog("handleWithCorrelationId", "Attempting to retrieve Correlation ID from request headers")

    lazy val result = request.headers
      .get(HeaderKey.correlationIdKey)
      .fold {
        errorLog(s"$extraContext $methodLoggingContext", "Correlation ID was missing from request headers")
        Future.successful(InternalServerError(Json.toJson(MissingCorrelationIdError)))
      } { id =>
        infoLog(s"$extraContext $methodLoggingContext", "Correlation ID was successfully retrieved from request headers")
        block(RequestWithCorrelationId(request, CorrelationId(id)))
      }

    result
  }

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {
    val logContext: String = "[AuthenticatedIdentifierAction][invokeBlock] - "
    
    handleWithCorrelationId(request, logContext) { req =>

      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(req)
      
      authorised(Enrolment(Constants.ptaEnrolmentKey))
        .retrieve(Retrievals.internalId and Retrievals.nino and Retrievals.confidenceLevel) {
          case Some(internalId) ~ Some(nino) ~ confidenceLevel if confidenceLevel >= config.confidenceLevel =>
            infoLog(
              context = logContext,
              message = s"User is authorised to access the LEPP service with correlationId: ${req.correlationId.value}"
            )
            block(IdentifierRequest(request, AuthUser.apply(internalId, nino), req.correlationId))
          case Some(_) ~ Some(_) ~ _ =>
            warnLog(
              context = logContext,
              message = s"User has insufficient confidence level, not authorised to use this service with correlationId: ${req.correlationId.value}"
            )
            throw InsufficientConfidenceLevel("Confidence Level is less than 250")
          case _ =>
            warnLog(
              context = logContext,
              message = s"Cannot retrieve nino, or internal ID for user with correlationId: ${req.correlationId.value}"
            )
            throw InternalError("User not permitted to access service")
        } recoverWith {
        case _: InsufficientEnrolments =>
          warnLog(
            context = logContext,
            message = s"User doesn't have PTA enrolment, not authorised to access this service with correlationId: ${req.correlationId.value}"
          )
          Future.successful(Unauthorized(Json.toJson(UnauthorisedError)))
        case ex: MissingBearerToken =>
          warnLog(
            context = "invokeBlock",
            message = s"Authorisation bearer token could not be found for correlationId: ${req.correlationId.value} due to ${ex.getMessage}"
          )
          Future.successful(Unauthorized(Json.toJson(InvalidBearerTokenError)))
        case ex: AuthorisationException =>
          warnLog(
            context = "invokeBlock",
            message = s"An authorisation error occurred for correlationId: ${req.correlationId.value} due to ${ex.getMessage}"
          )
          Future.successful(Unauthorized(Json.toJson(UnauthorisedError)))
      }
    }
  }
  