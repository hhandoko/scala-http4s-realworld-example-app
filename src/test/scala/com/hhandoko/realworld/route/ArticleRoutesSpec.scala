package com.hhandoko.realworld.route

import java.time.ZonedDateTime
import scala.concurrent.ExecutionContext

import cats.effect.{ContextShift, IO}
import org.http4s._
import org.http4s.implicits._
import org.specs2.Specification
import org.specs2.matcher.MatchResult

import com.hhandoko.realworld.core.{Article, Author, Username}
import com.hhandoko.realworld.route
import com.hhandoko.realworld.service.ArticleService
import com.hhandoko.realworld.service.ArticleService.ArticleCount
import com.hhandoko.realworld.service.query.Pagination

class ArticleRoutesSpec extends Specification { def is = s2"""

  Article routes
    when retrieving all articles
      without query params
        when articles exist
          should return 200 OK status          $hasArticlesReturn200
          should return a list of articles     $hasArticlesReturnList
        when no articles exist
          should return 200 OK status          $noArticlesReturn200
          should return empty array            $noArticlesReturnsEmpty
      with pagination query params
        when articles exist
          and results are within the page
            should return 200 OK status        $hasArticlesInPageReturn200
            should return a list of articles   $hasArticlesInPageReturnList
          and results are paged
            should return 200 OK status        $hasArticlesMultiPageReturn200
            should return a list of articles   $hasArticlesMultiPageReturnList
          and results are outside the page
            should return 200 OK status        $hasArticlesOutsidePageReturn200
            should return empty array          $hasArticlesOutsidePageReturnsEmpty
        when no articles exist
          with default offset and limit
            should return 200 OK status        $noArticlesWithPaginationReturns200
            should return empty array          $noArticlesWithPaginationReturnsEmpty
  """
  private[this] final val EMPTY_RESPONSE = """{"articles":[],"articlesCount":0}"""

  private[this] val retHasArticles: Response[IO] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val articles    = Vector(mockArticle("Hello"), mockArticle("World"))
    val getArticles = Request[IO](Method.GET, uri"/articles")

    ArticleRoutes[IO](new FakeArticleService(articles, articles.size))
      .orNotFound(getArticles)
      .unsafeRunSync()
  }

  private[this] val retNoArticles: Response[IO] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val getArticles = Request[IO](Method.GET, uri"/articles")

    route.ArticleRoutes[IO](new FakeArticleService(Vector.empty, 0))
      .orNotFound(getArticles)
      .unsafeRunSync()
  }

  private[this] val retHasArticlesInPage: Pagination => Response[IO] = { pg =>
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val articles    = Vector(mockArticle("Hello"), mockArticle("World"))
    val getArticles = Request[IO](Method.GET, uri"/articles".+?("offset", pg.offset).+?("limit", pg.limit))

    ArticleRoutes[IO](new FakeArticleService(articles, articles.size))
      .orNotFound(getArticles)
      .unsafeRunSync()
  }

  private[this] val retHasArticlesMultiPage: Pagination => Response[IO] = { pg =>
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val articles    = Vector(mockArticle("World"))
    val getArticles = Request[IO](Method.GET, uri"/articles".+?("offset", pg.offset).+?("limit", pg.limit))

    ArticleRoutes[IO](new FakeArticleService(articles, 2))
      .orNotFound(getArticles)
      .unsafeRunSync()
  }

  private[this] val retHasArticlesOutsidePage: Pagination => Response[IO] = { pg =>
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val getArticles = Request[IO](Method.GET, uri"/articles".+?("offset", pg.offset).+?("limit", pg.limit))

    ArticleRoutes[IO](new FakeArticleService(Vector.empty, 2))
      .orNotFound(getArticles)
      .unsafeRunSync()
  }

  private[this] val retNoArticlesWithPagination: Pagination => Response[IO] = { pg =>
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val getArticles = Request[IO](Method.GET, uri"/articles".+?("offset", pg.offset).+?("limit", pg.limit))

    route.ArticleRoutes[IO](new FakeArticleService(Vector.empty, 0))
      .orNotFound(getArticles)
      .unsafeRunSync()
  }

  private[this] def hasArticlesReturn200: MatchResult[Status] =
    retHasArticles.status must beEqualTo(Status.Ok)

  private[this] def hasArticlesReturnList: MatchResult[String] =
    retHasArticles.as[String].unsafeRunSync() must beEqualTo("""{"articles":[{"slug":"some-title-hello","title":"Some Title: Hello","description":"Some description","body":"some text","tagList":[],"createdAt":"2019-05-21T21:17:25.092+08:00","updatedAt":"2019-05-21T21:17:25.092+08:00","favorited":false,"favoritesCount":0,"author":{"username":"john-doe","bio":null,"image":null,"following":false}},{"slug":"some-title-world","title":"Some Title: World","description":"Some description","body":"some text","tagList":[],"createdAt":"2019-05-21T21:17:25.092+08:00","updatedAt":"2019-05-21T21:17:25.092+08:00","favorited":false,"favoritesCount":0,"author":{"username":"john-doe","bio":null,"image":null,"following":false}}],"articlesCount":2}""")

  private[this] def noArticlesReturn200: MatchResult[Status] =
    retNoArticles.status must beEqualTo(Status.Ok)

  private[this] def noArticlesReturnsEmpty: MatchResult[String] =
    retNoArticles.as[String].unsafeRunSync() must beEqualTo(EMPTY_RESPONSE)

  private[this] def hasArticlesInPageReturn200: MatchResult[Status] =
    retHasArticlesInPage(Pagination(limit = 2)).status must beEqualTo(Status.Ok)

  private[this] def hasArticlesInPageReturnList: MatchResult[String] =
    retHasArticlesInPage(Pagination(limit = 2)).as[String].unsafeRunSync() must beEqualTo("""{"articles":[{"slug":"some-title-hello","title":"Some Title: Hello","description":"Some description","body":"some text","tagList":[],"createdAt":"2019-05-21T21:17:25.092+08:00","updatedAt":"2019-05-21T21:17:25.092+08:00","favorited":false,"favoritesCount":0,"author":{"username":"john-doe","bio":null,"image":null,"following":false}},{"slug":"some-title-world","title":"Some Title: World","description":"Some description","body":"some text","tagList":[],"createdAt":"2019-05-21T21:17:25.092+08:00","updatedAt":"2019-05-21T21:17:25.092+08:00","favorited":false,"favoritesCount":0,"author":{"username":"john-doe","bio":null,"image":null,"following":false}}],"articlesCount":2}""")

  private[this] def hasArticlesMultiPageReturn200: MatchResult[Status] =
    retHasArticlesMultiPage(Pagination(limit = 1, offset = 1)).status must beEqualTo(Status.Ok)

  private[this] def hasArticlesMultiPageReturnList: MatchResult[String] =
    retHasArticlesMultiPage(Pagination(limit = 1, offset = 1)).as[String].unsafeRunSync() must beEqualTo("""{"articles":[{"slug":"some-title-world","title":"Some Title: World","description":"Some description","body":"some text","tagList":[],"createdAt":"2019-05-21T21:17:25.092+08:00","updatedAt":"2019-05-21T21:17:25.092+08:00","favorited":false,"favoritesCount":0,"author":{"username":"john-doe","bio":null,"image":null,"following":false}}],"articlesCount":2}""")

  private[this] def hasArticlesOutsidePageReturn200: MatchResult[Status] =
    retHasArticlesOutsidePage(Pagination(limit = 10, offset = 1)).status must beEqualTo(Status.Ok)

  private[this] def hasArticlesOutsidePageReturnsEmpty: MatchResult[String] =
    retHasArticlesOutsidePage(Pagination(limit = 10, offset = 1)).as[String].unsafeRunSync() must beEqualTo("""{"articles":[],"articlesCount":2}""")

  private[this] def noArticlesWithPaginationReturns200: MatchResult[Status] =
    retNoArticlesWithPagination(Pagination()).status must beEqualTo(Status.Ok)

  private[this] def noArticlesWithPaginationReturnsEmpty: MatchResult[String] =
    retNoArticlesWithPagination(Pagination()).as[String].unsafeRunSync() must beEqualTo(EMPTY_RESPONSE)

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

  class FakeArticleService(records: Vector[Article], count: ArticleCount) extends ArticleService[IO] {
    override def getAll(pg: Pagination): IO[(Vector[Article], ArticleCount)] = IO.pure((records, count))
  }
}
