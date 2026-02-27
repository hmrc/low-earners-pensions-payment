package models

import models.errors.ErrorResult
import play.api.http.Status.OK
import play.api.libs.json.{Json, OWrites}
import play.api.mvc.Result
import play.api.mvc.Results.{Ok, Status}
import utils.CorrelationIdKey

enum ResponseWrapper[T: OWrites] {
  val value: T
  val correlationId: CorrelationId

  case SuccessWrapper[S: OWrites](value: S, correlationId: CorrelationId) extends ResponseWrapper[S]
  case ErrorWrapper(value: ErrorResult, correlationId: CorrelationId) extends ResponseWrapper[ErrorResult]

  def toResult: Result = {
    val status = this match {
      case ResponseWrapper.SuccessWrapper(_, _) => OK
      case ResponseWrapper.ErrorWrapper(error, _) => error.status
    }

    Status(status)(Json.toJson(value))
  }
}

object ResponseWrapper {
  def apply[R: OWrites](value: Either[ErrorResult, R],
                        correlationId: CorrelationId): ResponseWrapper[? >: ErrorResult & R] = value match {
    case Left(error) => ErrorWrapper(error, correlationId)
    case Right(success) => SuccessWrapper(success, correlationId)
  }
}
