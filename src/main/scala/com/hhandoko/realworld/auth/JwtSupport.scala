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
  final val VALIDITY_DURATION = 3600

  final val ALGO = Algorithm.HMAC256(SECRET)
  final lazy val verifier = JWT.require(ALGO).withIssuer(ISSUER).build()

  def encodeToken(username: Username): JwtToken =
    JwtToken(generateToken(username))

  def decodeToken(token: JwtToken): Username = {
    // Throws JWTVerificationException
    val decoded = verifier.verify(token.value)
    Username(decoded.getClaim(CLAIM_USERNAME).asString())
  }

  private[this] def generateToken(username: Username): String =
    // Throws IllegalArgumentException and JWTCreationException
    JWT.create()
      .withIssuer(ISSUER)
      .withExpiresAt(Date.from(Instant.now().plusSeconds(VALIDITY_DURATION)))
      // Private claims
      .withClaim(CLAIM_USERNAME, username.value)
      .sign(ALGO)

}
