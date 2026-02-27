package controllers.validators

import com.google.inject.Singleton
import models.ResponseWrapper.ErrorWrapper
import models.bars.BarsRequest
import play.api.libs.json.JsValue

@Singleton
class BarsRequestValidator {
  def validate(json: JsValue): Either[ErrorWrapper, BarsRequest] = ???
}
