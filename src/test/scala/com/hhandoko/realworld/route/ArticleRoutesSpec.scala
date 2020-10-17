package com.hhandoko.realworld.route

import java.time.ZonedDateTime
import scala.concurrent.ExecutionContext

import cats.effect.{ContextShift, IO}
import org.http4s._
import org.http4s.implicits._
import org.specs2.Specification
import org.specs2.matcher.MatchResult

import com.hhandoko.realworld.article.ArticleService
import com.hhandoko.realworld.core.{Article, Author, Username}
import com.hhandoko.realworld.route

class ArticleRoutesSpec extends Specification { def is = s2"""

  Article routes
    when retrieving all articles
      when articles exist
        should return 200 OK status        $hasArticlesReturn200
        should return a list of articles   $hasArticlesReturnList
      when no articles exist
        should return 200 OK status        $noArticlesReturn200
        should return empty array          $noArticleReturnsEmpty
  """

  private[this] val retHasArticles: Response[IO] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val articles = Vector(mockArticle("Hello"), mockArticle("World"))
    val getArticles = Request[IO](Method.GET, uri"/api/articles")

    ArticleRoutes[IO](new FakeArticleService(articles))
      .orNotFound(getArticles)
      .unsafeRunSync()
  }

  private[this] val retNoArticles: Response[IO] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val getArticles = Request[IO](Method.GET, uri"/api/articles")

    route.ArticleRoutes[IO](new FakeArticleService(Vector.empty))
      .orNotFound(getArticles)
      .unsafeRunSync()
  }

  private[this] def hasArticlesReturn200: MatchResult[Status] =
    retHasArticles.status must beEqualTo(Status.Ok)

  private[this] def hasArticlesReturnList: MatchResult[String] =
    retHasArticles.as[String].unsafeRunSync() must beEqualTo("""{"articles":[{"slug":"some-title-hello","title":"Some Title: Hello","description":"Some description","body":"some text","tagList":[],"createdAt":"2019-05-21T21:17:25.092+08:00","updatedAt":"2019-05-21T21:17:25.092+08:00","favorited":false,"favoritesCount":0,"author":{"username":"john-doe","bio":null,"image":null,"following":false}},{"slug":"some-title-world","title":"Some Title: World","description":"Some description","body":"some text","tagList":[],"createdAt":"2019-05-21T21:17:25.092+08:00","updatedAt":"2019-05-21T21:17:25.092+08:00","favorited":false,"favoritesCount":0,"author":{"username":"john-doe","bio":null,"image":null,"following":false}}]}""")

  private[this] def noArticlesReturn200: MatchResult[Status] =
    retNoArticles.status must beEqualTo(Status.Ok)

  private[this] def noArticleReturnsEmpty: MatchResult[String] =
    retNoArticles.as[String].unsafeRunSync() must beEqualTo("""{"articles":[]}""")

  private[this] def mockArticle(title: String): Article =
    Article(
      slug = s"some-title-${title.toLowerCase}",
      title = s"Some Title: $title",
      description = "Some description",
      body = "some text",
      tagList = Set.empty,
      createdAt = ZonedDateTime.parse("2019-05-21T21:17:25.092+08:00"),
      updatedAt = ZonedDateTime.parse("2019-05-21T21:17:25.092+08:00"),
      favorited = false,
      favoritesCount = 0L,
      author = Author(
        username = Username("john-doe"),
        bio = None,
        image = None,
        following = false
      )
    )

  class FakeArticleService(records: Vector[Article]) extends ArticleService[IO] {
    override def getAll: IO[Vector[Article]] = IO.pure(records)
  }
}
