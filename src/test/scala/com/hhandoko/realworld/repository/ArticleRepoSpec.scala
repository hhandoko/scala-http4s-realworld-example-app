package com.hhandoko.realworld.repository

import cats.effect.IO
import doobie.implicits._
import doobie.specs2.IOChecker
import org.specs2.mutable.Specification

import com.hhandoko.realworld.RepoSpecSupport
import com.hhandoko.realworld.core.Article

class ArticleRepoSpec extends Specification
  with RepoSpecSupport
  with IOChecker { override def is = sequential ^ s2"""

  Article repository
    select query should
      return empty when there is no record   $selectEmptyResult
  """

  import ArticleRepo.Reader._

  val instance = "article"

  private[this] val retSingleNoClause: IO[Vector[Article]] =
    ArticleRepo.select.query[Article].to[Vector].transact(transactor)

  private[this] def selectEmptyResult =
    retSingleNoClause.unsafeRunSync() must be empty
}
