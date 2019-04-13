package com.hhandoko.realworld.auth

import java.time.Instant
import java.util.Date

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

import com.hhandoko.realworld.core.{JwtToken, Username}

trait JwtSupport {

  final val CLAIM_USERNAME = "username"

  // TODO: Move to configuration
  final val ISSUER = "realworld"
  final val SECRET = "S3cret!"

  final val ALGO = Algorithm.HMAC256(SECRET)
  final lazy val verifier = JWT.require(ALGO).withIssuer(ISSUER).build()

  def encodeToken(username: Username): JwtToken =
    // Throws IllegalArgumentException and JWTCreationException
    JwtToken {
      // TODO: Make expiration configurable, and use Scala time classes
      JWT.create()
        .withIssuer(ISSUER)
        .withExpiresAt(Date.from(Instant.now().plusSeconds(3600)))
        // Private claims
        .withClaim(CLAIM_USERNAME, username.value)
        .sign(ALGO)
    }

  def decodeToken(token: JwtToken): Username = {
    // Throws JWTVerificationException
    val decoded = verifier.verify(token.value)
    Username(decoded.getClaim(CLAIM_USERNAME).asString())
  }

}
