package yantl

/** Provides standard Scala type class instances (`Numeric`, `Ordering`, etc.) for `NewType` wrappers. */
trait NewTypeGivens extends scala.math.Numeric.ExtraImplicits
  with scala.math.Integral.ExtraImplicits
  with scala.math.Fractional.ExtraImplicits
object NewTypeGivens {
  given newTypeOrdering[TUnderlying, TWrapper](using
    yantl.Newtype.WithType[TUnderlying, TWrapper],
    Ordering[TUnderlying]
  ): Ordering[TWrapper] = Ordering.by(_.unwrap)

  given newTypeNumeric[TUnderlying, TWrapper](using
    newType: yantl.Newtype.WithUnvalidatedType[TUnderlying, TWrapper],
    numeric: Numeric[TUnderlying]
  ): Numeric[TWrapper] = new Numeric[TWrapper] {
    override def plus(x: TWrapper, y: TWrapper): TWrapper =
      newType.make.unsafe(numeric.plus(x.unwrap, y.unwrap))

    override def minus(x: TWrapper, y: TWrapper): TWrapper =
      newType.make.unsafe(numeric.minus(x.unwrap, y.unwrap))

    override def times(x: TWrapper, y: TWrapper): TWrapper =
      newType.make.unsafe(numeric.times(x.unwrap, y.unwrap))

    override def negate(x: TWrapper): TWrapper =
      newType.make.unsafe(numeric.negate(x.unwrap))

    override def fromInt(x: Int): TWrapper =
      newType.make.unsafe(numeric.fromInt(x))

    override def parseString(str: String): Option[TWrapper] =
      numeric.parseString(str).map(newType.make.unsafe(_))

    override def toInt(x: TWrapper): Int = numeric.toInt(x.unwrap)
    override def toLong(x: TWrapper): Long = numeric.toLong(x.unwrap)
    override def toFloat(x: TWrapper): Float = numeric.toFloat(x.unwrap)
    override def toDouble(x: TWrapper): Double = numeric.toDouble(x.unwrap)
    override def compare(x: TWrapper, y: TWrapper): Int = numeric.compare(x.unwrap, y.unwrap)
  }

  given newTypeIntegral[TUnderlying, TWrapper](using
    newType: yantl.Newtype.WithUnvalidatedType[TUnderlying, TWrapper],
    integral: Integral[TUnderlying]
  ): Integral[TWrapper] = new Integral[TWrapper] {
    override def quot(x: TWrapper, y: TWrapper): TWrapper =
      newType.make.unsafe(integral.quot(x.unwrap, y.unwrap))

    override def rem(x: TWrapper, y: TWrapper): TWrapper =
      newType.make.unsafe(integral.rem(x.unwrap, y.unwrap))

    private val numericInstance: Numeric[TWrapper] =
      newTypeNumeric(using newType, integral)

    override def plus(x: TWrapper, y: TWrapper): TWrapper = numericInstance.plus(x, y)
    override def minus(x: TWrapper, y: TWrapper): TWrapper = numericInstance.minus(x, y)
    override def times(x: TWrapper, y: TWrapper): TWrapper = numericInstance.times(x, y)
    override def negate(x: TWrapper): TWrapper = numericInstance.negate(x)
    override def fromInt(x: Int): TWrapper = numericInstance.fromInt(x)
    override def parseString(str: String): Option[TWrapper] = numericInstance.parseString(str)
    override def toInt(x: TWrapper): Int = numericInstance.toInt(x)
    override def toLong(x: TWrapper): Long = numericInstance.toLong(x)
    override def toFloat(x: TWrapper): Float = numericInstance.toFloat(x)
    override def toDouble(x: TWrapper): Double = numericInstance.toDouble(x)
    override def compare(x: TWrapper, y: TWrapper): Int = numericInstance.compare(x, y)
  }

  given newTypeFractional[TUnderlying, TWrapper](using
    newType: yantl.Newtype.WithUnvalidatedType[TUnderlying, TWrapper],
    fractional: Fractional[TUnderlying]
  ): Fractional[TWrapper] = new Fractional[TWrapper] {
    override def div(x: TWrapper, y: TWrapper): TWrapper =
      newType.make.unsafe(fractional.div(x.unwrap, y.unwrap))

    private val numericInstance: Numeric[TWrapper] =
      newTypeNumeric(using newType, fractional)

    override def plus(x: TWrapper, y: TWrapper): TWrapper = numericInstance.plus(x, y)
    override def minus(x: TWrapper, y: TWrapper): TWrapper = numericInstance.minus(x, y)
    override def times(x: TWrapper, y: TWrapper): TWrapper = numericInstance.times(x, y)
    override def negate(x: TWrapper): TWrapper = numericInstance.negate(x)
    override def fromInt(x: Int): TWrapper = numericInstance.fromInt(x)
    override def parseString(str: String): Option[TWrapper] = numericInstance.parseString(str)
    override def toInt(x: TWrapper): Int = numericInstance.toInt(x)
    override def toLong(x: TWrapper): Long = numericInstance.toLong(x)
    override def toFloat(x: TWrapper): Float = numericInstance.toFloat(x)
    override def toDouble(x: TWrapper): Double = numericInstance.toDouble(x)
    override def compare(x: TWrapper, y: TWrapper): Int = numericInstance.compare(x, y)
  }
}
