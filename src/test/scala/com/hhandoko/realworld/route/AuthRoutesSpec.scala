package com.hhandoko.realworld.route

import scala.concurrent.ExecutionContext

import cats.effect.{ContextShift, IO}
import cats.implicits._
import org.http4s.implicits._
import org.http4s.{Method, Request, Response, Status}
import org.specs2.Specification
import org.specs2.matcher.MatchResult

import com.hhandoko.realworld.auth.AuthService
import com.hhandoko.realworld.core.{JwtToken, User, Username}
import com.hhandoko.realworld.route
import com.hhandoko.realworld.route.AuthRoutes.{LoginPost, LoginPostPayload}

class AuthRoutesSpec extends Specification { def is = s2"""

  Auth routes
    on successful login
      should return 200 OK status             $successReturns200
      should return user info                 $successReturnsUserInfo
    on failed login
      should return 401 Unauthorized status   $failedReturns401
  """

  private[this] val retSuccessLogin: Response[IO] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val email     = "me@test.com"
    val payload   = LoginPost(LoginPostPayload(email, "ABC123"))
    val postLogin = Request[IO](Method.POST, uri"/api/users/login").withEntity(payload)

    AuthRoutes[IO](new FakeAuthService(email))
      .orNotFound(postLogin)
      .unsafeRunSync()
  }

  private[this] val retFailedLogin: Response[IO] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val email     = "me@test.com"
    val payload   = LoginPost(LoginPostPayload("invalid@test.com", "ABC123"))
    val postLogin = Request[IO](Method.POST, uri"/api/users/login").withEntity(payload)

    route.AuthRoutes[IO](new FakeAuthService(email))
      .orNotFound(postLogin)
      .unsafeRunSync()
  }

  private[this] def successReturns200: MatchResult[Status] =
    retSuccessLogin.status must beEqualTo(Status.Ok)

  private[this] def successReturnsUserInfo: MatchResult[String] = {
    val expected = """{"user":{"email":"me@test.com","token":"this.jwt.token","username":"me","bio":null,"image":null}}"""
    retSuccessLogin.as[String].unsafeRunSync() must beEqualTo(expected)
  }

  private[this] def failedReturns401: MatchResult[Status] =
    retFailedLogin.status must beEqualTo(Status.Unauthorized)

  class FakeAuthService(savedEmail: String) extends AuthService[IO] {
    override def verify(email: Email, password: Password): IO[Either[String, User]] = {
      val successResponse = User(Username("me"), None, None, savedEmail, JwtToken("this.jwt.token"))
      val failedResponse = "Invalid username and password combination"

      if (email == savedEmail) IO.pure(Either.right(successResponse))
      else IO.pure(Either.left(failedResponse))
    }
  }
}
