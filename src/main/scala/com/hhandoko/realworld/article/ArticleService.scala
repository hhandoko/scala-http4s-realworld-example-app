package com.hhandoko.realworld.article

import java.time.ZonedDateTime

import cats.Applicative

import com.hhandoko.realworld.core.{Article, Author, Username}

trait ArticleService[F[_]] {
  def getAll: F[Vector[Article]]
}

object ArticleService {

  implicit def apply[F[_]](implicit ev: ArticleService[F]): ArticleService[F] = ev

  def impl[F[_]: Applicative]: ArticleService[F] =
    new ArticleService[F] {
      import cats.implicits._

      def getAll: F[Vector[Article]] =
        Vector("world", "you")
          .map(mockArticles)
          .pure[F]
    }

  private[this] def mockArticles(title: String): Article =
    Article(
      slug = s"hello-${title.toLowerCase}",
      title = title,
      description = title,
      body = title,
      tagList = Set.empty,
      createdAt = ZonedDateTime.now(),
      updatedAt = ZonedDateTime.now(),
      favorited = false,
      favoritesCount = 0,
      author = Author(
        username = Username("test"),
        bio = None,
        image = None,
        following = false
      )
    )
}