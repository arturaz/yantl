package yantl

/** Validates values producing one or more errors, contains [[rules]]. */
trait Validator[-Input, +Error] extends Validate[Input, Error] { self =>

  /** Rules that are used to validate the input. */
  def rules: IArray[ValidatorRule[Input, Error]]

  /** Validates the input.
    *
    * @return
    *   a [[Vector]] of errors. If the vector is empty, the input is valid.
    */
  override def validate(input: Input): Vector[Error] =
    rules.iterator.flatMap(_.validate(input)).toVector

  def mapInput[NewInput](f: NewInput => Input): Validator[NewInput, Error] =
    new {
      override val rules = self.rules.map(_.mapInput(f))
      override def validate(input: NewInput) = self.validate(f(input))
    }

  def emapInput[NewInput, NewError >: Error](
      f: NewInput => Either[NewError, Input]
  ): Validator[NewInput, NewError] = new {
    override val rules = self.rules.map(_.emapInput(f))

    override def validate(input: NewInput) = f(input) match {
      case Left(error)  => Vector(error)
      case Right(input) => self.validate(input)
    }
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

  /** Combines two validators for the same input type into one. */
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
  def errorsToString[Error](
      errors: Vector[Error]
  )(implicit asString: AsString[Error]): String = {
    if (errors.sizeIs == 1) asString.asString(errors.head)
    else
      s"""Multiple errors (${errors.size}) were encountered while validating a value:
        |
        |- ${errors.iterator
          .map(asString.asString)
          .mkString("\n\n- ")}""".stripMargin
  }
}
