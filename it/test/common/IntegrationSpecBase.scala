package common


import controllers.actions.IdentifierAction
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.{HeaderNames, Status}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.BodyParsers
import play.api.test.{DefaultAwaitTimeout, ResultExtractors}
import play.api.{Application, inject}
import uk.gov.hmrc.http.test.{HttpClientV2Support, WireMockSupport}
import utils.CorrelationIdOptional

class IntegrationSpecBase extends AnyWordSpec
  with Matchers
  with ScalaFutures
  with IntegrationPatience
  with DefaultAwaitTimeout
  with GuiceOneServerPerSuite
  with WireMockSupport 
  with ResultExtractors
  with HeaderNames
  with Status
  with HttpClientV2Support {

  val parsers: BodyParsers.Default = app.injector.instanceOf[BodyParsers.Default]

  override def fakeApplication(): Application = {
    GuiceApplicationBuilder()
      .overrides(
        inject.bind[IdentifierAction].toInstance(new FakeIdentifierAction(parsers))
      )
      .configure(
        "microservice.services.bars.port" -> wireMockPort,
        "microservice.services.bars.host" -> wireMockHost
      )
      .build()
  }

}
