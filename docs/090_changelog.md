# Changelog

## v0.2.0

**2025-02-16**

- Added `Validate[-TInput, +TError]` and `Make[-TInput, +TError, +TOutput]` traits.
- `Newtype` now contains `object make extends Make[...]`.
    - Source compatibility for `MyNewType.make(10)` did not change, but it's not binary compatible with previous version.
    - `MyNewType.makeUnsafe` -> `MyNewType.make.unsafe`
    - `MyNewType.makeAsString` -> `MyNewType.make.asString`
    - `MyNewType.makeAsStrings` -> `MyNewType.make.asStrings`
    - `MyNewType.makeOrThrow` -> `MyNewType.make.orThrow`
- All of the `Make` functions that return errors as strings now require `AsString[TError]` instance.
    - You can use the default `.toString()` based implementation with `AsString.fromToString`. 

## v0.1.0

**2025-01-26**

Initial release.