package com.hhandoko.realworld.service

import cats.Applicative

import com.hhandoko.realworld.core.{Profile, Username}

trait ProfileService[F[_]] {
  def get(username: Username): F[Option[Profile]]
}

object ProfileService {

  def apply[F[_]: Applicative]: ProfileService[F] =
    new ProfileService[F] {
      import cats.implicits._

      def get(username: Username): F[Option[Profile]] = {
        val result =
          if (username.value.startsWith("celeb_")) Some(Profile(username, None, None))
          else None

        result.pure[F]
      }
    }
}
