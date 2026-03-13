package controllers.validators

import models.errors.{MultipleValidationErrors, SingleValidationError, ValidationError}

import scala.annotation.tailrec
import scala.util.matching.Regex

object ValidationUtils {

  @tailrec
  def stripCharacters(field: String, characters: Seq[String]): String = characters match {
    case Nil => field
    case head :: tail => stripCharacters(field.replace(head, ""), tail)
  }
  
  protected[validators] def assertMandatoryFieldExists[T](fieldOpt: Option[T],
                                                          path: String): Either[ValidationError, T] =
    fieldOpt.fold(
      Left[ValidationError, T](
        SingleValidationError(
          code = "REQUEST_MISSING_MANDATORY_FIELD",
          path = path
        )
      ).withRight
    )(field => Right[ValidationError, T](field))

  protected[validators] def assertStringLengthValid(field: String,
                                                    minLength: Int,
                                                    maxLength: Int,
                                                    path: String): Option[SingleValidationError] = {
    val fieldLengthMessage: String = "REQUEST_PARAMETER_FAILED_LENGTH_VALIDATION"
    val length = field.length
    if (length >= minLength && length <= maxLength) None else Some(SingleValidationError(fieldLengthMessage, path))
  }
  
  protected[validators] def assertStringMatchesPattern(field: String,
                                                       format: Regex,
                                                       path: String): Option[SingleValidationError] = {
    val fieldFormatMessage: String = "REQUEST_PARAMETER_FAILED_PATTERN_VALIDATION"
    if (format.matches(field)) None else Some(SingleValidationError(fieldFormatMessage, path))
  }

  def assertStringFormatValid(field: String,
                              minLength: Int,
                              maxLength: Int,
                              regex: Regex,
                              path: String): Option[ValidationError] =
    assertStringLengthValid(field, minLength, maxLength, path) orElse
      assertStringMatchesPattern(field, regex, path)
}
