/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import base.SpecBase
import config.AppConfig
import models.bars.{BarsVerifyStatusId, BarsVerifyStatusResponse, EncryptedBarsVerifyStatus, NumberOfBarsVerifyAttempts}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import repositories.{BarsVerifyStatusRepo, MongoCryptoImpl}

import java.time.{Clock, Instant, LocalDate, ZoneId, ZoneOffset}
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

class BarsVerifyStatusServiceSpec extends SpecBase {

  given ec: ExecutionContext = ExecutionContext.Implicits.global
  val encryptionKey = "P5xsJ9Nt+quxGZzB4DeLfw=="
  val nino = "AA123456C"
  private val initialLocalDate = LocalDate.parse("2020-12-25")
  private val currentClock: Clock = Clock.fixed(initialLocalDate.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneId.of("Z"))
  
  trait Test {
    private val testClock: Clock = new Clock {
      override def getZone: ZoneId = currentClock.getZone
      override def withZone(zoneId: ZoneId): Clock = currentClock.withZone(zoneId)
      override def instant(): Instant = currentClock.instant()
    }
    
    val barsRepo: BarsVerifyStatusRepo = mock[BarsVerifyStatusRepo]
    val config: AppConfig = mock[AppConfig]
    when(config.encryptionKey).thenReturn(encryptionKey)
    val mongoCrypto = new MongoCryptoImpl(config)
    val service = new BarsVerifyStatusService(barsRepo, config, mongoCrypto, testClock)
    val statusId = BarsVerifyStatusId(nino)
  }
  
  "status" - {
    "return default response if no record exists" in new Test {
      when(barsRepo.findById(any())).thenReturn(Future.successful(None))
      
      val result: Future[BarsVerifyStatusResponse] = service.status(statusId)
      whenReady(result) { resp =>
        resp.attempts shouldBe NumberOfBarsVerifyAttempts.zero
        resp.lockoutExpiryDateTime shouldBe None
      }
    }

    "return response from existing record" in new Test {
      val now: Instant = Instant.now
      val status = EncryptedBarsVerifyStatus(mongoCrypto.encryptStr(statusId.value), NumberOfBarsVerifyAttempts(2), now, now, None)
      when(barsRepo.findById(any())).thenReturn(Future.successful(Some(status)))

      val result: Future[BarsVerifyStatusResponse] = service.status(statusId)
      whenReady(result) { resp =>
        resp.attempts shouldBe NumberOfBarsVerifyAttempts(2)
        resp.lockoutExpiryDateTime shouldBe None
      }
    }
  }

  "update" - {
    "create a new record if none exists" in  new Test{
      when(barsRepo.findById(any())).thenReturn(Future.successful(None))
      when(barsRepo.upsert(any())).thenReturn(Future.successful(()))
      when(config.barsVerifyMaxAttempts).thenReturn(3)

      val result: Future[BarsVerifyStatusResponse] = service.update(statusId)
      whenReady(result) { resp =>
        resp.attempts shouldBe NumberOfBarsVerifyAttempts(1)
        resp.lockoutExpiryDateTime shouldBe None
      }
    }

    "increment attempts and set lockout if max reached" in new Test {
      val now: Instant = Instant.now
      val status = EncryptedBarsVerifyStatus(mongoCrypto.encryptStr(statusId.value), NumberOfBarsVerifyAttempts(2), now, now, None)
      when(barsRepo.findById(any())).thenReturn(Future.successful(Some(status)))
      when(barsRepo.upsert(any())).thenReturn(Future.successful(()))
      when(config.barsVerifyMaxAttempts).thenReturn(3)
      when(config.barsVerifyRepoTtl).thenReturn(FiniteDuration(100, TimeUnit.MINUTES))

      val result: Future[BarsVerifyStatusResponse] = service.update(statusId)
      whenReady(result) { resp =>
        resp.attempts shouldBe NumberOfBarsVerifyAttempts(3)
        resp.lockoutExpiryDateTime should not be None
      }
    }
  }
}
