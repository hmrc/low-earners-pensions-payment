package connectors

import play.api.libs.json.Reads
import uk.gov.hmrc.http.HttpReads

trait HttpHandler[Resp: Reads] {
  implicit def httpReads: HttpReads[ConnectorResponse[Resp]] = ???
}
