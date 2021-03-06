package com.hhandoko.realworld.repository

import java.time.ZonedDateTime

import cats._
import cats.syntax.functor._
import doobie.Read

import com.hhandoko.realworld.core.{Author, Username}
import cats.effect.Effect
import doobie.Fragment
import doobie.implicits._
import doobie.util.transactor.Transactor

import com.hhandoko.realworld.core.Article
import com.hhandoko.realworld.service.query.Pagination

trait ArticleRepo[F[_]] {
  def findFiltered(pg: Pagination): F[Vector[Article]]
}

object ArticleRepo {

  def apply[F[_]: Effect: Monad](xa: Transactor[F]): ArticleRepo[F] =
    new ArticleRepo[F] {
      import Reader._

      override def findFiltered(pg: Pagination): F[Vector[Article]] =
        for {
          arts <- findArticles(pg)
        } yield arts

      def findArticles(pg: Pagination): F[Vector[Article]] =
        (select ++ withPagination(pg))
          .query[Article]
          .to[Vector]
          .transact(xa)
    }

  private[repository] final val select =
    Fragment.const {
      """    SELECT a.slug
        |         , a.title
        |         , a.description
        |         , a.body
        |         , a.created_at
        |         , a.updated_at
        |         , p.username
        |      FROM article a
        |INNER JOIN profile p ON a.author_id = p.id
        |""".stripMargin
    }

  private[this] def withPagination(pg: Pagination) =
    fr"     LIMIT ${pg.limit} OFFSET ${pg.offset}"

  object Reader {
    import doobie.implicits.javatime._

    implicit val readArticle: Read[Article] =
      Read[(String, String, String, String, ZonedDateTime, ZonedDateTime, String)]
        .map { case (slug, title, description, body, created_at, updated_at, username) =>
          Article(
            slug,
            title,
            description,
            body,
            Set.empty[String],
            created_at,
            updated_at,
            favorited = false,
            favoritesCount = 0,
            Author(
              Username(username),
              None,
              None,
              following = false,
            )
          )
        }
  }
}
