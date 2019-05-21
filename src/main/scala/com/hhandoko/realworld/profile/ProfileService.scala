package com.hhandoko.realworld.profile

import cats.Applicative

import com.hhandoko.realworld.core.{Profile, Username}

trait ProfileService[F[_]] {
  def get(username: Username): F[Option[Profile]]
}

object ProfileService {

  implicit def apply[F[_]](implicit ev: ProfileService[F]): ProfileService[F] = ev

  def impl[F[_]: Applicative]: ProfileService[F] =
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
