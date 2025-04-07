# Provided Standard Scala Typeclass Instances

The `NewTypeGivens` trait provides standard Scala type class instances (such as `Ordering`, `Numeric`, etc.)
for newtype wrappers.

It allows you for your newtype wrappers to have operations such as `+`, `-`, `*` for free.

To use `NewTypeGiven` trait, extend it for your newtype like so:
```scala mdoc
import yantl.*

object Score extends Newtype.WithoutValidationOf[Long] with NewTypeGivens
type Score = Score.Type
```


This allows you to use `+` operator on wrappers for free like so:
```scala mdoc
val score1 = Score(50)
val score2 = Score(100)
val total = score1 + score2
```

Note that `Numeric`, `Integral` and `Fractional` typeclasses only exists for newtypes without validation.

