package yantl

/** Interface that defines the generic newtype functionality. */
trait INewtype { self =>

  /** The type of errors that can be encountered while validating a value. */
  type TError

  /** The type that we are going to wrap. */
  type TUnderlying

  /** The wrapped type. */
  type Type

  /** Allows requiring this companion object in `using` clauses. */
  transparent inline given instance: Newtype.WithType[TUnderlying, Type] = this

  /** The [[Validate]] instance for this [[Newtype]]. */
  def validate: Validate[TUnderlying, TError]

  /** Returns the wrapped value as the underlying type.
    *
    * @note
    *   the weird name is here so it wouldn't clash with the extension method.
    */
  protected def protected_unwrap(wrapped: Type): TUnderlying

  /** Returns the underlying value as the wrapped type. */
  protected def wrap(underlying: TUnderlying): Type

  object make extends Make[TUnderlying, TError, Type] {
    override def apply(input: TUnderlying): Either[Vector[TError], Type] = {
      val errors = self.validate.validate(input)

      if (errors.isEmpty) Right(wrap(input)) else Left(errors)
    }

    /** Creates a new instance of the wrapped type without validating it. */
    override def unsafe(input: TUnderlying): Type =
      wrap(input)
  }
  given Make[TUnderlying, TError, Type] = make

  extension (v: Type) {

    /** Unwraps the value from the newtype to the underlying type. */
    def unwrap: TUnderlying = self.protected_unwrap(v)
  }

  /** Composes this [[Newtype]] with another [[Newtype]] that uses [[Type]] as
    * the underlying type.
    *
    * For example: `String -> Email -> GoogleMailEmail`
    */
  def compose(
      other: Newtype.WithUnderlying[Type]
  ): Newtype.WithTypeAndError[TUnderlying, other.Type, TError | other.TError] =
    new INewtype {
      override type TUnderlying = self.TUnderlying
      override type TError = self.TError | other.TError
      override type Type = other.Type

      override protected def protected_unwrap(
          wrapped: other.Type
      ): TUnderlying =
        self.unwrap(other.unwrap(wrapped))

      override protected def wrap(underlying: TUnderlying): other.Type =
        other.wrap(self.wrap(underlying))

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

/** Implementation of [[INewtype]] that forces [[INewtype.Type]] to be an opaque
  * type of [[TUnderlying]].
  */
trait Newtype extends INewtype { self =>

  /** The wrapped type. */
  opaque type Type = TUnderlying

  override protected final def protected_unwrap(wrapped: Type): TUnderlying =
    wrapped

  override protected final def wrap(underlying: TUnderlying): Type = underlying
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
  type WithUnderlying[TUnderlying_] = INewtype {
    type TUnderlying = TUnderlying_
  }

  type WithUnderlyingAndError[TUnderlying_, TError_] = INewtype {
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
  type WithType[TUnderlying_, TWrapper] = INewtype {
    type TUnderlying = TUnderlying_
    type Type = TWrapper
  }

  /** As [[WithType]] but with error type. */
  type WithTypeAndError[TUnderlying_, TWrapper, TError_] = INewtype {
    type TUnderlying = TUnderlying_
    type TError = TError_
    type Type = TWrapper
  }

  /** As [[WithType]] but for unvalidated newtypes. */
  type WithUnvalidatedType[TUnderlying_, TWrapper] = INewtype &
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
