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

package config

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.auth.core.ConfidenceLevel
import uk.gov.hmrc.auth.core.ConfidenceLevel.L250
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.concurrent.duration.FiniteDuration

@Singleton
class AppConfig @Inject()(configuration: Configuration):

  private def loadConfig(key: String): String = configuration.get[String](key)

  val appName: String = loadConfig("appName")

  val host: String = loadConfig("host")

  val confidenceLevel: ConfidenceLevel =
    ConfidenceLevel
      .fromInt(configuration.get[Int]("controllers.confidenceLevel"))
      .getOrElse(L250)

  private val servicesConfig = ServicesConfig(configuration)
  private lazy val npsBase: String = servicesConfig.baseUrl("nps")

  private lazy val npsContext: String = loadConfig("urls.npsContext")
  lazy val getPaymentDetailsUrl: String = npsBase + npsContext + s"/${loadConfig("urls.getPaymentDetails")}"

  lazy val npsClientId: String = loadConfig("nps-headers.clientId")
  lazy val npsSecret: String = loadConfig("nps-headers.secret")
  lazy val npsEnv: String = loadConfig("nps-headers.env")

  lazy val govUkOriginatorId: String = loadConfig("nps-headers.govUkOriginatorId")


  val encryptionKey: String = servicesConfig.getString("mongodb.encryption-key")

  val barsVerifyRepoTtl: FiniteDuration     = configuration.get[FiniteDuration]("bars.verify.repoTtl")
  val barsVerifyMaxAttempts: Int            = configuration.get[Int]("bars.verify.maxAttempts")
  val barsVerifyRepoReplaceIndexes: Boolean = configuration.get[Boolean]("bars.verify.replaceIndexes")
