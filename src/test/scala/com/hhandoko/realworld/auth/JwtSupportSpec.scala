package com.hhandoko.realworld.auth

import scala.util.Random

import org.specs2.Specification
import org.specs2.matcher.MatchResult

import com.hhandoko.realworld.core.{JwtToken, Username}

class JwtSupportSpec extends Specification { def is = s2"""

  JWT support trait
    should generate token with valid username   $tokenHasValidUsername
  """

  object JwtTokenTester extends JwtSupport

  private val username = (1 to 10).map(_ => Random.alphanumeric).mkString

  private[this] val tokenToTest: JwtToken =
    Username(username) |> JwtTokenTester.encodeToken

  private[this] def tokenHasValidUsername: MatchResult[String] =
    (tokenToTest |> JwtTokenTester.decodeToken).value must beEqualTo(username)

  implicit class PipeSyntax[T](t: T) {
    def |>[U](fn: T => U): U = fn(t)
  }
}
