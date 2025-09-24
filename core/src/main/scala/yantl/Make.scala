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
  self =>

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

  override def mapValidateInput[NewInput](
      f: NewInput => TInput
  ): Make[NewInput, TError, TOutput] = new {
    override def apply(input: NewInput): Either[Vector[TError], TOutput] =
      self.apply(f(input))
    override def unsafe(input: NewInput): TOutput = self.unsafe(f(input))
  }

  override def mapValidateError[NewError](
      f: TError => NewError
  ): Make[TInput, NewError, TOutput] = new {
    override def apply(input: TInput): Either[Vector[NewError], TOutput] =
      self.apply(input).left.map(_.map(f))
    override def unsafe(input: TInput): TOutput = self.unsafe(input)
  }

  /** Maps the [[Make]] changing the input type and adding extra validation. */
  def mapInputWithExtraValidation[NewInput, NewError >: TError](
    mapValidated: NewInput => Either[Vector[NewError], TInput],
    mapUnsafe: NewInput => TInput
  ): Make[NewInput, NewError, TOutput] = new {
    override def apply(input: NewInput): Either[Vector[NewError], TOutput] = mapValidated(
      input
    ) match {
      case Left(errors) => Left(errors)
      case Right(input) => self.apply(input)
    }

    override def unsafe(input: NewInput): TOutput = self.unsafe(mapUnsafe(input))
  }

  override def mapValidateBoth[NewInput, NewError](
      inputMapper: NewInput => TInput,
      errorMapper: TError => NewError
  ): Make[NewInput, NewError, TOutput] = new {
    override def apply(input: NewInput): Either[Vector[NewError], TOutput] =
      self.apply(inputMapper(input)).left.map(_.map(errorMapper))

    override def unsafe(input: NewInput): TOutput =
      self.unsafe(inputMapper(input))
  }
}
object Make {
  extension [TInput, TError, TOutput](make: Make[TInput, TError, TOutput]) {

    /** Maps the [[Make]] adding extra validation to the input, but keeping the input type the same. */
    def mapExtraValidation[NewError >: TError](
        f: TInput => Either[Vector[NewError], TInput]
    ): Make[TInput, NewError, TOutput] = new {
      override def apply(input: TInput): Either[Vector[NewError], TOutput] = f(
        input
      ) match {
        case Left(errors) => Left(errors)
        case Right(input) => make.apply(input)
      }

      override def unsafe(input: TInput): TOutput = make.unsafe(input)
    }
  }
}
