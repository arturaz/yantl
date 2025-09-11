package yantl

object Age extends Newtype.ValidatedOf(Validator.of(ValidatorRule.minValue(0L)))
type Age = Age.Type

object Name extends Newtype.WithoutValidationOf[String]
type Name = Name.Type

case class NotAnEmail(email: String)
object Email
    extends Newtype.ValidatedOf(
      Validator.of(
        ValidatorRule.of((email: String) =>
          if (email.contains("@")) None else Some(NotAnEmail(email))
        )
      )
    )
type Email = Email.Type

case class NotAGoogleMail(email: Email)
object GoogleMailEmail
    extends Newtype.ValidatedOf(
      Validator.of(
        ValidatorRule.of((email: Email) =>
          if (Email.unwrap(email).endsWith("@gmail.com")) None
          else Some(NotAGoogleMail(email))
        )
      )
    )

val ChainedGoogleMailEmail = Email.compose(GoogleMailEmail)
type ChainedGoogleMailEmail = ChainedGoogleMailEmail.Type
