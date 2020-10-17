package com.hhandoko.realworld.service

import cats.Applicative

import com.hhandoko.realworld.core.Tag

trait TagService[F[_]] {
  def getAll: F[Vector[Tag]]
}

object TagService {

  def apply[F[_]: Applicative]: TagService[F] =
    new TagService[F] {
      import cats.implicits._

      def getAll: F[Vector[Tag]] =
        Vector("hello", "world")
          .map(Tag)
          .pure[F]
    }
}
