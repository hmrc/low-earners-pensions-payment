package controllers

import common.IntegrationSpecBase
import play.api.Application
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.route as routeRequest
import play.api.test.Helpers.running as runningRequest
import play.mvc.Http.Status
import play.test.Helpers.*

import scala.concurrent.Future

class BarsControllerIntegrationSpec extends IntegrationSpecBase {
  
  "/check-bank-account-details" should {
    val url: String = "/low-earners-pensions-payment/check-bank-account-details"
    
    "handle validation errors" when {
      "request body cannot be parsed to JSON" in {
        val application: Application = fakeApplication()
        runningRequest(application) {
          val fakeRequest = FakeRequest(POST, url).withBody("")
          val result: Future[Result] = routeRequest(application, fakeRequest).getOrElse(
            Future.failed(new RuntimeException("TEST_ERROR"))
          )
          
          status(result) shouldBe Status.UNSUPPORTED_MEDIA_TYPE
          contentAsString(result) should include("Expecting text/json or application/json body")
        }
      }
      
      "request body is missing mandatory fields" in {
        val application: Application = fakeApplication()
        runningRequest(application) {
          val fakeRequest = FakeRequest(POST, url).withBody(JsObject.empty)
          val result: Future[Result] = routeRequest(application, fakeRequest).getOrElse(
            Future.failed(new RuntimeException("TEST_ERROR"))
          )

          status(result) shouldBe Status.BAD_REQUEST
          contentAsString(result) should include("REQUEST_MISSING_MANDATORY_FIELD")
        }
      }
      
      "request body fails field validations" in {
        val application: Application = fakeApplication()
        runningRequest(application) {
          val fakeRequest = FakeRequest(POST, url).withBody(Json.parse(
            """
              |{
              | "name": "!!!",
              | "accountNumber": "1234",
              | "sortCode": "11-22-3"
              |}
            """.stripMargin))
          val result: Future[Result] = routeRequest(application, fakeRequest).getOrElse(
            Future.failed(new RuntimeException("TEST_ERROR"))
          )

          status(result) shouldBe Status.BAD_REQUEST
          contentAsString(result) should include("REQUEST_PARAMETER_FAILED_PATTERN_VALIDATION")
        }
      }
    }
    
    "handle connector errors" when {
    }
  }

}
