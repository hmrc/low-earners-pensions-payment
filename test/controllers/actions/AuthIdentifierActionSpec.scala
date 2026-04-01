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

import base.UnitBaseSpec
import com.google.inject.Inject
import config.AppConfig
import models.errors.{InvalidBearerTokenError, UnauthorisedError}
import models.requests.{AuthUser, IdentifierRequest}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.libs.json.Json
import play.api.mvc.*
import play.api.mvc.Results.Ok
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, StubPlayBodyParsersFactory}
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.ConfidenceLevel.{L200, L250}
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.auth.core.syntax.retrieved.authSyntaxForRetrieved
import uk.gov.hmrc.http.HeaderCarrier
import utils.Constants

import scala.concurrent.{ExecutionContext, Future}

class AuthIdentifierActionSpec extends UnitBaseSpec with StubPlayBodyParsersFactory {

  private val mockAuthConnector: AuthConnector = mock[AuthConnector]
  private val mockAppConfig: AppConfig = mock[AppConfig]
  
  def authAction = new AuthIdentifierAction(authConnector = mockAuthConnector,
    config = mockAppConfig,
    playBodyParsers = parsers
  )(ExecutionContext.global)
  
  class Handler {
    def run: Action[AnyContent] = authAction { request =>
      request match {
        case IdentifierRequest(_, AuthUser(userId, nino), correlationId) =>
          Ok(
            Json.obj(
              "userId" -> userId,
              "nino" -> nino,
              "correlationId" -> correlationId.value
            )
          )
      }
    }
  }

  def handler: Handler = new Handler()
  
  def authResult(internalId: Option[String],
                 nino: Option[String],
                 confidenceLevel: ConfidenceLevel,
                 enrolments: Enrolment*): Option[String] ~ Option[String] ~ ConfidenceLevel ~ Enrolments =
    internalId and nino and confidenceLevel and Enrolments(enrolments.toSet)

  val ptaEnrolment: Enrolment =
    Enrolment(Constants.ptaEnrolmentKey, Seq(EnrolmentIdentifier("Some_Id", "A2100001")), "Activated")

  val invalidEnrolment: Enrolment =
    Enrolment("INVALID", Seq.empty, "Activated")

  def setAuthValue(value: Option[String] ~ Option[String] ~ ConfidenceLevel ~ Enrolments): Unit =
    setAuthValue(Future.successful(value))

  def setAuthValue[A](value: Future[A]): Unit =
    when(mockAuthConnector.authorise[A](any(), any())(any(), any()))
      .thenReturn(value)

  "AuthenticateIdentifierAction" - {
    val fakeRequestWithCorrelationId = FakeRequest().withHeaders("correlationId" -> "x-id")

    "throw an exception" - {
      "when any unhandled exception occurs" in runningApplication { _ =>
        setAuthValue(Future.failed(new RuntimeException("Authorise predicate fails")))
        val result: Future[Result] = handler.run(fakeRequestWithCorrelationId)
        assertThrows[RuntimeException](await(result))
      }

      "when authorise fails to match predicate" in runningApplication { _ =>
        setAuthValue(Future.failed(new AuthorisationException("Authorise predicate fails") {}))
        val result = handler.run(fakeRequestWithCorrelationId)
        redirectLocation(result) mustBe None
        contentAsJson(result) mustBe Json.toJson(UnauthorisedError)
      }

      "when authorise fails due to invalid or no bearer token" in runningApplication { _ =>
        setAuthValue(Future.failed(new MissingBearerToken("No Bearer token") {}))
        val result = handler.run(fakeRequestWithCorrelationId)
        redirectLocation(result) mustBe None
        contentAsJson(result) mustBe Json.toJson(InvalidBearerTokenError)
      }

      "when user does not have an Internal Id" in runningApplication { _ =>
        setAuthValue(authResult(None, Some("AA123456C"), L250, ptaEnrolment))
        val result = handler.run(fakeRequestWithCorrelationId)
        redirectLocation(result) mustBe None
      }

      "when user does not have enough confidence level" in runningApplication { _ =>
        when(mockAppConfig.confidenceLevel).thenReturn(L250)
        setAuthValue(authResult(Some("internalId"), Some("AA123456C"), L200, ptaEnrolment))
        val result = handler.run(fakeRequestWithCorrelationId)
        contentAsJson(result) mustBe Json.toJson(UnauthorisedError)
      }

      "when user does not have pta enrolment" in runningApplication { _ =>
        setAuthValue(authResult(Some("internalId"), Some("AA123456C"), L250, invalidEnrolment))
        val result = handler.run(fakeRequestWithCorrelationId)
        redirectLocation(result) mustBe None
      }
    }

    "return an IdentifierRequest" - {
      "User has a pta enrolment" in runningApplication { _ =>
        when(mockAppConfig.confidenceLevel).thenReturn(L250)
        setAuthValue(authResult(Some("internalId"), Some("AA123456C"), L250, ptaEnrolment))

        val result = handler.run(fakeRequestWithCorrelationId)

        status(result) mustBe OK
        (contentAsJson(result) \ "userId").asOpt[String] mustBe Some("internalId")
        (contentAsJson(result) \ "correlationId").asOpt[String] mustBe Some("x-id")
        (contentAsJson(result) \ "nino").asOpt[String] mustBe Some("AA123456C")
      }

      "must throw an error when correlationId is missing from request headers" in runningApplication { _ =>
        when(mockAppConfig.confidenceLevel).thenReturn(L250)
        setAuthValue(authResult(Some("internalId"), Some("AA123456C"), L250, ptaEnrolment))

        val result = handler.run(FakeRequest())

        status(result) mustBe INTERNAL_SERVER_ERROR
        (contentAsJson(result) \ "code").asOpt[String] mustBe Some("MISSING_CORRELATION_ID")
        (contentAsJson(result) \ "message").asOpt[String] mustBe Some(
          "CorrelationId header could not be found in request"
        )
      }
    }
  }
}

class FakeFailingAuthConnector @Inject()(exceptionToReturn: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)
}
