# Validator Rules

Each `ValidatorRule` applies a single validation. Many rules are combined to form a `Validator`.

## Built-in Rules

The `ValidatorRule` object provides several built-in rules:

```scala mdoc
import yantl.*

val minValueValidator = ValidatorRule.minValue(0L)

val maxValueValidator = ValidatorRule.maxValue(100L)

val betweenValidator = ValidatorRule.between(0L, 100L)

val minLengthOfVectorValidator = ValidatorRule.minLength(2, getLength = (_: Vector[String]).size)

val maxLengthOfVectorValidator = ValidatorRule.maxLength(50, getLength = (_: Vector[String]).size)

val minLengthValidator = ValidatorRule.minLength(2)

val maxLengthValidator = ValidatorRule.maxLength(50)

val nonEmptyValidator = ValidatorRule.nonEmpty(isEmpty = (_: Vector[String]).isEmpty)

val nonEmptyStringValidator = ValidatorRule.nonEmptyString

val nonBlankStringValidator = ValidatorRule.nonBlankString

val withoutSurroundingWhitespaceValidator = ValidatorRule.withoutSurroundingWhitespace
```

## Custom Rules

You can define your own `ValidatorRule` using the `ValidatorRule.of` method:

```scala mdoc

case class NotEven(value: Long)
val isEven = ValidatorRule.of((v: Long) => if v % 2 == 0 then None else Some(NotEven(v)))

```

## Validator

The `Validator` object is used to combine multiple `ValidatorRule` objects into a single validator.

```scala mdoc
import yantl.*

val validator = Validator.of(
  ValidatorRule.minValue(0L), ValidatorRule.maxValue(100L), isEven
)

validator.validate(50)

validator.validate(51)

validator.validate(101)
```