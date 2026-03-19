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
import models.auth.{AuthorisedRequest, Nino, UserDetails}
import play.api.mvc.{AnyContent, BodyParser, Request, Result}
import play.api.test.Helpers.stubBodyParser
import utils.CorrelationIdHandler

import scala.concurrent.{ExecutionContext, Future}

class MockIdentifierAction[C <: CorrelationIdHandler](handler: C) extends IdentifierAction[C] with SpecBase {
  override def invokeBlock[A](request: Request[A],
                              block: AuthorisedRequest[A] => Future[Result]): Future[Result] =
    handler.handle(request) { correlationId =>
      block(
        AuthorisedRequest(
          request = request,
          correlationId = correlationId,
          userDetails = UserDetails(Nino("someNino"))
        )
      )
    }


  override def parser: BodyParser[AnyContent] = stubBodyParser()

  override protected def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global
}
