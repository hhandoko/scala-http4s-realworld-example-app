package com.hhandoko.realworld.tag

import cats.Applicative
import cats.implicits._

import com.hhandoko.realworld.core.Tag

trait TagService[F[_]] {
  def getAll: F[Vector[Tag]]
}

object TagService {
  implicit def apply[F[_]](implicit ev: TagService[F]): TagService[F] = ev

  def impl[F[_]: Applicative]: TagService[F] =
    new TagService[F] {
      def getAll: F[Vector[Tag]] =
        Vector("hello", "world")
          .map(Tag)
          .pure[F]
    }
}
