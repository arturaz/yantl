# Defining Newtypes

You can define newtypes with or without validation. Newtypes are a way to create type-safe wrappers around existing types, ensuring type safety and domain correctness at compile time.

## With Validation

When you need to ensure that values conform to specific rules, use `Newtype.ValidatedOf`. The underlying type is
inferred from the validator rules:

```scala mdoc
import yantl.*

object Age extends Newtype.ValidatedOf(Validator.of(ValidatorRule.minValue(0L)))
type Age = Age.Type
```

This creates an `Age` type that:

- Wraps a `Long` value
- Ensures the value is non-negative
- Provides type safety (you can't accidentally use a raw `Long` where an `Age` is expected)

Creating validated instances:

```scala mdoc
// Safe creation
Age.make(25)
Age.make(-5)

// Creation with multiple string error messages
Age.makeAsStrings(-5)

// Creation with a single string error message
Age.makeAsString(-5)

// Performs no checking, allowing invalid values to be created
Age.makeUnsafe(-5)
```

For statically known values, you can use `makeOrThrow` to throw an exception if the value is invalid:

```scala mdoc:crash
Age.makeOrThrow(-5)
```

## Without Validation

When you just need type safety without validation rules:

```scala mdoc
object Name extends Newtype.WithoutValidationOf[String]
type Name = Name.Type
```

This creates a `Name` type that:

- Wraps a `String` value
- Provides type safety
- Has no validation rules

Creating unvalidated instances:

```scala mdoc
Name("John")
```

## Intermediatary Newtypes

Sometimes (for example, in a framework) you want to define a newtype that has some functionality, but it is not the final
type. For example, a newtype helper for non-empty strings:

```scala mdoc
/** A newtype wrapping a [[String]]. */
trait NewtypeString extends Newtype.Of[String] {
  given CanEqual[Type, Type] = CanEqual.derived

  given Ordering[Type] = Ordering.by(unwrap)
}

/** A newtype wrapping a non-empty [[String]] without surrounding whitespace. */
trait NewtypeNonEmptyString extends NewtypeString {
  // Unfortunately you have to repeat the type here
  type TError = ValidatorRule.HadSurroundingWhitespace | ValidatorRule.WasBlank

  override val validator = NewtypeNonEmptyString.validator
}
object NewtypeNonEmptyString {
  val validator = Validator.of(
    ValidatorRule.nonBlankString,
    ValidatorRule.withoutSurroundingWhitespace,
  )
}
```

Then, end-user code can use `NewtypeNonEmptyString` to get the `Ordering` instance for free:

```scala mdoc
object ForumTopic extends NewtypeNonEmptyString
type ForumTopic = ForumTopic.Type

ForumTopic.make("What are newtypes?")

ForumTopic.make("")
```

Or they can refine the type even further:

```scala mdoc
object ForumTopicStrict extends NewtypeString {
  type TError =
    ValidatorRule.HadSurroundingWhitespace | 
      ValidatorRule.WasBlank | 
      ValidatorRule.UnderMinLength[String]

  override val validator = 
    NewtypeNonEmptyString.validator and Validator.of(ValidatorRule.minLength(10))
}
type ForumTopicStrict = ForumTopicStrict.Type

ForumTopicStrict.make("What are newtypes?")

ForumTopicStrict.make("newtypes?")
```

## Best Practices

1. Always define both the object and type alias:

   ```scala mdoc:reset:silent
   object MyType extends Newtype.WithoutValidationOf[String]
   type MyType = MyType.Type  // Allows using `MyType` as your type in the codebase
   ```

2. Use validation when your type has invariants that must be maintained
3. Use `WithoutValidation` when you just need type safety
4. Use `makeOrThrow` only when you are certain the value is valid, like in statically known values
