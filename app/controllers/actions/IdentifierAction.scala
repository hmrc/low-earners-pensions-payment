package controllers.actions

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.auth.IdentifierRequest
import play.api.mvc.*
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[IdentifierActionImpl])
trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent]

@Singleton
class IdentifierActionImpl @Inject()(override val authConnector: AuthConnector,
                                     val parser: BodyParsers.Default)
                                    (implicit override val executionContext: ExecutionContext)
  extends IdentifierAction with AuthorisedFunctions {
  override def invokeBlock[A](request: Request[A],
                              block: IdentifierRequest[A] => Future[Result]): Future[Result] = ???
}
