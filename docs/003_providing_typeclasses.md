# Providing Typeclasses

Imagine you have a typeclass for serialization, for example:
```scala mdoc
trait SerializeToString[T] {
  def serialize(t: T): String
}
object SerializeToString {
  given SerializeToString[String] = str => str
  given SerializeToString[Long] = _.toString
}

trait DeserializeFromString[T] {
  def deserialize(s: String): Either[String, T]
}
object DeserializeFromString {
  given DeserializeFromString[String] = Right(_)
  given DeserializeFromString[Long] = str => str.toLongOption.toRight(s"Not a long: $str")
}
```

And now you have your newtypes:
```scala mdoc
import yantl.*

object Age extends Newtype.ValidatedOf(IArray(ValidatorRule.minValue(0L)))
type Age = Age.Type

object Name extends Newtype.WithoutValidationOf[String]
type Name = Name.Type
```

Wouldn't it be nice if these typeclass instances could be provided for your newtypes automatically?

```scala mdoc:nest
given newTypeSerializeToString[TUnderlying, TWrapper](using
  newType: yantl.Newtype.WithType[TUnderlying, TWrapper],
  serializer: SerializeToString[TUnderlying],
): SerializeToString[TWrapper] =
  t => serializer.serialize(t.unwrap)

given newTypeDeserializeFromString[TUnderlying, TWrapper](using
  newType: yantl.Newtype.WithType[TUnderlying, TWrapper],
  deserializer: DeserializeFromString[TUnderlying],
): DeserializeFromString[TWrapper] =
  str => deserializer.deserialize(str).flatMap(newType.makeAsString)

// The instances are automatically provided
summon[SerializeToString[Age]]
summon[DeserializeFromString[Age]]

summon[SerializeToString[Name]]
summon[DeserializeFromString[Name]]
```

You can also provide instances only for unvalidated newtypes:
```scala mdoc:nest
given newTypeDeserializeUnvalidatedFromString[TUnderlying, TWrapper](using
  newType: yantl.Newtype.WithUnvalidatedType[TUnderlying, TWrapper],
  deserializer: DeserializeFromString[TUnderlying],
): DeserializeFromString[TWrapper] =
  str => deserializer.deserialize(str).map(newType.apply)

// The instance is automatically provided
summon[DeserializeFromString[Name]]
```