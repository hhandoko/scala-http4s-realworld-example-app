package com.hhandoko.realworld.route

import scala.concurrent.ExecutionContext

import cats.effect.{ContextShift, IO}
import org.http4s._
import org.http4s.implicits._
import org.specs2.Specification
import org.specs2.matcher.MatchResult

import com.hhandoko.realworld.auth.RequestAuthenticator
import com.hhandoko.realworld.core.{JwtToken, User, Username}
import com.hhandoko.realworld.route
import com.hhandoko.realworld.service.UserService

class UserRoutesSpec extends Specification { def is = s2"""

  User routes
    when logged in
      should return 200 OK status             $currentUserReturns200
      should return user info                 $currentUserReturnsUserInfo
    when not logged in
      should return 401 Unauthorized status   $unauthorisedReturns401
  """
  private[this] val nonExpiringToken = JwtToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJyZWFsd29ybGQiLCJ1c2VybmFtZSI6ImZhbW91cyJ9.c3ghryIJayjtL3wL4j2KSEeLBXUd5U8ALbdSQBau2Qg")
  private[this] val invalidToken     = "invalid.jwt.token"

  private[this] val retCurrentUser: Response[IO] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val getCurrentUser = Request[IO](
      Method.GET,
      uri"/user",
      headers = Headers.of(Header("Authorization", s"Token ${nonExpiringToken.value}"))
    )

    UserRoutes[IO](new RequestAuthenticator[IO], FakeUserService)
      .orNotFound(getCurrentUser)
      .unsafeRunSync()
  }

  private[this] val retUnauthorized: Response[IO] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val getCurrentUser = Request[IO](
      Method.GET,
      uri"/user",
      headers = Headers.of(Header("Authorization", s"Token ${invalidToken}"))
    )

    route.UserRoutes[IO](new RequestAuthenticator[IO], FakeUserService)
      .orNotFound(getCurrentUser)
      .unsafeRunSync()
  }

  private[this] def currentUserReturns200: MatchResult[Status] =
    retCurrentUser.status must beEqualTo(Status.Ok)

  private[this] def currentUserReturnsUserInfo: MatchResult[String] =
    retCurrentUser.as[String].unsafeRunSync() must beEqualTo(s"""{"user":{"email":"famous@test.com","token":"${nonExpiringToken.value}","username":"famous","bio":null,"image":null}}""")

  private[this] def unauthorisedReturns401: MatchResult[Status] =
    retUnauthorized.status must beEqualTo(Status.Unauthorized)

  object FakeUserService extends UserService[IO] {
    def get(username: Username): IO[Option[User]] = IO.pure {
      Some(User(username, None, None, s"${username.value}@test.com", nonExpiringToken))
    }
  }
}
