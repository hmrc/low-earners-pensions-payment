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

import play.api.Configuration
import uk.gov.hmrc.auth.core.ConfidenceLevel
import uk.gov.hmrc.auth.core.ConfidenceLevel.L250
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(config: Configuration):
  private val servicesConfig = ServicesConfig(config)

  val appName: String = config.get[String]("appName")

  private val barsBaseUrl: String = servicesConfig.baseUrl("bars")
  private val barsEnv: String = config.get("microservice.services.bars.env")
  def barsUrl: String = barsBaseUrl + (if(barsEnv == "local") "" else "/bank-account-reputation")
  
  val confidenceLevelMinimum: ConfidenceLevel =
    ConfidenceLevel
    .fromInt(config.get[Int]("confidenceLevelMinimum"))
    .getOrElse(L250)
