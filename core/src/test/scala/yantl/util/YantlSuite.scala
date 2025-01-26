package yantl.util

import munit.FunSuite
import munit.Location
import munit.diff.DiffOptions

trait YantlSuite extends FunSuite {
  def assertRight[L, R](obtained: Either[L, R], expected: R)(using
      loc: Location,
      diffOptions: DiffOptions
  ): Unit =
    assertEquals(obtained, Right(expected))

  def assertLeft[L, R](obtained: Either[L, R], expected: L)(using
      loc: Location,
      diffOptions: DiffOptions
  ): Unit =
    assertEquals(obtained, Left(expected))
}
