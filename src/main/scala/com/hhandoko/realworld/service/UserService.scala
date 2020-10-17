package com.hhandoko.realworld.service

import cats.Applicative

import com.hhandoko.realworld.auth.JwtSupport
import com.hhandoko.realworld.core.{User, Username}

trait UserService[F[_]] {
  def get(username: Username): F[Option[User]]
}

object UserService extends JwtSupport {

  def apply[F[_]: Applicative]: UserService[F] =
    new UserService[F] {
      import cats.implicits._

      def get(username: Username): F[Option[User]] = {
        Option(
          User(
            email = s"${username.value}@test.com",
            token = encodeToken(username),
            username = username,
            bio = None,
            image = None
          )
        ).pure[F]
      }
    }
}
