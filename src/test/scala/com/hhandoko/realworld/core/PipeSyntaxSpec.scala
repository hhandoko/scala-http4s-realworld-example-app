package com.hhandoko.realworld.core

import org.specs2.Specification
import org.specs2.matcher.MatchResult

import com.hhandoko.realworld.core.lang.pipe._

class PipeSyntaxSpec extends Specification { def is = s2"""

  Pipe syntax
    should pipe method with one arity          $pipeOneArity
    should pipe method with (n) arity with λ   $pipeNArity
  """

  private[this] def pipeOneArity: MatchResult[BigDecimal] =
    "20" pipe BigDecimal.apply must beEqualTo(BigDecimal("20"))

  private[this] def pipeNArity: MatchResult[Int] =
    5 |> (i => add(i, 2)) must beEqualTo(7)

  private[this] def add(first: Int, second: Int): Int = first + second

}
