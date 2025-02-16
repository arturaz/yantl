package yantl

/** Converts a value to a string. */
trait AsString[-A] {
  def asString(a: A): String
}
object AsString {

  /** Uses `.toString` to convert a value to a string.
    *
    * Example:
    * {{{
    *   given [A]: AsString[A] = AsString.fromToString
    * }}}
    */
  def fromToString[A]: AsString[A] = _.toString
}
