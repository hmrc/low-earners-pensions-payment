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

import base.SpecBase
import config.AppConfig
import models.auth.AuthorisedRequest
import org.apache.pekko.stream.testkit.NoMaterializer
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.mockito.stubbing.OngoingStubbing
import play.api.mvc.Results.Ok
import play.api.mvc.{AnyContentAsEmpty, BodyParsers, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.stubPlayBodyParsers
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.ConfidenceLevel.L250
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import utils.{CorrelationIdHandler, CorrelationIdMandatory, CorrelationIdOptional}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IdentifierActionSpec extends SpecBase {

  trait Test {
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockAppConfig: AppConfig = mock[AppConfig]
    
    val idHandler: CorrelationIdHandler = new CorrelationIdOptional()

    lazy val testIdentifierAction: IdentifierActionImpl = new IdentifierActionImpl(
      authConnector = mockAuthConnector,
      config = mockAppConfig,
      parser = BodyParsers.Default(stubPlayBodyParsers(NoMaterializer))
    ) {
      override val correlationIdHandler: CorrelationIdHandler = idHandler
    }
    
    when(mockAppConfig.confidenceLevelMinimum).thenReturn(L250)

    type RetrievalsType = Option[String] ~ ConfidenceLevel
    
    def mockAuth(res: Future[RetrievalsType]): OngoingStubbing[Future[RetrievalsType]] = when(
      mockAuthConnector.authorise(
        ArgumentMatchers.any(),
        ArgumentMatchers.any[Retrieval[Option[String] ~ ConfidenceLevel]]()
      )(
        ArgumentMatchers.any(),
        ArgumentMatchers.any()
      )
    ).thenReturn(res)
    
    def block[A](): AuthorisedRequest[A] => Future[Result] = _ => Future.successful(Ok("A result"))
    
    val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders("correlationId" -> "some-id")
  }

  "IdentifierAction" - {
    "invokeBlock" - {
      "when correlationId is mandatory and missing should return an error" in new Test {
        override val idHandler: CorrelationIdHandler = new CorrelationIdMandatory
        val result: Future[Result] = testIdentifierAction.invokeBlock(
          request = FakeRequest(),
          block = block()
        )

        status(result) shouldBe BAD_REQUEST
        contentAsString(result) should include("CORRELATION_ID_HEADER_MISSING")
      }

      "when correlationId is optional and missing should proceed normally" in new Test {
        mockAuth(Future.successful(~.apply(Some("123"), ConfidenceLevel.L250)))
        val result: Future[Result] = testIdentifierAction.invokeBlock(
          request = FakeRequest(),
          block = block()
        )

        status(result) shouldBe OK
        contentAsString(result) should include("A result")
      }
      
      "should return an error when PTA enrolment doesn't exist for a user" in new Test {
        mockAuth(Future.failed(InsufficientEnrolments("Missing PTA Enrolment")))
        val result: Future[Result] = testIdentifierAction.invokeBlock(
          request = request,
          block = block()
        )
        
        status(result) shouldBe UNAUTHORIZED
        contentAsString(result) should include("MISSING_PTA_ENROLMENT")
      }

      "should return an error when confidence level is below 250 for a user" in new Test {
        mockAuth(Future.successful(~.apply(Some("aNino"), ConfidenceLevel.L50)))
        val result: Future[Result] = testIdentifierAction.invokeBlock(
          request = request,
          block = block()
        )

        status(result) shouldBe UNAUTHORIZED
        contentAsString(result) should include("INSUFFICIENT_CONFIDENCE_LEVEL")
      }

      "should return an error when no NINO can be found for a user" in new Test {
        mockAuth(Future.successful(~.apply(None, ConfidenceLevel.L50)))
        val result: Future[Result] = testIdentifierAction.invokeBlock(
          request = request,
          block = block()
        )

        status(result) shouldBe UNAUTHORIZED
        contentAsString(result) should include("NO_NINO_FOUND_FOR_USER")
      }

      "should return an error when no active session exists user" in new Test {
        mockAuth(Future.failed(BearerTokenExpired()))
        val result: Future[Result] = testIdentifierAction.invokeBlock(
          request = request,
          block = block()
        )

        status(result) shouldBe UNAUTHORIZED
        contentAsString(result) should include("NO_ACTIVE_SESSION")
      }

      "should return an error when any other authorisation exception occurs" in new Test {
        mockAuth(Future.failed(FailedRelationship()))
        val result: Future[Result] = testIdentifierAction.invokeBlock(
          request = request,
          block = block()
        )

        status(result) shouldBe INTERNAL_SERVER_ERROR
        contentAsString(result) should include("AUTHORISATION_FAILED")
      }
      
      "should invoke block for a valid request" in new Test {
        mockAuth(Future.successful(~.apply(Some("123"), ConfidenceLevel.L250)))
        val result: Future[Result] = testIdentifierAction.invokeBlock(
          request = request,
          block = block()
        )

        status(result) shouldBe OK
        contentAsString(result) should include("A result")
      }
    }
  }
}
