## Yet Another Newtype Library - yantl

Yes, this is Yet Another Newtype Library for Scala, abbreviated as `yantl`.

Is it a horrible name? Probably. But I am bad at naming things.

### Motivation

There are plently of options for newtypes in Scala, including:

- [scala-newtype](https://github.com/estatico/scala-newtype) - the original newtype library for Scala 2.x.
- [Monix newtypes](https://github.com/monix/newtypes) - macro free newtype library for Scala 2.x and 3.x.
- [neotype](https://github.com/kitlangton/neotype) - macro-based newtype library for 3.x providing compile-time
  validation.

However, they all presume that errors are strings. Which is not an option for localized applications, as you
have to translate the string to the user's language somehow, losing all type safety in the process.

### Goals

#### Be macro-free

Macros seem great. You get compile-time checks for your types. Until you realise that the compiler has to
evaluate the macro at compile time and therefore you can only use a subset of Scala.

If you need compile time literal validation, I suggest using [literally](https://github.com/typelevel/literally).

#### Support flexible composition of error types

Yantl does not care what your error type is. There are some defined by default, but you can ignore them if you
choose to do so. Yantl only cares that after validating you get either an error or the newtype.

Turns out `Either[Vector[Error1 | Error2], MyNewType]` is a great encoding for that.

Which makes this library only available for Scala 3.

## Next steps

Head over to [installation](000_installation.md).
