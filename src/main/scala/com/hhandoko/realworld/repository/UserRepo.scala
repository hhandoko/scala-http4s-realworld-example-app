package com.hhandoko.realworld.repository

import cats.effect.Sync
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor

import com.hhandoko.realworld.core.{Profile, Username}

trait UserRepo[F[_]] {
  def find(username: Username): F[Option[Profile]]
}

object UserRepo {

  def apply[F[_]: Sync](xa: Transactor[F]): UserRepo[F] =
    new UserRepo[F] {
      override def find(username: Username): F[Option[Profile]] =
        (select ++ withUsername(username) ++ withLimit)
          .query[Profile]
          .option
          .transact(xa)
    }

  private[repository] final val select =
    Fragment.const {
      """SELECT username
        |     , bio
        |     , image
        |  FROM profile
        |""".stripMargin
    }

  private[repository] final val withLimit =
    Fragment.const("LIMIT 1")

  private[this] def withUsername(username: Username): Fragment =
    fr"WHERE lower(username) = lower(${username.value})"
}
