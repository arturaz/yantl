package yantl

/** Trait that defines validation of [[TInput]] values potentially producing
  * [[TError]]s.
  *
  * @tparam TInput
  *   The type from which we validate values.
  *
  * @tparam TError
  *   The type of errors that can be encountered while validating a value.
  */
trait Validate[-TInput, +TError] { self =>

  /** Validates the input.
    *
    * @return
    *   a [[Vector]] of errors. If the vector is empty, the input is valid.
    */
  def validate(input: TInput): Vector[TError]

  /** Validates the input, returning `None` if it is valid. */
  def validateAsOption(input: TInput): Option[Vector[TError]] = {
    val errors = validate(input)
    if (errors.isEmpty) None else Some(errors)
  }

  /** Validates the input, returning `Left` if it is invalid. */
  def validateAsEither(input: TInput): Either[Vector[TError], Unit] = {
    val errors = validate(input)
    if (errors.isEmpty) Right(()) else Left(errors)
  }

  /** Checks if the input is valid. */
  def isValid(input: TInput): Boolean =
    validate(input).isEmpty

  /** Variant of [[validate]] that returns errors as a [[Vector]] of strings. */
  def validateAsStrings(input: TInput)(implicit
      asString: AsString[TError]
  ): Vector[String] =
    validate(input).map(asString.asString)

  /** Variant of [[validate]] that returns errors as a single English string. */
  def validateAsString(
      input: TInput
  )(using asString: AsString[TError]): Option[String] = {
    val errors = validate(input)
    if (errors.isEmpty) None else Some(Validator.errorsToString(errors))
  }

  /** Changes the type of input. */
  def mapValidateInput[NewInput](
      f: NewInput => TInput
  ): Validate[NewInput, TError] =
    input => self.validate(f(input))

  /** Contramaps this [[Validate]], changing the input and error types.
    *
    * This is more powerful than [[mapValidateBoth]] as it allows you to add
    * extra validations to input.
    */
  def emapValidateInput[NewInput, NewError >: TError](
      f: NewInput => Either[Vector[NewError], TInput]
  ): Validate[NewInput, NewError] =
    input => {
      f(input) match {
        case Left(errors) => errors
        case Right(input) => self.validate(input)
      }
    }

  /** Changes the type of the validation error. */
  def mapValidateError[NewError](
      f: TError => NewError
  ): Validate[TInput, NewError] =
    input => self.validate(input).map(f)

  /** Changes the type of input and the validation error. */
  def mapValidateBoth[NewInput, NewError](
      inputMapper: NewInput => TInput,
      errorMapper: TError => NewError
  ): Validate[NewInput, NewError] = input =>
    self.validate(inputMapper(input)).map(errorMapper)

  /** Combines two [[Validate]] instances into one. */
  infix def and[OtherInput <: TInput, OtherError](
      that: Validate[OtherInput, OtherError]
  ): Validate[OtherInput, TError | OtherError] =
    input => self.validate(input) ++ that.validate(input)
}
object Validate {

  /** Creates an instance from a function. */
  def of[TInput, TError](
      f: TInput => Vector[TError]
  ): Validate[TInput, TError] = (input: TInput) => f(input)

  /** Does not validate. */
  val noOp: Validate[Any, Nothing] = of(_ => Vector.empty)
}
