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
