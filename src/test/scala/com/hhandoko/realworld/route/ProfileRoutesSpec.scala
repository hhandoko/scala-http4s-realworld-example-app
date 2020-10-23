package com.hhandoko.realworld.route

import scala.concurrent.ExecutionContext

import cats.effect.{ContextShift, IO}
import org.http4s._
import org.http4s.implicits._
import org.specs2.Specification
import org.specs2.matcher.MatchResult

import com.hhandoko.realworld.core.{Profile, Username}
import com.hhandoko.realworld.route
import com.hhandoko.realworld.service.ProfileService

class ProfileRoutesSpec extends Specification { def is = s2"""

  Profile routes
    when record exists
      should return 200 OK status   $foundReturns200
      should return profile         $foundReturnsProfile
    when record does not exists
      should return 404 Not Found   $notFoundReturns404
  """

  private[this] val retFoundProfile: Response[IO] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val getProfile = Request[IO](Method.GET, uri"/profiles/celeb_1")

    ProfileRoutes[IO](FakeProfileService)
      .orNotFound(getProfile)
      .unsafeRunSync()
  }

  private[this] val retNotFoundProfile: Response[IO] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val getProfile = Request[IO](Method.GET, uri"/profile/abc")

    route.ProfileRoutes[IO](FakeProfileService)
      .orNotFound(getProfile)
      .unsafeRunSync()
  }

  private[this] def foundReturns200: MatchResult[Status] =
    retFoundProfile.status must beEqualTo(Status.Ok)

  private[this] def foundReturnsProfile: MatchResult[String] =
    retFoundProfile.as[String].unsafeRunSync() must beEqualTo("""{"profile":{"username":"celeb_1","bio":null,"image":null,"following":false}}""")

  private[this] def notFoundReturns404: MatchResult[Status] =
    retNotFoundProfile.status must beEqualTo(Status.NotFound)

  object FakeProfileService extends ProfileService[IO] {
    override def get(username: Username): IO[Option[Profile]] = IO.pure {
      if (username.value.startsWith("celeb_")) Some(Profile(username, None, None))
      else None
    }
  }
}
