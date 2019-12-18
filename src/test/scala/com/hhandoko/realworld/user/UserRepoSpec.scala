package com.hhandoko.realworld.user

import cats.effect.IO
import doobie.implicits._
import doobie.specs2.IOChecker
import org.specs2.mutable.Specification

import com.hhandoko.realworld.RepoSpecSupport
import com.hhandoko.realworld.core.User

class UserRepoSpec extends Specification
  with RepoSpecSupport
  with IOChecker { override def is = s2"""

  User repository
    should be empty   $emptyResult
  """

  val instance = "user"

  private[this] val retNilOnEmpty: IO[Option[User]] =
    UserRepo.select.query[User].option.transact(transactor)

  private[this] def emptyResult =
    retNilOnEmpty.unsafeRunSync() must beEqualTo(None)
}
