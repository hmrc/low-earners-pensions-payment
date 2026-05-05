/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package utils

import java.time.*

object DatesSupport {
  def fixedClockUTC(fixedAtDate: LocalDate): Clock         =
    Clock.fixed(fixedAtDate.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneId.of("Z"))
  def fixedClockUTC(fixedAtDateTime: LocalDateTime): Clock =
    Clock.fixed(fixedAtDateTime.toInstant(ZoneOffset.UTC), ZoneId.of("Z"))
}
