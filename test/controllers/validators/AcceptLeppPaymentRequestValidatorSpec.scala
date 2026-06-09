package controllers.validators

import base.SpecBase
import models.CorrelationId
import models.errors.{ErrorWrapper, FormatRequestBodyError, FormatTaxYearError, LeppError, MissingRequestBodyError, RequestBodyNotJsonError}
import models.nps.accept.AcceptLeppPaymentRequest
import models.requests.{AuthUser, IdentifierRequest}
import play.api.mvc.{AnyContent, Request}
import play.api.test.FakeRequest
import play.api.libs.json.{JsObject, JsString, Json}
import uk.gov.hmrc.domain.Nino

import scala.language.postfixOps

class AcceptLeppPaymentRequestValidatorSpec extends SpecBase {
  "AcceptLeppPaymentRequestValidator" - {
    val validator: AcceptLeppPaymentRequestValidator = new AcceptLeppPaymentRequestValidator
    "validate" - {
      def handleForScenario(scenarioName: String,
                            taxYear: String,
                            bodyOpt: Option[String],
                            expectedError: LeppError): Unit = s"should return the expected error for scenario: $scenarioName" in {
        val request: Request[AnyContent] = bodyOpt.fold(
          FakeRequest()
        )(
          body => FakeRequest().withTextBody(body)
        )

        val idRequest = IdentifierRequest[AnyContent](
          request = request,
          user = AuthUser("user-id", Nino(generateNino())),
          correlationId = testCorrelationId
        )

        val result: Either[ErrorWrapper, AcceptLeppPaymentRequest] = validator.validate(taxYear)(idRequest)
        result mustBe a[Left[_, _]]
        val dummyErrorWrapper: ErrorWrapper = ErrorWrapper(CorrelationId("dummy-id"), LeppError("N/A", "N/A"))
        result.swap.getOrElse(dummyErrorWrapper) mustBe ErrorWrapper(testCorrelationId, expectedError)
      }
      
      def optFieldToJson(key: String, valueOpt: Option[String]): JsObject = valueOpt.fold(
        JsObject.empty
      )(
        value => Json.obj(key -> JsString(value))
      )

      def requestBody(lock: Option[String] = Some("1234"),
                      hasAccountDetails: Boolean = true,
                      accountNumber: Option[String] = Some("12345678"),
                      accountName: Option[String] = Some("Name"),
                      sortCode: Option[String] = Some("123456"),
                      rollNumber: Option[String] = Some("roll")): String = {
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

        (optFieldToJson("currentLowEarnersOptimisticLock", lock) ++ accountDetailsJson).toString
      }

      val missingAccountDetailsBody: Option[String] = Some(requestBody(hasAccountDetails = false))
      val missingAccountNameBody: Option[String] = Some(requestBody(accountName = None))
      val incorrectAccountNameBody: Option[String] = Some(requestBody(accountName = Some("1234")))
      val missingAccountNumberBody: Option[String] = Some(requestBody(accountNumber = None))
      val incorrectAccountNumberBody: Option[String] = Some(requestBody(accountNumber = Some("ABCD")))
      val missingSortCodeBody: Option[String] = Some(requestBody(sortCode = None))
      val incorrectSortCodeBody: Option[String] = Some(requestBody(sortCode = Some("!!!!!")))
      val incorrectRollNumberBody: Option[String] = Some(requestBody(rollNumber = Some("####")))
        
      val jsPath: String = "/lowEarnersAccountDetails"
      val lockText: String = "currentLowEarnersOptimisticLock"

      Seq(
        ("Tax year format is incorrect", "XXXX", None, FormatTaxYearError),
        ("No request body is supplied", "2025", None, MissingRequestBodyError),
        ("Request body is not valid JSON", "2025", Some(""), RequestBodyNotJsonError),
        (s"$lockText is missing", "2025", missingAccountDetailsBody, FormatRequestBodyError(Set(s"/$lockText"))),
        (s"$lockText has incorrect format", "2025", Some(requestBody(lock = Some("ABC"))), FormatRequestBodyError(Set(s"/$lockText"))),
        ("lowEarnersAccountDetails are missing", "2025", Some(requestBody(hasAccountDetails = false)), FormatRequestBodyError(Set(jsPath))),
        ("accountNumber is missing", "2025", missingAccountNumberBody, FormatRequestBodyError(Set(s"$jsPath/accountNumber"))),
        ("accountNumber has incorrect format", "2025", incorrectAccountNumberBody, FormatRequestBodyError(Set(s"$jsPath/accountNumber"))),
        ("accountName is missing", "2025", missingAccountNameBody, FormatRequestBodyError(Set(s"$jsPath/accountName"))),
        ("accountName has incorrect format", "2025", incorrectAccountNameBody, FormatRequestBodyError(Set(s"$jsPath/accountName"))),
        ("sortCode is missing", "2025", missingSortCodeBody, FormatRequestBodyError(Set(s"$jsPath/sortCode"))),
        ("sortCode has incorrect format", "2025", incorrectSortCodeBody, FormatRequestBodyError(Set(s"$jsPath/sortCode"))),
        ("rollNumber has incorrect format", "2025", incorrectRollNumberBody, FormatRequestBodyError(Set(s"$jsPath/rollNumber"))),
      ).foreach(handleForScenario)
    }
  }
}
