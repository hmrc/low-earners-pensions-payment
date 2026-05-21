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

package repositories

import config.AppConfig
import models.bars.EncryptedBarsVerifyStatus
import org.mongodb.scala.model.{IndexModel, IndexOptions, Indexes}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.Codecs

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import repositories.BarsVerifyStatusRepo.id
import repositories.BarsVerifyStatusRepo.idExtractor
import repositories.Repo.{Id, IdExtractor}

@Singleton
final class BarsVerifyStatusRepo @Inject() (
  mongoComponent: MongoComponent,
  config:         AppConfig
)(implicit ec: ExecutionContext)
    extends Repo[String, EncryptedBarsVerifyStatus](
      collectionName = "bars",
      mongoComponent = mongoComponent,
      indexes = BarsVerifyStatusRepo.indexes(config.barsVerifyRepoTtl.toSeconds),
      extraCodecs = Codecs.playFormatCodecsBuilder(EncryptedBarsVerifyStatus.format).build,
      replaceIndexes = config.barsVerifyRepoReplaceIndexes
    )

object BarsVerifyStatusRepo {
  implicit val id: Id[String]                                              = (i: String) => i
  implicit val idExtractor: IdExtractor[EncryptedBarsVerifyStatus, String] = (b: EncryptedBarsVerifyStatus) => b._id

  def indexes(cacheTtlInSeconds: Long): Seq[IndexModel] = Seq(
    IndexModel(
      keys = Indexes.ascending("lastUpdated"),
      indexOptions = IndexOptions().expireAfter(cacheTtlInSeconds, TimeUnit.SECONDS).name("lastUpdatedIdx")
    )
  )
}
