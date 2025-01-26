package yantl

object Age extends Newtype.ValidatedOf(Validator.of(ValidatorRule.minValue(0L)))
type Age = Age.Type

object Name extends Newtype.WithoutValidationOf[String]
type Name = Name.Type
