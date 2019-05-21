package com.hhandoko.realworld.auth

import scala.concurrent.ExecutionContext

import cats.effect.{ContextShift, IO}
import cats.implicits._
import org.http4s.{Method, Request, Response, Status, Uri}
import org.http4s.implicits._
import org.specs2.Specification
import org.specs2.matcher.MatchResult

import com.hhandoko.realworld.auth.AuthRoutes.{LoginPost, LoginPostPayload}
import com.hhandoko.realworld.core.{JwtToken, User, Username}

class AuthRoutesSpec extends Specification { def is = s2"""

  Auth routes
    on successful login
      should return 200 OK status             $uriReturns200
      should return user info                 $uriReturnsUserInfo
    on failed login
      should return 401 Unauthorized status   $uriReturns401
  """

  private[this] val loginSuccessResponse: Response[IO] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val payload = LoginPost(LoginPostPayload("me@test.com", "ABC123"))
    val postLogin = Request[IO](Method.POST, Uri.uri("/api/users/login")).withEntity(payload)

    AuthRoutes[IO](FakeAuthService)
      .orNotFound(postLogin)
      .unsafeRunSync()
  }

  private[this] val loginFailedResponse: Response[IO] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val payload = LoginPost(LoginPostPayload("invalid@test.com", "ABC123"))
    val postLogin = Request[IO](Method.POST, Uri.uri("/api/users/login")).withEntity(payload)

    AuthRoutes[IO](FakeAuthService)
      .orNotFound(postLogin)
      .unsafeRunSync()
  }

  private[this] def uriReturns200: MatchResult[Status] =
    loginSuccessResponse.status must beEqualTo(Status.Ok)

  private[this] def uriReturnsUserInfo: MatchResult[String] = {
    val expected = """{"user":{"email":"me@test.com","token":"this.jwt.token","username":"me","bio":null,"image":null}}"""
    loginSuccessResponse.as[String].unsafeRunSync() must beEqualTo(expected)
  }

  private[this] def uriReturns401: MatchResult[Status] =
    loginFailedResponse.status must beEqualTo(Status.Unauthorized)

  object FakeAuthService extends AuthService[IO] {
    def verify(email: Email, password: Password): IO[Either[String, User]] = {
      val successResponse = User(Username("me"), None, None, s"me@test.com", JwtToken("this.jwt.token"))
      val failedResponse = "Invalid username and password combination"

      if (email == "me@test.com") IO.pure(Either.right(successResponse))
      else IO.pure(Either.left(failedResponse))
    }
  }

}
