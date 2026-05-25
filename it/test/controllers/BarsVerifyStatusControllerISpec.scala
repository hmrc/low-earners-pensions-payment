/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers

import base.ItBaseSpec
import config.AppConfig
import models.bars.{BarsUpdateVerifyStatusParams, BarsVerifyStatusId, EncryptedBarsVerifyStatus, NumberOfBarsVerifyAttempts}
import org.mongodb.scala.SingleObservableFuture
import org.mongodb.scala.bson.BsonDocument
import org.scalatest.matchers.should.Matchers.shouldBe
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status.*
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import repositories.{BarsVerifyStatusRepo, MongoCrypto}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.domain.Nino
import utils.FrozenTime

import java.time.Instant
import java.time.temporal.ChronoUnit.HOURS
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BarsVerifyStatusControllerISpec extends ItBaseSpec {

  val repo: BarsVerifyStatusRepo = application.injector.instanceOf[BarsVerifyStatusRepo]
  val crypto: MongoCrypto        = application.injector.instanceOf[MongoCrypto]

  trait Setup {
    
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockAppConfig: AppConfig = mock[AppConfig]
    
    val controller: BarsVerifyStatusController = application.injector.instanceOf[BarsVerifyStatusController]

    val ninoABC: Nino = Nino("AA123456C")

    def updateVerifyStatusParams(nino: Nino): BarsUpdateVerifyStatusParams =
      BarsUpdateVerifyStatusParams(BarsVerifyStatusId.from(nino))

    def request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withAuthToken()

    def initialStatusAttempts(nino: Nino, attempts: Int): Unit =
      repo.collection
        .insertOne(
          EncryptedBarsVerifyStatus(
            _id = crypto.encryptStr(BarsVerifyStatusId.from(nino).value),
            verifyCalls = NumberOfBarsVerifyAttempts(attempts)
          )
        )
        .toFuture()
        .map(_ => ())
        .futureValue
  }
  
  "the bars verify status controller" when {
    "called for an Id with no record" should {
      "respond with body 'zero attempts'" in new Setup {
        val response: Future[Result] = controller.status()(request)
        status(response) shouldBe OK
        contentAsString(response) shouldBe """{"attempts":0}"""
      }
    }

    "called for an Id with an existing record of 'one attempt'" should {
      "respond with body 'one attempt'" in new Setup {
        initialStatusAttempts(ninoABC, attempts = 1)
        val response: Future[Result] = controller.status()(request)
        status(response) shouldBe OK
        contentAsString(response) shouldBe """{"attempts":1}"""
      }
    }
  }

  "the bars verify update controller" when {
    "called for an Id with no record" should {
      "respond with body 'one attempt'" in new Setup {
        val response: Future[Result] = controller.update()(request)
        status(response) shouldBe OK
        contentAsString(response) shouldBe """{"attempts":1}"""
      }
    }

    "called for an Id with an existing record of 'one attempt'" should {
      "respond with body 'two attempts'" in new Setup {
        initialStatusAttempts(ninoABC, attempts = 1)
        val response: Future[Result] = controller.update()(request)
        status(response) shouldBe OK
        contentAsString(response) shouldBe """{"attempts":2}"""
      }
    }

    "called for an Id with an existing record of 'two attempts'" should {
      "respond with locked-out body" in new Setup {
        initialStatusAttempts(ninoABC, attempts = 2)
        val response: Future[Result] = controller.update()(request)
        status(response) shouldBe OK

        val expectedLockout: Instant = FrozenTime.instant.plus(24, HOURS)
        contentAsString(response) shouldBe s"""{"attempts":3,"lockoutExpiryDateTime":"${expectedLockout.toString}"}"""
      }
    }
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    repo.collection.deleteMany(BsonDocument("{}")).toFuture().futureValue.wasAcknowledged() shouldBe true
    ()
  }
}
