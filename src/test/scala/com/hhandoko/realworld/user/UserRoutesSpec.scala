package com.hhandoko.realworld.user

import scala.concurrent.ExecutionContext

import cats.effect.{ContextShift, IO}
import org.http4s._
import org.http4s.implicits._
import org.specs2.Specification
import org.specs2.matcher.MatchResult

import com.hhandoko.realworld.auth.RequestAuthenticator
import com.hhandoko.realworld.core.{JwtToken, User, Username}

class UserRoutesSpec extends Specification { def is = s2"""

  User routes
    should return 200 OK status   $uriReturns200
    should return user info       $uriReturnsUserInfo
  """
  private[this] val nonExpiringToken = JwtToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJyZWFsd29ybGQiLCJ1c2VybmFtZSI6ImZhbW91cyJ9.c3ghryIJayjtL3wL4j2KSEeLBXUd5U8ALbdSQBau2Qg")

  private[this] val retCurrentUser: Response[IO] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val getCurrentUser = Request[IO](
      Method.GET,
      Uri.uri("/api/user"),
      headers = Headers.of(Header("Authorization", s"Token ${nonExpiringToken.value}"))
    )

    UserRoutes[IO](new RequestAuthenticator[IO], FakeUserService)
      .orNotFound(getCurrentUser)
      .unsafeRunSync()
  }

  private[this] def uriReturns200: MatchResult[Status] =
    retCurrentUser.status must beEqualTo(Status.Ok)

  private[this] def uriReturnsUserInfo: MatchResult[String] =
    retCurrentUser.as[String].unsafeRunSync() must beEqualTo(s"""{"user":{"email":"me@test.com","token":"${nonExpiringToken.value}","username":"me","bio":null,"image":null}}""")

  object FakeUserService extends UserService[IO] {
    def get(username: Username): IO[Option[User]] = IO.pure {
      Some(User(s"${username.value}@test.com", nonExpiringToken, username, None, None))
    }
  }

}
