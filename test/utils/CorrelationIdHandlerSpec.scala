package utils

import base.SpecBase
import models.CorrelationId
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest

import scala.concurrent.Future

class CorrelationIdHandlerSpec extends SpecBase {
  "CorrelationIdHandler" - {
    "handle" - {
      val block: CorrelationId => Future[Result] = id => Future.successful(Ok(id.value))
      
      def idToRequest(idOpt: Option[String]) = idOpt.fold(FakeRequest())(
        id => FakeRequest().withHeaders("correlationId" -> id)
      )
      
      "should invoke block when correlation ID exists and is mandatory" in {
        val result: Future[Result] = new CorrelationIdMandatory().handle(idToRequest(Some("id")))(block)
        status(result) shouldBe OK
        contentAsString(result) should include("id")
      }

      "should invoke block when correlation ID exists and is optional" in {
        val result: Future[Result] = new CorrelationIdOptional().handle(idToRequest(Some("id")))(block)
        status(result) shouldBe OK
        contentAsString(result) should include("id")
      }

      "should return error when correlation ID is required and doesn't exist" in {
        val result: Future[Result] = new CorrelationIdMandatory().handle(idToRequest(None))(block)
        status(result) shouldBe BAD_REQUEST
        contentAsString(result) should include("CORRELATION_ID_HEADER_MISSING")
      }

      "should generate ID and invoke block when correlation ID is optional and doesn't exist" in {
        val handler: CorrelationIdOptional =  new CorrelationIdOptional() {
          override protected[utils] def generateCorrelationId: CorrelationId = CorrelationId("generatedId")
        }
        
        val result: Future[Result] = handler.handle(idToRequest(None))(block)
        status(result) shouldBe OK
        contentAsString(result) should include("generatedId")
      }
    }
  }
}
