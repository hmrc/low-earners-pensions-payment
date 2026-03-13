package models.bars

case class BarsRequestWithMandatoryFields(name: String,
                                          sortCode: String,
                                          accountNumber: String,
                                          rollNumber: Option[String])
