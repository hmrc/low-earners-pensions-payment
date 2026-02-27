package services

import com.google.inject.{Inject, Singleton}
import connectors.{BarsConnector, ConnectorResponse}
import models.CorrelationId
import models.bars.{BarsRequest, BarsResponse}

@Singleton
class BarsService @Inject()(connector: BarsConnector) {
  def checkBankAccountDetails(barsRequest: BarsRequest, correlationId: CorrelationId): ConnectorResponse[BarsResponse] =
    connector.checkBankAccountDetails(barsRequest, correlationId)
}
