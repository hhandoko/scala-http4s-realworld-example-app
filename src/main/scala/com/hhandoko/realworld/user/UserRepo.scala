package com.hhandoko.realworld.user

import cats._
import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor

import com.hhandoko.realworld.core.{Profile, Username}

trait UserRepo[F[_]] {
  def find(username: Username): F[Option[Profile]]
}

object UserRepo {

  private[user] final val select =
    fr"""SELECT username
        |     , bio
        |     , image
        |  FROM profile
        |""".stripMargin

  def apply[F[_]: Monad: Sync](xa: Transactor[F]): UserRepo[F] =
    new UserRepo[F] {
      def find(username: Username): F[Option[Profile]] =
        (select ++ fr"WHERE lower(username) = lower(${username.value}) LIMIT 1")
          .query[Profile]
          .option
          .transact(xa)
    }
}
