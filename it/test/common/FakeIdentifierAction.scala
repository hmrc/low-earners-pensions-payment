package common

import controllers.actions.IdentifierAction
import models.auth.{AuthorisedRequest, Nino, UserDetails}
import play.api.mvc.*
import utils.CorrelationIdOptional

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FakeIdentifierAction @Inject()(bodyParsers: BodyParsers.Default) extends IdentifierAction {
  override def invokeBlock[A](request: Request[A],
                              block: AuthorisedRequest[A] => Future[Result]): Future[Result] = {
    val handler = new CorrelationIdOptional
    handler.handleCorrelationId(request) { correlationId =>
      block(
        AuthorisedRequest(
          request = request,
          correlationId = correlationId,
          userDetails = UserDetails(Nino("someNino"))
        )
      )
    }
  }


  override def parser: BodyParser[AnyContent] = bodyParsers

  override protected def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global
}
