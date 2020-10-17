package com.hhandoko.realworld.auth

import org.http4s.Challenge
import org.http4s.headers.`WWW-Authenticate`

trait UnauthorizedResponseSupport {

  private final val SCHEME = "Token"
  private final val REALM = "realworld"

  // TODO: Split to invalid_request and invalid_token
  // Refer to:
  //   - https://tools.ietf.org/html/rfc6750#section-3
  //   - http://self-issued.info/docs/draft-ietf-oauth-v2-bearer.html#rfc.section.3
  protected def withChallenge(err: String): `WWW-Authenticate` = {
    val params = Map(
      "error" -> "invalid_token",
      "error_description" -> err
    )
    `WWW-Authenticate`(Challenge(SCHEME, REALM, params))
  }
}
