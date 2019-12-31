package com.hhandoko.realworld.user

import cats.effect.IO
import doobie.implicits._
import doobie.specs2.IOChecker
import org.specs2.mutable.Specification

import com.hhandoko.realworld.RepoSpecSupport
import com.hhandoko.realworld.core.Profile

class UserRepoSpec extends Specification
  with RepoSpecSupport
  with IOChecker { override def is = s2"""

  User repository
    select query should
      be empty when there is no record $emptyResult
      return a record if exists        $singleResult
  """

  val instance = "user"

  private[this] val retSingleNoClause: IO[Option[Profile]] =
    UserRepo.select.query[Profile].option.transact(transactor)

  private[this] def emptyResult =
    retSingleNoClause.unsafeRunSync() must beNone

  private[this] def singleResult = {
    execute(sql"""INSERT INTO profile (username, email) VALUES ('test', 'test@test.com')""")

    retSingleNoClause.unsafeRunSync() must not beNone
  }

}
