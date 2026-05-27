package utils

object OptionUtils {
  def optFromSeq[A](seq: Seq[A]): Option[Seq[A]] = seq match {
    case Nil => None
    case nonEmpty => Some(nonEmpty)
  }
}
