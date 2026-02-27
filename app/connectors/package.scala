import cats.data.EitherT
import models.ResponseWrapper.{ErrorWrapper, SuccessWrapper}

import scala.concurrent.Future

package object connectors {
  type ConnectorResponse[R] = EitherT[Future, ErrorWrapper, SuccessWrapper[R]]
}
