package yantl

/** Validates values producing one or more errors. */
trait Validator[-Input, +Error] { self =>

  /** Rules that are used to validate the input. */
  def rules: IArray[ValidatorRule[Input, Error]]

  /** Validates the input.
    *
    * @return
    *   a [[Vector]] of errors. If the vector is empty, the input is valid.
    */
  def validate(input: Input): Vector[Error] =
    rules.iterator.flatMap(_.validate(input)).toVector

  /** Validates the input, returning `None` if it is valid. */
  def validateAsOption(input: Input): Option[Vector[Error]] = {
    val errors = validate(input)
    if (errors.isEmpty) None else Some(errors)
  }

  /** Validates the input, returning `Left` if it is invalid. */
  def validateAsEither(input: Input): Either[Vector[Error], Unit] = {
    val errors = validate(input)
    if (errors.isEmpty) Right(()) else Left(errors)
  }

  /** Checks if the input is valid. */
  def isValid(input: Input): Boolean =
    validate(input).isEmpty

  /** Variant of [[validate]] that returns errors as a [[Vector]] of strings. */
  def validateAsStrings(input: Input): Vector[String] =
    validate(input).map(_.toString)

  /** Variant of [[validate]] that returns errors as a single English string. */
  def validateAsString(input: Input): Option[String] = {
    val errors = validate(input)
    if (errors.isEmpty) None else Some(Validator.errorsToString(errors))
  }

  def mapInput[NewInput](f: NewInput => Input): Validator[NewInput, Error] =
    new {
      override val rules = self.rules.map(_.mapInput(f))
      override def validate(input: NewInput) = self.validate(f(input))
    }

  def mapError[NewError](f: Error => NewError): Validator[Input, NewError] =
    new {
      override val rules = self.rules.map(_.mapError(f))
      override def validate(input: Input) = self.validate(input).map(f)
    }

  def mapBoth[NewInput, NewError](
      inputMapper: NewInput => Input,
      errorMapper: Error => NewError
  ): Validator[NewInput, NewError] = new {
    override val rules = self.rules.map(_.mapBoth(inputMapper, errorMapper))
    override def validate(input: NewInput) =
      self.validate(inputMapper(input)).map(errorMapper)
  }

  /** Combines two validators into one. */
  infix def and[OtherInput <: Input, OtherError](
      that: Validator[OtherInput, OtherError]
  ): Validator[OtherInput, Error | OtherError] = new {
    override val rules = self.rules ++ that.rules
    override def validate(input: OtherInput) =
      self.validate(input) ++ that.validate(input)
  }
}
object Validator {
  def of[Input, Error](
      rules: ValidatorRule[Input, Error]*
  ): Validator[Input, Error] = {
    fromRules(IArray(rules*))
  }

  def fromRules[Input, Error](
      rules: IArray[ValidatorRule[Input, Error]]
  ): Validator[Input, Error] = {
    val rules_ = rules
    new {
      override val rules = rules_
    }
  }

  given [Input, Error]: Conversion[IArray[
    ValidatorRule[Input, Error]
  ], Validator[Input, Error]] =
    fromRules

  given [Input, Error]
      : Conversion[ValidatorRule[Input, Error], Validator[Input, Error]] =
    rule => fromRules(IArray(rule))

  /** Does not validate. */
  val noOp: Validator[Any, Nothing] = fromRules(IArray.empty)

  /** Converts a [[Vector]] of errors into an English string, assuming
    * `Error.toString` returns an English string.
    */
  def errorsToString[Error](errors: Vector[Error]): String = {
    if (errors.sizeIs == 1) errors.head.toString
    else
      s"""Multiple errors (${errors.size}) were encountered while validating a value:
        |
        |- ${errors.mkString("\n\n- ")}""".stripMargin
  }
}
