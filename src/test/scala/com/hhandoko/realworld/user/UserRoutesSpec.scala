package com.hhandoko.realworld.user

import scala.concurrent.ExecutionContext

import cats.effect.{ContextShift, IO}
import org.http4s._
import org.http4s.implicits._
import org.specs2.Specification
import org.specs2.matcher.MatchResult

import com.hhandoko.realworld.core.{JwtToken, User, Username}

class UserRoutesSpec extends Specification { def is = s2"""

  User routes
    should return 200 OK status   $uriReturns200
    should return user info       $uriReturnsUserInfo
  """

  private[this] val retCurrentUser: Response[IO] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val getCurrentUser = Request[IO](Method.GET, Uri.uri("/api/user"))

    UserRoutes[IO](FakeUserService)
      .orNotFound(getCurrentUser)
      .unsafeRunSync()
  }

  private[this] def uriReturns200: MatchResult[Status] =
    retCurrentUser.status must beEqualTo(Status.Ok)

  private[this] def uriReturnsUserInfo: MatchResult[String] =
    retCurrentUser.as[String].unsafeRunSync() must beEqualTo("""{"user":{"email":"me@test.com","token":"this.jwt.token","username":"me","bio":null,"image":null}}""")

  object FakeUserService extends UserService[IO] {
    def get(username: Username): IO[Option[User]] = IO.pure {
      Some(User(s"${username.value}@test.com", JwtToken("this.jwt.token"), username, None, None))
    }
  }

}
