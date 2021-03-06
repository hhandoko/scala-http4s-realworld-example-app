package com.hhandoko.realworld.route

import java.time.format.DateTimeFormatter

import cats.Applicative
import cats.effect.{ContextShift, Sync}
import cats.implicits._
import io.circe.{Encoder, Json}
import org.http4s.circe.jsonEncoderOf
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.OptionalQueryParamDecoderMatcher
import org.http4s.{EntityEncoder, HttpRoutes}

import com.hhandoko.realworld.core.Article
import com.hhandoko.realworld.service.ArticleService
import com.hhandoko.realworld.service.ArticleService.ArticleCount
import com.hhandoko.realworld.service.query.Pagination

object ArticleRoutes {

  def apply[F[_]: ContextShift: Sync](articleService: ArticleService[F]): HttpRoutes[F] = {
    object dsl extends Http4sDsl[F]; import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "articles"
        :? LimitQuery(limitOpt)
        +& OffsetQuery(offsetOpt) =>
        for {
          artsWithCount <- articleService.getAll(Pagination(limitOpt, offsetOpt))
          res           <- Ok(ArticlesResponse(artsWithCount._1, artsWithCount._2))
        } yield res
    }
  }

  object LimitQuery extends OptionalQueryParamDecoderMatcher[Int]("limit")
  object OffsetQuery extends OptionalQueryParamDecoderMatcher[Int]("offset")

  final case class ArticlesResponse(articles: Vector[Article], count: ArticleCount)
  object ArticlesResponse {
    implicit val encoder: Encoder[ArticlesResponse] = (r: ArticlesResponse) => Json.obj(
      "articles" -> Json.fromValues(
        r.articles.map { a =>
          Json.obj(
            "slug"           -> Json.fromString(a.slug),
            "title"          -> Json.fromString(a.title),
            "description"    -> Json.fromString(a.description),
            "body"           -> Json.fromString(a.body),
            "tagList"        -> Json.fromValues(a.tagList.map(Json.fromString)),
            "createdAt"      -> Json.fromString(a.createdAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)),
            "updatedAt"      -> Json.fromString(a.updatedAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)),
            "favorited"      -> Json.fromBoolean(a.favorited),
            "favoritesCount" -> Json.fromLong(a.favoritesCount),
            "author"         -> Json.obj(
              "username"  -> Json.fromString(a.author.username.value),
              "bio"       -> a.author.bio.fold(Json.Null)(Json.fromString),
              "image"     -> a.author.image.fold(Json.Null)(Json.fromString),
              "following" -> Json.fromBoolean(a.author.following)
            )
          )
        }
      ),
      "articlesCount" -> Json.fromInt(r.count)
    )

    implicit def entityEncoder[F[_]: Applicative]: EntityEncoder[F, ArticlesResponse] =
      jsonEncoderOf[F, ArticlesResponse]
  }
}
