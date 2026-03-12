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

package base

import models.ResponseWrapper.ErrorWrapper
import models.bars.{BarsAccount, BarsResponse, BarsSubject, ValidatedBarsRequest}
import models.errors.ErrorResult
import models.errors.ErrorResult.{DownstreamErrorResult, ServiceErrorResult}
import models.{CorrelationId, ResponseWrapper}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.IM_A_TEAPOT
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.http.HeaderCarrier

trait SpecBase extends AnyFreeSpec
  with Matchers
  with ScalaFutures
  with MockitoSugar 
  with FutureAwaits
  with DefaultAwaitTimeout {
  
  val testCorrelationId: CorrelationId = CorrelationId("some-id")
  implicit val dummyHeaderCarrier: HeaderCarrier = HeaderCarrier()

  val dummyServiceErrorWrapper: ResponseWrapper[ErrorResult] = ErrorWrapper(
    value = ServiceErrorResult(IM_A_TEAPOT, "TEST_ERROR"),
    correlationId = testCorrelationId
  )
  
  val dummyDownstreamErrorWrapper: ResponseWrapper[ErrorResult] = ErrorWrapper(
    value = DownstreamErrorResult(IM_A_TEAPOT, "TEST_ERROR"),
    correlationId = testCorrelationId
  )
  
  val testBarsAccount: BarsAccount = BarsAccount(
    sortCode = "11-22-33",
    accountNumber = "12345678",
    rollNumber = Some("abcdef/re")
  )
  
  val testBarsSubject: BarsSubject = BarsSubject(
    title = Some("Mr"),
    name = Some("Taxwell Payer"),
    firstName = Some("Taxwell"),
    lastName = Some("Payer")
  )
  
  val dummyValidatedBarsRequest: ValidatedBarsRequest = ValidatedBarsRequest(
    account = BarsAccount(
      sortCode = "N/A",
      accountNumber = "N/A",
      rollNumber = None
    ),
    subject = BarsSubject(
      title = None,
      name = Some("N/A"),
      firstName = None,
      lastName = None
    )
  )
  
  val testValidatedBarsRequest: ValidatedBarsRequest = ValidatedBarsRequest(account = testBarsAccount, subject = testBarsSubject)
  
  val testBarsResponse: BarsResponse = BarsResponse(
    accountNumberIsWellFormatted = "yes",
    accountExists = "yes",
    nameMatches = "yes",
    accountName = Some("Taxwell Payer"),
    nonStandardAccountDetailsRequiredForBacs = "no",
    sortCodeIsPresentOnEISCD = "yes",
    sortCodeSupportsDirectDebit = "yes",
    sortCodeSupportsDirectCredit = "yes",
    sortCodeBankName = Some("banky bank"),
    iban = Some("iban")
  )
  
  val dummyBarsResponse: BarsResponse = BarsResponse(
    accountNumberIsWellFormatted = "N/A",
    accountExists = "N/A",
    nameMatches = "N/A",
    accountName = Some("N/A"),
    nonStandardAccountDetailsRequiredForBacs = "N/A",
    sortCodeIsPresentOnEISCD = "N/A",
    sortCodeSupportsDirectDebit = "N/A",
    sortCodeSupportsDirectCredit = "N/A",
    sortCodeBankName = Some("N/A"),
    iban = Some("N/A")
  )

}
