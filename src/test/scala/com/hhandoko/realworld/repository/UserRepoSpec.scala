package com.hhandoko.realworld.repository

import cats.effect.IO
import doobie.implicits._
import doobie.specs2.IOChecker
import org.specs2.mutable.Specification

import com.hhandoko.realworld.RepoSpecSupport
import com.hhandoko.realworld.core.{Profile, Username}

class UserRepoSpec extends Specification
  with RepoSpecSupport
  with IOChecker { override def is = sequential ^ s2"""

  User repository
    select query should
      return empty when there is no record   $selectEmptyResult
      return a record if exists              $selectSingleResult
    find query should
      return empty when no matching username is found   $foundNoUsername
      return User info if found                         $foundUsername
  """

  val instance = "user"

  private[this] val retSingleNoClause: IO[Option[Profile]] =
    UserRepo.select.query[Profile].option.transact(transactor)

  private[this] val retSingleByUsername: IO[Option[Profile]] =
    UserRepo(transactor).find(Username("test2"))

  private[this] def selectEmptyResult =
    retSingleNoClause.unsafeRunSync() must beNone

  private[this] def selectSingleResult = {
    execute(sql"""INSERT INTO profile (username, email) VALUES ('test', 'test@test.com')""")

    retSingleNoClause.unsafeRunSync() must not beNone
  }

  private[this] def foundNoUsername =
    retSingleByUsername.unsafeRunSync() must beNone

  private[this] def foundUsername = {
    execute(sql"""INSERT INTO profile (username, email) VALUES ('test2', 'test2@test.com')""")

    val Some(result) = retSingleByUsername.unsafeRunSync()
    result.username must_== Username("test2")
    //result.email must_== "test@test.com"
  }
}
