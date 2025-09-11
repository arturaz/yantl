package yantl

trait Newtype { self =>

  /** The type of errors that can be encountered while validating a value. */
  type TError

  /** The type that we are going to wrap. */
  type TUnderlying

  /** The wrapped type. */
  opaque type Type = TUnderlying

  /** Allows requiring this companion object in `using` clauses. */
  transparent inline given instance: Newtype.WithType[TUnderlying, Type] = this

  /** The [[Validate]] instance for this [[Newtype]]. */
  def validate: Validate[TUnderlying, TError]

  object make extends Make[TUnderlying, TError, Type] {
    override def apply(input: TUnderlying): Either[Vector[TError], Type] = {
      val errors = self.validate.validate(input)

      if (errors.isEmpty) Right(input) else Left(errors)
    }

    /** Creates a new instance of the wrapped type without validating it. */
    override def unsafe(input: TUnderlying): Type =
      input
  }
  given Make[TUnderlying, TError, Type] = make

  extension (v: Type) {

    /** Unwraps the value from the newtype to the underlying type. */
    def unwrap: TUnderlying = v
  }

  /** Composes this [[Newtype]] with another [[Newtype]] that uses [[Type]] as
    * the underlying type.
    *
    * For example: `String -> Email -> GoogleMailEmail`
    */
  def compose(
      other: Newtype.WithUnderlying[Type]
  ): Newtype.WithUnderlyingAndError[TUnderlying, TError | other.TError] =
    new Newtype {
      override type TUnderlying = self.TUnderlying
      override type TError = self.TError | other.TError

      override val validate: Validate[TUnderlying, TError] = Validate.of {
        underlying =>
          self.make(underlying) match {
            case Left(errors)        => errors
            case Right(intermediary) => other.validate.validate(intermediary)
          }
      }

      override def toString = s"ComposedNewtype($self -> $other)"
    }
}
object Newtype {

  /** Helper trait to save some typing.
    *
    * Example:
    * {{{
    * object Email extends Newtype.Of[String]
    * }}}
    */
  trait Of[TUnderlying_] extends Newtype {
    type TUnderlying = TUnderlying_
  }

  /** Helper trait to save some typing when object has validators.
    *
    * Example:
    * {{{
    * object Email extends Newtype.ValidatedOf(IArray(
    *   ValidatorRule.nonBlankString, ValidatorRule.withoutSurroundingWhitespace
    * ))
    * }}}
    */
  trait ValidatedOf[TUnderlying_, TError_](
      val validator: Validator[TUnderlying_, TError_]
  ) extends Newtype {
    type TUnderlying = TUnderlying_
    type TError = TError_

    override def validate: Validate[TUnderlying_, TError_] = validator
  }

  /** The `Aux` type helper, to easily specify the underlying type.
    *
    * Example:
    * {{{
    * def doThingsWithStrings(using newType: Newtype.WithUnderlying[String]): newType.Type
    * }}}
    */
  type WithUnderlying[TUnderlying_] = Newtype {
    type TUnderlying = TUnderlying_
  }

  type WithUnderlyingAndError[TUnderlying_, TError_] = Newtype {
    type TUnderlying = TUnderlying_
    type TError = TError_
  }

  /** The `Aux` type helper, to easily specify the underlying and wrapper types.
    *
    * Example:
    * {{{
    * def doThingsWithStrings[TWrapped](using newType: Newtype.WithType[String, TWrapped]): TWrapped
    * }}}
    */
  type WithType[TUnderlying_, TWrapper] = Newtype {
    type TUnderlying = TUnderlying_
    type Type = TWrapper
  }

  /** As [[WithType]] but with error type. */
  type WithTypeAndError[TUnderlying_, TWrapper, TError_] = Newtype {
    type TUnderlying = TUnderlying_
    type TError = TError_
    type Type = TWrapper
  }

  /** As [[WithType]] but for unvalidated newtypes. */
  type WithUnvalidatedType[TUnderlying_, TWrapper] = Newtype &
    WithoutValidation {
      type TUnderlying = TUnderlying_
      type Type = TWrapper
    }

  /** Combines [[Of]] and [[WithoutValidation]]. */
  trait WithoutValidationOf[TUnderlying_]
      extends Newtype.Of[TUnderlying_]
      with WithoutValidation

  /** Mix-in for newtypes that don't need validation logic.
    *
    * Provides an [[apply]] method that always succeeds.
    */
  trait WithoutValidation { self: Newtype =>
    type TError = Nothing

    /** Allows requiring this companion object in `using` clauses. */
    transparent inline given instanceForUnvalidated
        : Newtype.WithUnvalidatedType[TUnderlying, Type] = this

    final override def validate: Validate[TUnderlying, TError] = Validate.noOp

    // Not final as the extending type can add extra logic to making these types.
    def apply(input: TUnderlying): Type = make.apply(input) match {
      case Left(_)      => throw new Exception("impossible")
      case Right(value) => value
    }
  }
}
