package models.bars

import play.api.libs.json.{Json, Reads}

case class BarsRequestWithMandatoryFields(name: String,
                          sortCode: String,
                          accountNumber: String,
                          rollNumber: Option[String])
