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

import com.google.inject.{Inject, Singleton}
import config.AppConfig
import controllers.requests.{CorrelationId, RequestWithCorrelationId}
import models.errors.{InternalLeppError, InvalidBearerTokenError, MissingCorrelationIdError, UnauthorisedError}
import models.requests.{AuthUser, IdentifierRequest}
import play.api.libs.json.Json
import play.api.mvc.*
import play.api.mvc.Results.{InternalServerError, Unauthorized}
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.{Constants, HeaderKey, Logging}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IdentifierAction @Inject()(override val authConnector: AuthConnector,
                                              config: AppConfig,
                                              playBodyParsers: BodyParsers.Default)
                                             (implicit override val executionContext: ExecutionContext)
  extends ActionBuilder[IdentifierRequest, AnyContent] with AuthorisedFunctions with Logging:

  private[actions] def handleWithCorrelationId[A](
                                                   request: Request[A],
                                                   extraContext: String
                                                 )(block: RequestWithCorrelationId[A] => Future[Result]): Future[Result] = {
    val methodLoggingContext: String = "handleWithCorrelationId"

    infoLog("handleWithCorrelationId","Attempting to retrieve Correlation ID from request headers")

    lazy val result = request.headers
      .get(HeaderKey.correlationIdKey)
      .fold {
        errorLog(s"$extraContext $methodLoggingContext","Correlation ID was missing from request headers")
        Future.successful(InternalServerError(Json.toJson(MissingCorrelationIdError)))
      } { id =>
        infoLog(s"$extraContext $methodLoggingContext", "Correlation ID was successfully retrieved from request headers")
        block(RequestWithCorrelationId(request, CorrelationId(id)))
      }

    result
  }

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {
    val logContext: String = "[AuthenticatedIdentifierAction][invokeBlock] - "
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    handleWithCorrelationId(request, logContext) { req =>
      val idLogString = correlationIdLogString(req.correlationId)

      authorised(Enrolment(Constants.ptaEnrolmentKey))
        .retrieve(Retrievals.internalId and Retrievals.nino and Retrievals.confidenceLevel and Retrievals.authorisedEnrolments) {
          case Some(internalId) ~ Some(nino) ~ confidenceLevel ~ enrolments if hasEnrolments(enrolments) =>
            if (confidenceLevel >= config.confidenceLevel) {
              block(IdentifierRequest(request, AuthUser.apply(internalId, nino), req.correlationId))
            } else {
              infoLog(logContext, s"User has insufficient confidence level, not authorised to use this service with correlationId: $idLogString")
              throw InsufficientConfidenceLevel("Confidence Level is less than 250")
            }
          case _ =>
            infoLog(logContext, s"User doesn't have PTA enrolment, not authorised to access this service with correlationId: $idLogString")
            throw InsufficientEnrolments("User has insufficient PTA enrolments")
        } recoverWith {
        case ex: MissingBearerToken =>
          warnLog("invokeBlock", s"Authorisation bearer token could not be found for correlationId: $idLogString due to ${ex.getMessage}")
          Future.successful(Unauthorized(Json.toJson(InvalidBearerTokenError)))
        case ex: AuthorisationException =>
          warnLog("invokeBlock", s"An authorisation error occurred  for correlationId: $idLogString due to ${ex.getMessage}")
          Future.successful(Unauthorized(Json.toJson(UnauthorisedError)))
        case _: UnauthorizedException =>
          errorLog("invokeBlock", s"An unexpected authorisation error occurred for correlationId: $idLogString")
          Future.successful(InternalServerError(Json.toJson(InternalLeppError)))
      }
    }
  }

  private def hasEnrolments(enrolments: Enrolments): Boolean =
    enrolments.getEnrolment(Constants.ptaEnrolmentKey).nonEmpty

  override def parser: BodyParser[AnyContent] = playBodyParsers

