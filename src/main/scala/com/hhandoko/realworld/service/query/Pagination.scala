package com.hhandoko.realworld.service.query

import com.hhandoko.realworld.service.query.Pagination.{DEFAULT_LIMIT, DEFAULT_OFFSET}

/** See: https://github.com/gothinkster/realworld/tree/master/api#list-articles */
final case class Pagination(limit: Int = DEFAULT_LIMIT, offset: Int = DEFAULT_OFFSET)

object Pagination {

  final val DEFAULT_LIMIT = 20
  final val DEFAULT_OFFSET = 0

  def apply(limit: Option[Int], offset: Option[Int]): Pagination = {

    def sanitise(valueOpt: Option[Int], default: Int): Int = valueOpt match {
      case Some(value) if value >= 0 => value
      case _                         => default
    }

    val sanitisedLimit  = sanitise(limit, DEFAULT_LIMIT)
    val sanitisedOffset = sanitise(offset, DEFAULT_OFFSET)

    new Pagination(sanitisedLimit, sanitisedOffset)
  }
}
