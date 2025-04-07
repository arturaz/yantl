# Standard Given Instances

The `StandardGivenInstances` trait provides standard Scala type class instances `Numeric`, `Integral`, `Fractional`
for newtype wrappers (only works for newtypes which do not have validation).

## Examples

For the upcoming examples let's define some newtypes and values:
```scala mdoc
import yantl.*

object Score extends Newtype.WithoutValidationOf[Long] with StandardGivenInstances
type Score = Score.Type

object ScoreFloat extends Newtype.WithoutValidationOf[Float] with StandardGivenInstances
type ScoreFloat = ScoreFloat.Type

val score1 = Score(50)
val score2 = Score(100)
val scoreFloat1 = ScoreFloat(50)
val scoreFloat2 = ScoreFloat(100)
```

### Numeric
Brings `+`, `-`, `*` operations:

```scala mdoc
val addition = score1 + score2
val subtraction = score1 - score2
val multiplication = score1 * score2
```

### Integral
Brings `/` and `%` operations for natural numbers (like `Int`, `Long`)

```scala mdoc
val division = score1 / score2
val modulus = score1 % score2
```

### Fractional
Brings `/` operation for real numbers (like `Float`, `Double`)

```scala mdoc
val divisionFloat = scoreFloat1 / scoreFloat2
```




