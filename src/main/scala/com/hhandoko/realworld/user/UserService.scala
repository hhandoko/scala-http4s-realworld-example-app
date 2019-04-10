package com.hhandoko.realworld.user

import cats.Applicative
import cats.implicits._

import com.hhandoko.realworld.core.{User, Username}

trait UserService[F[_]] {
  def get(username: Username): F[Option[User]]
}

object UserService {

  implicit def apply[F[_]](implicit ev: UserService[F]): UserService[F] = ev

  def impl[F[_]: Applicative]: UserService[F] =
    new UserService[F] {
      def get(username: Username): F[Option[User]] = {
        Option(
          User(
            email = s"${username.value}@test.com",
            token = "this.jwt.token",
            username = username,
            bio = None,
            image = None
          )
        ).pure[F]
      }
    }
}
