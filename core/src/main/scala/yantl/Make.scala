package yantl

/** Creates values of [[TOutput]] from [[TInput]] validating them first. Errors
  * are reported as [[TError]].
  *
  * @tparam TInput
  *   The type from which we make values.
  *
  * @tparam TError
  *   The type of errors that can be encountered while validating a value.
  *
  * @tparam TOutput
  *   The validated type.
  */
trait Make[-TInput, +TError, +TOutput] extends Validate[TInput, TError] {

  /** Creates a new instance of the wrapped type, validating it first.
    *
    * @return
    *   [[Left]] if there were errors, [[Right]] otherwise
    */
  def apply(input: TInput): Either[Vector[TError], TOutput]

  /** Creates a new instance of the wrapped type without validating it. */
  def unsafe(input: TInput): TOutput

  /** Variant of [[apply]] that returns errors as a [[Vector]] of strings. */
  def asStrings(input: TInput)(using
      asString: AsString[TError]
  ): Either[Vector[String], TOutput] =
    apply(input).left.map(_.map(err => asString.asString(err)))

  /** Variant of [[apply]] that returns errors as a single English string. */
  def asString(input: TInput)(using AsString[TError]): Either[String, TOutput] =
    apply(input).left.map(Validator.errorsToString)

  /** Creates a new instance of the wrapped type, validating it first.
    *
    * Should be used only in cases where the input is guaranteed to be valid,
    * like in statically known values.
    *
    * @throws IllegalArgumentException
    *   if there were errors
    */
  def orThrow(input: TInput)(using AsString[TError]): TOutput =
    asString(input) match {
      case Left(value)  => throw new IllegalArgumentException(value)
      case Right(value) => value
    }

  override def validate(input: TInput): Vector[TError] =
    apply(input) match {
      case Left(value) => value
      case Right(_)    => Vector.empty
    }
}
