package models

import scala.language.implicitConversions

case class CorrelationId(value: String)

object CorrelationId {
  implicit def toString(correlationId: CorrelationId): String = correlationId.value
}
