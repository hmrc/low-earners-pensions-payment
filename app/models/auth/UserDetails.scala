package models.auth

case class UserDetails(nino: Nino)

case class Nino(value: String)
