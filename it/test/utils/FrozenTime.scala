/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package utils

import java.time.*

/** A time machine which allows to travel back and forth in time.
  */

object FrozenTime {

  def setTime(fixedAtDate: LocalDate): Unit = {
    val clock = DatesSupport.fixedClockUTC(fixedAtDate)
    currentClock = clock
  }

  def setTime(fixedAtDateTime: LocalDateTime): Unit = {
    val clock = DatesSupport.fixedClockUTC(fixedAtDateTime)
    currentClock = clock
  }

  def addSeconds(seconds: Long): Unit = {
    val nowPlusSeconds = LocalDateTime.now(testClock).plusSeconds(seconds)
    setTime(nowPlusSeconds)
  }

  def addHours(hours: Long): Unit =
    setTime(LocalDateTime.now(testClock).plusHours(hours))

  def setTime(fixedAtDate: String): Unit = {
    val clock = DatesSupport.fixedClockUTC(LocalDate.parse(fixedAtDate))
    currentClock = clock
  }

  def instant: Instant             = Instant.now(testClock)
  def localDateTime: LocalDateTime = LocalDateTime.now(testClock)
  def localDate: LocalDate         = LocalDate.now(testClock)
  def getClock: Clock              = testClock

  def reset(): Unit = setTime(initialLocalDate)

  private val initialLocalDate = LocalDate.parse("2020-12-25")

  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  private var currentClock: Clock = DatesSupport.fixedClockUTC(initialLocalDate)

  private val testClock: Clock = new Clock {
    override def getZone(): ZoneId = currentClock.getZone
    override def withZone(zoneId: ZoneId): Clock = currentClock.withZone(zoneId)
    override def instant(): Instant = currentClock.instant()
  }
}
