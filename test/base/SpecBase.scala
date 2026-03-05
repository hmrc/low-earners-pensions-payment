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

import models.{CorrelationId, ResponseWrapper}
import models.ResponseWrapper.ErrorWrapper
import models.errors.ErrorResult
import models.errors.ErrorResult.DownstreamErrorResult
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import play.api.http.Status.IM_A_TEAPOT
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

trait SpecBase extends AnyFreeSpec with FutureAwaits with DefaultAwaitTimeout with Matchers {
  val testCorrelationId: CorrelationId = CorrelationId("some-id")
  
  val dummyDownstreamErrorWrapper: ResponseWrapper[ErrorResult] = ErrorWrapper(
    value = DownstreamErrorResult(IM_A_TEAPOT, "TEST_ERROR"),
    correlationId = testCorrelationId
  )
}
