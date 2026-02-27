package models.auth

import models.CorrelationId
import play.api.mvc.{Request, WrappedRequest}

case class IdentifierRequest[A](request: Request[A],
                                correlationId: CorrelationId,
                                userDetails: UserDetails) extends WrappedRequest[A](request)

