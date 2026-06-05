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

import com.google.inject.{Inject, Singleton}
import controllers.actions.IdentifierAction
import models.bars.BarsVerifyStatusId
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.*
import services.BarsVerifyStatusService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext

@Singleton
class BarsVerifyStatusController @Inject()(
                                            identify: IdentifierAction,
                                            barsService:             BarsVerifyStatusService,
                                            cc:                      ControllerComponents
                                          )(implicit exec: ExecutionContext)
                                              extends BackendController(cc) {

  def status(): Action[AnyContent] =
    identify.async { implicit request =>
      barsService
        .status(BarsVerifyStatusId(request.user.nino.value))
        .map(resp => Ok(Json.toJson(resp)))
    }

  def update(): Action[JsValue] =
    identify.async(parse.json) { implicit request =>
      barsService
        .update(BarsVerifyStatusId(request.user.nino.value))
        .map(resp => Ok(Json.toJson(resp)))
    }
}
