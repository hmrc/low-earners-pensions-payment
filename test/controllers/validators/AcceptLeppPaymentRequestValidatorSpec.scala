package controllers.validators

import base.SpecBase
import models.CorrelationId
import models.errors.{ErrorWrapper, FormatRequestBodyError, FormatTaxYearError, LeppError, MissingRequestBodyError, RequestBodyNotJsonError}
import models.nps.accept.AcceptLeppPaymentRequest
import models.requests.{AuthUser, IdentifierRequest}
import play.api.mvc.{AnyContent, AnyContentAsJson, Request}
import play.api.test.FakeRequest
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import uk.gov.hmrc.domain.Nino

import scala.language.postfixOps

class AcceptLeppPaymentRequestValidatorSpec extends SpecBase {
  "AcceptLeppPaymentRequestValidator" - {
    val validator: AcceptLeppPaymentRequestValidator = new AcceptLeppPaymentRequestValidator
    "validate" - {
      val nino: Nino = Nino(generateNino())
      
      def idRequest(underlying: Request[AnyContent]) = IdentifierRequest[AnyContent](
        request = underlying,
        user = AuthUser("user-id", nino),
        correlationId = testCorrelationId
      )
      
      "should handle correctly when tax year format is incorrect" in {
        val req: IdentifierRequest[AnyContent] = idRequest(FakeRequest())
        val result: Either[ErrorWrapper, AcceptLeppPaymentRequest] = validator.validate("XXXX")(idRequest(req))
        result mustBe a[Left[_, _]]
        val dummyErrorWrapper: ErrorWrapper = ErrorWrapper(CorrelationId("dummy-id"), LeppError("N/A", "N/A"))
        result.swap.getOrElse(dummyErrorWrapper) mustBe ErrorWrapper(testCorrelationId, FormatTaxYearError)
      }
      
      "should handle when request body is missing" in {
        val req: IdentifierRequest[AnyContent] = idRequest(FakeRequest())
        val result: Either[ErrorWrapper, AcceptLeppPaymentRequest] = validator.validate("1234")(idRequest(req))
        result mustBe a[Left[_, _]]
        val dummyErrorWrapper: ErrorWrapper = ErrorWrapper(CorrelationId("dummy-id"), LeppError("N/A", "N/A"))
        result.swap.getOrElse(dummyErrorWrapper) mustBe ErrorWrapper(testCorrelationId, MissingRequestBodyError)
      }
      
      "should handle with request body is not valid JSON" in {
        val req: IdentifierRequest[AnyContent] = idRequest(FakeRequest().withTextBody("not json"))
        val result: Either[ErrorWrapper, AcceptLeppPaymentRequest] = validator.validate("1234")(idRequest(req))
        result mustBe a[Left[_, _]]
        val dummyErrorWrapper: ErrorWrapper = ErrorWrapper(CorrelationId("dummy-id"), LeppError("N/A", "N/A"))
        result.swap.getOrElse(dummyErrorWrapper) mustBe ErrorWrapper(testCorrelationId, RequestBodyNotJsonError)
      }
      
      def handleForScenario(scenarioName: String,
                            taxYear: String,
                            body: JsValue,
                            expectedError: LeppError): Unit = s"should return the expected error for scenario: $scenarioName" in {
        val request: Request[AnyContentAsJson] = FakeRequest().withJsonBody(body)

        val result: Either[ErrorWrapper, AcceptLeppPaymentRequest] = validator.validate(taxYear)(idRequest(request))
        result mustBe a[Left[_, _]]
        val dummyErrorWrapper: ErrorWrapper = ErrorWrapper(CorrelationId("dummy-id"), LeppError("N/A", "N/A"))
        result.swap.getOrElse(dummyErrorWrapper) mustBe ErrorWrapper(testCorrelationId, expectedError)
      }
      
      def optFieldToJson(key: String, valueOpt: Option[String]): JsObject = 
        valueOpt.fold(JsObject.empty)(value => Json.obj(key -> JsString(value)))

      def requestBody(lock: Option[String] = Some("1234"),
                      hasAccountDetails: Boolean = true,
                      accountNumber: Option[String] = Some("12345678"),
                      accountName: Option[String] = Some("Name"),
                      sortCode: Option[String] = Some("123456"),
                      rollNumber: Option[String] = Some("ROLL")): JsValue = {
        val accountDetailsObject: JsObject =
          optFieldToJson("accountNumber", accountNumber) ++
            optFieldToJson("accountName", accountName) ++
            optFieldToJson("sortCode", sortCode) ++
            optFieldToJson("rollNumber", rollNumber)

        val accountDetailsJson = if (hasAccountDetails) {
          Json.obj(
            "lowEarnersAccountDetails" -> accountDetailsObject
          )
        } else {
          JsObject.empty
        }

        optFieldToJson("currentLowEarnersOptimisticLock", lock) ++ accountDetailsJson
      }

      val jsPath: String = "/lowEarnersAccountDetails"
      val lockText: String = "currentLowEarnersOptimisticLock"

      val missingLockBody: JsValue = requestBody(lock = None)
      val incorrectLockBody: JsValue = requestBody(lock = Some("abc"))
      val missingAccountDetailsBody: JsValue = requestBody(hasAccountDetails = false)
      val missingAccountNameBody: JsValue = requestBody(accountName = None)
      val incorrectAccountNameBody: JsValue = requestBody(accountName = Some("!!!"))
      val missingAccountNumberBody: JsValue = requestBody(accountNumber = None)
      val incorrectAccountNumberBody: JsValue = requestBody(accountNumber = Some("ABCD"))
      val missingSortCodeBody: JsValue = requestBody(sortCode = None)
      val incorrectSortCodeBody: JsValue = requestBody(sortCode = Some("!!!!!"))
      val incorrectRollNumberBody: JsValue = requestBody(rollNumber = Some("####"))
      
      val multipleErrorsBody: JsValue = requestBody(
        lock = None,
        accountNumber = None,
        accountName = None,
        sortCode = None,
        rollNumber = Some("!!!!")
      )
      
      val multipleErrorsPaths: Set[String] = Set(
        s"/$lockText",
        s"$jsPath/accountNumber",
        s"$jsPath/accountName",
        s"$jsPath/sortCode",
        s"$jsPath/rollNumber"
      )

      Seq(
        (s"$lockText is missing", "2025", missingLockBody, FormatRequestBodyError(Set(s"/$lockText"))),
        (s"$lockText has incorrect format", "2025", incorrectLockBody, FormatRequestBodyError(Set(s"/$lockText"))),
        ("lowEarnersAccountDetails are missing", "2025", missingAccountDetailsBody, FormatRequestBodyError(Set(jsPath))),
        ("accountNumber is missing", "2025", missingAccountNumberBody, FormatRequestBodyError(Set(s"$jsPath/accountNumber"))),
        ("accountNumber has incorrect format", "2025", incorrectAccountNumberBody, FormatRequestBodyError(Set(s"$jsPath/accountNumber"))),
        ("accountName is missing", "2025", missingAccountNameBody, FormatRequestBodyError(Set(s"$jsPath/accountName"))),
        ("accountName has incorrect format", "2025", incorrectAccountNameBody, FormatRequestBodyError(Set(s"$jsPath/accountName"))),
        ("sortCode is missing", "2025", missingSortCodeBody, FormatRequestBodyError(Set(s"$jsPath/sortCode"))),
        ("sortCode has incorrect format", "2025", incorrectSortCodeBody, FormatRequestBodyError(Set(s"$jsPath/sortCode"))),
        ("rollNumber has incorrect format", "2025", incorrectRollNumberBody, FormatRequestBodyError(Set(s"$jsPath/rollNumber"))),
        ("multiple errors", "2025", multipleErrorsBody, FormatRequestBodyError(multipleErrorsPaths)),
      ).foreach(handleForScenario)
    }
  }
}
