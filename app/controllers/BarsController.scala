package controllers

import cats.data.EitherT
import com.google.inject.{Inject, Singleton}
import controllers.actions.IdentifierAction
import controllers.validators.BarsRequestValidator
import models.{CorrelationId, ResponseWrapper}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents, Result}
import services.BarsService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.CorrelationIdKey

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BarsController @Inject()(identifierAction: IdentifierAction,
                               validator: BarsRequestValidator,
                               service: BarsService,
                               cc: ControllerComponents)(implicit ec: ExecutionContext) extends BackendController(cc) {
  def checkBankAccountDetails(): Action[JsValue] = identifierAction.async(parse.json) { implicit request => {
      val correlationId: CorrelationId = ???

      val result: EitherT[Future, ResponseWrapper.ErrorWrapper, Result] = for {
        barsRequest <- EitherT.fromEither[Future](validator.validate(request.body))
        barsResult <- service.checkBankAccountDetails(barsRequest, correlationId)
      } yield {
        Ok(Json.toJson(barsResult.value)).withHeaders(
          CorrelationIdKey.value -> barsResult.correlationId
        )
      }

      result.leftMap(errorResult => {
        errorResult.toResult.withHeaders(
          CorrelationIdKey.value -> errorResult.correlationId
        )
      }).merge
    }
  }
}
