package com.hhandoko.realworld.route

import java.time.format.DateTimeFormatter

import cats.Applicative
import cats.effect.{ContextShift, Sync}
import cats.implicits._
import io.circe.{Encoder, Json}
import org.http4s.circe.jsonEncoderOf
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityEncoder, HttpRoutes}

import com.hhandoko.realworld.core.Article
import com.hhandoko.realworld.service.ArticleService

object ArticleRoutes {

  def apply[F[_]: ContextShift: Sync](articleService: ArticleService[F]): HttpRoutes[F] = {
    object dsl extends Http4sDsl[F]; import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "articles" =>
        for {
          arts <- articleService.getAll
          res  <- Ok(ArticlesResponse(arts))
        } yield res
    }
  }

  final case class ArticlesResponse(articles: Vector[Article])

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
      )
    )

    implicit def entityEncoder[F[_]: Applicative]: EntityEncoder[F, ArticlesResponse] =
      jsonEncoderOf[F, ArticlesResponse]
  }
}
