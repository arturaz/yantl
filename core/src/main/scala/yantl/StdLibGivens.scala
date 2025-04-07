package yantl

/** Provides standard Scala type class instances ([[Numeric]], [[Integral]],
  * [[Fractional]]) for `Newtype` wrappers.
  */
trait StdLibGivens {

  given newtypeNumeric[TUnderlying, TWrapper](using
      newtype: yantl.Newtype.WithUnvalidatedType[TUnderlying, TWrapper],
      numeric: Numeric[TUnderlying]
  ): Numeric[TWrapper] with {
    override def plus(x: TWrapper, y: TWrapper): TWrapper =
      newtype(numeric.plus(x.unwrap, y.unwrap))

    override def minus(x: TWrapper, y: TWrapper): TWrapper =
      newtype(numeric.minus(x.unwrap, y.unwrap))

    override def times(x: TWrapper, y: TWrapper): TWrapper =
      newtype(numeric.times(x.unwrap, y.unwrap))

    override def negate(x: TWrapper): TWrapper =
      newtype(numeric.negate(x.unwrap))

    override def fromInt(x: Int): TWrapper =
      newtype(numeric.fromInt(x))

    override def parseString(str: String): Option[TWrapper] =
      numeric.parseString(str).map(newtype(_))

    override def toInt(x: TWrapper): Int = numeric.toInt(x.unwrap)

    override def toLong(x: TWrapper): Long = numeric.toLong(x.unwrap)

    override def toFloat(x: TWrapper): Float = numeric.toFloat(x.unwrap)

    override def toDouble(x: TWrapper): Double = numeric.toDouble(x.unwrap)

    override def compare(x: TWrapper, y: TWrapper): Int =
      numeric.compare(x.unwrap, y.unwrap)
  }

  given newtypeIntegral[TUnderlying, TWrapper](using
      newtype: yantl.Newtype.WithUnvalidatedType[TUnderlying, TWrapper],
      integral: Integral[TUnderlying]
  ): Integral[TWrapper] with {
    override def quot(x: TWrapper, y: TWrapper): TWrapper =
      newtype(integral.quot(x.unwrap, y.unwrap))

    override def rem(x: TWrapper, y: TWrapper): TWrapper =
      newtype(integral.rem(x.unwrap, y.unwrap))

    private val numericInstance: Numeric[TWrapper] =
      newtypeNumeric(using newtype, integral)

    override def plus(x: TWrapper, y: TWrapper): TWrapper =
      numericInstance.plus(x, y)

    override def minus(x: TWrapper, y: TWrapper): TWrapper =
      numericInstance.minus(x, y)

    override def times(x: TWrapper, y: TWrapper): TWrapper =
      numericInstance.times(x, y)

    override def negate(x: TWrapper): TWrapper = numericInstance.negate(x)

    override def fromInt(x: Int): TWrapper = numericInstance.fromInt(x)

    override def parseString(str: String): Option[TWrapper] =
      numericInstance.parseString(str)

    override def toInt(x: TWrapper): Int = numericInstance.toInt(x)

    override def toLong(x: TWrapper): Long = numericInstance.toLong(x)

    override def toFloat(x: TWrapper): Float = numericInstance.toFloat(x)

    override def toDouble(x: TWrapper): Double = numericInstance.toDouble(x)

    override def compare(x: TWrapper, y: TWrapper): Int =
      numericInstance.compare(x, y)
  }

  given newtypeFractional[TUnderlying, TWrapper](using
      newtype: yantl.Newtype.WithUnvalidatedType[TUnderlying, TWrapper],
      fractional: Fractional[TUnderlying]
  ): Fractional[TWrapper] with {
    override def div(x: TWrapper, y: TWrapper): TWrapper =
      newtype(fractional.div(x.unwrap, y.unwrap))

    private val numericInstance: Numeric[TWrapper] =
      newtypeNumeric(using newtype, fractional)

    override def plus(x: TWrapper, y: TWrapper): TWrapper =
      numericInstance.plus(x, y)

    override def minus(x: TWrapper, y: TWrapper): TWrapper =
      numericInstance.minus(x, y)

    override def times(x: TWrapper, y: TWrapper): TWrapper =
      numericInstance.times(x, y)

    override def negate(x: TWrapper): TWrapper = numericInstance.negate(x)

    override def fromInt(x: Int): TWrapper = numericInstance.fromInt(x)

    override def parseString(str: String): Option[TWrapper] =
      numericInstance.parseString(str)

    override def toInt(x: TWrapper): Int = numericInstance.toInt(x)

    override def toLong(x: TWrapper): Long = numericInstance.toLong(x)

    override def toFloat(x: TWrapper): Float = numericInstance.toFloat(x)

    override def toDouble(x: TWrapper): Double = numericInstance.toDouble(x)

    override def compare(x: TWrapper, y: TWrapper): Int =
      numericInstance.compare(x, y)
  }
}
