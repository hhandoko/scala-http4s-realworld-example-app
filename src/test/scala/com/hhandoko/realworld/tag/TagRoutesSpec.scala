package com.hhandoko.realworld.tag

import scala.concurrent.ExecutionContext

import cats.effect.{ContextShift, IO}
import org.http4s._
import org.http4s.implicits._
import org.specs2.Specification
import org.specs2.matcher.MatchResult

import com.hhandoko.realworld.core.Tag

class TagRoutesSpec extends Specification { def is = s2"""

  Tag routes
    should return 200 OK status        $uriReturns200
    should return an array of 'tags'   $uriReturnsTagArray
  """

  private[this] val retAllTags: Response[IO] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val getAllTags = Request[IO](Method.GET, Uri.uri("/api/tags"))

    TagRoutes[IO](FakeTagService)
      .orNotFound(getAllTags)
      .unsafeRunSync()
  }

  private[this] def uriReturns200: MatchResult[Status] =
    retAllTags.status must beEqualTo(Status.Ok)

  private[this] def uriReturnsTagArray: MatchResult[String] =
    retAllTags.as[String].unsafeRunSync() must beEqualTo("""{"tags":["hello","world"]}""")

  object FakeTagService extends TagService[IO] {
    private val tags = Vector(Tag("hello"), Tag("world"))

    def getAll: IO[Vector[Tag]] = IO.pure(tags)
  }

}
