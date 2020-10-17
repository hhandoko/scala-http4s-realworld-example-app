package com.hhandoko.realworld.route

import scala.concurrent.ExecutionContext

import cats.effect.{ContextShift, IO}
import org.http4s._
import org.http4s.implicits._
import org.specs2.Specification
import org.specs2.matcher.MatchResult

import com.hhandoko.realworld.core.Tag
import com.hhandoko.realworld.tag.TagService

class TagRoutesSpec extends Specification { def is = s2"""

  Tag routes
    should return 200 OK status        $uriReturns200
    should return an array of 'tags'   $uriReturnsTagArray
  """

  private[this] val retAllTags: Response[IO] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val tags       = Vector(Tag("hello"), Tag("world"))
    val getAllTags = Request[IO](Method.GET, uri"/api/tags")

    TagRoutes[IO](new FakeTagService(tags))
      .orNotFound(getAllTags)
      .unsafeRunSync()
  }

  private[this] def uriReturns200: MatchResult[Status] =
    retAllTags.status must beEqualTo(Status.Ok)

  private[this] def uriReturnsTagArray: MatchResult[String] =
    retAllTags.as[String].unsafeRunSync() must beEqualTo("""{"tags":["hello","world"]}""")

  class FakeTagService(tags: Vector[Tag]) extends TagService[IO] {
    override def getAll: IO[Vector[Tag]] = IO.pure(tags)
  }
}
