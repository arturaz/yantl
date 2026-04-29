# Localized Error Messages

Eventually you have to turn `Error1 | Error2 | Error3` into something that is
readable by the user.

The best way I have found so far is to use [union-derivation](https://github.com/iRevive/union-derivation).

## Example

### Defining the localization trait

```scala mdoc
import yantl.*
import io.github.irevive.union.derivation.*

enum LocaleEnum { case En }

trait LocalizedTextOfValue[A] {
  def text(a: A): LocaleEnum ?=> String
}
object LocalizedTextOfValue {
  /** Creates a new instance. */
  def of[A](localize: (A, LocaleEnum) => String): LocalizedTextOfValue[A] = new {
    override def text(value: A): LocaleEnum ?=> String = 
      (locale: LocaleEnum) ?=> localize(value, locale)
  }

  inline given derivedUnion[A](using IsUnion[A]): LocalizedTextOfValue[A] =
    UnionDerivation.derive[LocalizedTextOfValue, A]
}

extension [A](a: A) {
  def localized(using loc: LocalizedTextOfValue[A], locale: LocaleEnum): String = loc.text(a)
}
```

### Defining the newtype

```scala mdoc
object Age extends Newtype.ValidatedOf(Validator.of(ValidatorRule.between(0L, 100L)))
type Age = Age.Type
```

### Defining the error messages

```scala mdoc
given LocalizedTextOfValue[ValidatorRule.SmallerThan[Long]] = LocalizedTextOfValue.of {
  case (err, LocaleEnum.En) => s"Must be actually born."
}

given LocalizedTextOfValue[ValidatorRule.LargerThan[Long]] = LocalizedTextOfValue.of {
  case (err, LocaleEnum.En) => s"Sorry, too old. Must be under ${err.maximum}, was ${err.actual}."
}
```

### Localizing the error message

```scala mdoc
given LocaleEnum = LocaleEnum.En

Age.make(-1).left.map(errors => errors.map(_.localized))

Age.make(105).left.map(errors => errors.map(_.localized))
```