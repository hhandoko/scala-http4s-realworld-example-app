package com.hhandoko.realworld.service.query

import com.hhandoko.realworld.service.query.Pagination.{DEFAULT_LIMIT, DEFAULT_OFFSET}

/** See: https://github.com/gothinkster/realworld/tree/master/api#list-articles */
final case class Pagination(limit: Int = DEFAULT_LIMIT, offset: Int = DEFAULT_OFFSET)

object Pagination {

  final val DEFAULT_LIMIT = 20
  final val DEFAULT_OFFSET = 0

  def apply(limit: Option[Int], offset: Option[Int]): Pagination =
    new Pagination(limit.getOrElse(DEFAULT_LIMIT), offset.getOrElse(DEFAULT_OFFSET))
}
