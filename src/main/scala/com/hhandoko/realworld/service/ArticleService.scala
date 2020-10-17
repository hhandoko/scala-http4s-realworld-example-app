package com.hhandoko.realworld.service

import java.time.ZonedDateTime

import cats.Applicative

import com.hhandoko.realworld.core.{Article, Author, Username}

trait ArticleService[F[_]] {
  def getAll: F[Vector[Article]]
}

object ArticleService {

  def apply[F[_]: Applicative]: ArticleService[F] =
    new ArticleService[F] {
      import cats.implicits._

      override def getAll: F[Vector[Article]] =
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
