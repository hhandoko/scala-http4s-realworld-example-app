package com.hhandoko.realworld.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

import com.hhandoko.realworld.core.{JwtToken, Username}

trait JwtSupport {

  final val CLAIM_USERNAME = "username"

  // TODO: Move to configuration
  final val SECRET = "S3cret!"
  final val ALGO = Algorithm.HMAC256(SECRET)

  def generateToken(username: Username): JwtToken =
    JwtToken {
      // TODO: Make expiration configurable, and use Scala time classes
      JWT.create()
        // Private claims
        .withClaim(CLAIM_USERNAME, username.value)
        .sign(ALGO)
    }

}
