package com.hhandoko.realworld.service.query

/** See: https://github.com/gothinkster/realworld/tree/master/api#list-articles */
final case class Pagination(limit: Int = 20, offset: Int = 0)

object Pagination {

  final val DEFAULT_LIMIT = 20
  final val DEFAULT_OFFSET = 0

  def apply(limit: Option[Int], offset: Option[Int]): Pagination =
    new Pagination(limit.getOrElse(DEFAULT_LIMIT), offset.getOrElse(DEFAULT_OFFSET))
}
