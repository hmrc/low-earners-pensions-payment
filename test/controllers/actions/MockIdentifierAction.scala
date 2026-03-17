package controllers.actions

import base.SpecBase
import models.CorrelationId
import models.auth.{IdentifierRequest, Nino, UserDetails}
import play.api.mvc.{AnyContent, BodyParser, Request, Result}
import play.api.test.Helpers.stubBodyParser

import scala.concurrent.{ExecutionContext, Future}

class MockIdentifierAction extends IdentifierAction with SpecBase {
  override def invokeBlock[A](request: Request[A],
                              block: IdentifierRequest[A] => Future[Result]): Future[Result] = block(
    IdentifierRequest(
      request = request,
      correlationId = testCorrelationId,
      userDetails = UserDetails(Nino("someNino"))
    )
  )


  override def parser: BodyParser[AnyContent] = stubBodyParser()

  override protected def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global
}
