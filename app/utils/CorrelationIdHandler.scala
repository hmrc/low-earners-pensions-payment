package utils

import com.google.inject.{Inject, Singleton}
import models.CorrelationId
import models.errors.ErrorResult.ServiceErrorResult
import play.api.http.Status.BAD_REQUEST
import play.api.mvc.{Request, Result}

import java.util.UUID
import scala.concurrent.Future

sealed class CorrelationIdHandler(correlationIdMandatory: Boolean) {
  protected[utils] def generateCorrelationId: CorrelationId = CorrelationId(UUID.randomUUID().toString)
  
  def handle[A](request: Request[A])(block: CorrelationId => Future[Result]): Future[Result] =
    request.headers.get(CorrelationIdKey.value) match {
    case Some(value) => block(CorrelationId(value))
    case None if correlationIdMandatory => Future.successful(
      ServiceErrorResult(BAD_REQUEST, "CORRELATION_ID_HEADER_MISSING").toResult
    )
    case _ => block(generateCorrelationId)
  }
}

@Singleton
class CorrelationIdMandatory @Inject() extends CorrelationIdHandler(true)

@Singleton
class CorrelationIdOptional @Inject() extends CorrelationIdHandler(false)

