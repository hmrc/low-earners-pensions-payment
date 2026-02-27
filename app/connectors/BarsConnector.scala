package connectors

import models.bars.{BarsRequest, BarsResponse}
import com.google.inject.Singleton
import models.CorrelationId

@Singleton
class BarsConnector extends HttpHandler[BarsResponse] {
  def checkBankAccountDetails(request: BarsRequest, correlationId: CorrelationId): ConnectorResponse[BarsResponse] = ???
}
