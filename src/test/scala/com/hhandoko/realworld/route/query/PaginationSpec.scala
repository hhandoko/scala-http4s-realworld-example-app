package com.hhandoko.realworld.route.query

import org.specs2.Specification
import org.specs2.matcher.MatchResult

import com.hhandoko.realworld.service.query.Pagination

class PaginationSpec extends Specification { def is = s2"""

  Pagination query params object
    when limit and offset is not defined
      should use default limit     $paginationUseDefaultLimit
      should use default offset    $paginationUseDefaultOffset
    when either limit or offset is defined
      should use provided limit    $paginationUseLimit
      should use provided offset   $paginationUseOffset
    when both limit and offset is defined
      should use provided limit    $paginationUseLimitFromLimitAndOffset
      should use provided offset   $paginationUseOffsetFromLimitAndOffset
    when either invalid limit or offset is defined
      should use default limit     $paginationInvalidLimitUseDefault
      should use default offset    $paginationInvalidOffsetUseDefault
    when both invalid limit or offset is defined
      should use default limit     $paginationInvalidLimitAndOffsetUseDefaultLimit
      should use default offset    $paginationInvalidLimitAndOffsetUseDefaultOffset
  """

  val limit  = 100
  val offset = 100

  private[this] val paginationUseDefault: Pagination =
    Pagination(None, None)

  private[this] val paginationUseProvided: Pagination =
    Pagination(limit = Some(limit), offset = Some(offset))

  private[this] val paginationUseProvidedInvalid: Pagination =
    Pagination(limit = Some(-100), offset = Some(-100))

  private[this] def paginationUseDefaultLimit: MatchResult[Int] =
    paginationUseDefault.limit must beEqualTo(Pagination.DEFAULT_LIMIT)

  private[this] def paginationUseDefaultOffset: MatchResult[Int] =
    paginationUseDefault.offset must beEqualTo(Pagination.DEFAULT_OFFSET)

  private[this] def paginationUseLimit: MatchResult[Int] =
    Pagination(limit = Some(limit), offset = None).limit must beEqualTo(limit)

  private[this] def paginationUseOffset: MatchResult[Int] =
    Pagination(limit = None, offset = Some(offset)).offset must beEqualTo(offset)

  private[this] def paginationUseLimitFromLimitAndOffset: MatchResult[Int] =
    paginationUseProvided.limit must beEqualTo(limit)

  private[this] def paginationUseOffsetFromLimitAndOffset: MatchResult[Int] =
    paginationUseProvided.offset must beEqualTo(offset)

  private[this] def paginationInvalidLimitUseDefault: MatchResult[Int] =
    Pagination(limit = Some(-100), offset = None).limit must beEqualTo(Pagination.DEFAULT_LIMIT)

  private[this] def paginationInvalidOffsetUseDefault: MatchResult[Int] =
    Pagination(limit = None, offset = Some(-100)).offset must beEqualTo(Pagination.DEFAULT_OFFSET)

  private[this] def paginationInvalidLimitAndOffsetUseDefaultLimit: MatchResult[Int] =
    paginationUseProvidedInvalid.limit must beEqualTo(Pagination.DEFAULT_LIMIT)

  private[this] def paginationInvalidLimitAndOffsetUseDefaultOffset: MatchResult[Int] =
    paginationUseProvidedInvalid.offset must beEqualTo(Pagination.DEFAULT_OFFSET)
}
