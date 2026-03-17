/*
 * Copyright 2026 HM Revenue & Customs
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

import cats.data.EitherT
import com.google.inject.{Inject, Singleton}
import controllers.actions.IdentifierAction
import controllers.validators.BarsRequestValidator
import models.{CorrelationId, ResponseWrapper}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents, Result}
import services.{BarsService, CorrelationIdService}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.CorrelationIdKey

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BarsController @Inject()(identifierAction: IdentifierAction,
                               validator: BarsRequestValidator,
                               service: BarsService,
                               cc: ControllerComponents)(implicit ec: ExecutionContext) extends BackendController(cc) {
  def checkBankAccountDetails(): Action[JsValue] = identifierAction.async(parse.json) { implicit request =>
    def result: EitherT[Future, ResponseWrapper.ErrorWrapper, Result] = for {
      barsRequest <- EitherT.fromEither[Future](validator.validate(request.body, request.correlationId))
      barsResult <- service.checkBankAccountDetails(barsRequest, request.correlationId)
    } yield {
      Ok(Json.toJson(barsResult.value)).withHeaders(
        CorrelationIdKey.value -> barsResult.correlationId
      )
    }

    result.leftMap(errorResult => {
      errorResult
        .value.toResult
        .withHeaders(
          CorrelationIdKey.value -> errorResult.correlationId
        )
    }).merge
  }
}
