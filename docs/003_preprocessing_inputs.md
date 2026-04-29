# Preprocessing Inputs

Sometimes you need to process the inputs to the newtype before validating them.

One particular case is emails. Email addresses are not case-sensitive, yet users might enter then capitalized.

Therefore, Yantl provides a way to preprocess the inputs.

```scala mdoc
import yantl.*

object Email extends Newtype.WithoutValidationOf[String] {
  override protected val resolveMake =
    super.resolveMake.mapValidateInput((input: String) =>
      input.toLowerCase()
    )
}

Email("John.Smith@gMail.com")
```