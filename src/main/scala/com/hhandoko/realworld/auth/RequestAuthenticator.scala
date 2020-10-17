package com.hhandoko.realworld.auth

import cats.Monad
import cats.data.{Kleisli, OptionT}
import org.http4s.server.AuthMiddleware
import org.http4s.util.CaseInsensitiveString
import org.http4s.{AuthedRoutes, Header, HttpRoutes, Request}

import com.hhandoko.realworld.core.{JwtToken, Username}

class RequestAuthenticator[F[_]: Monad] extends JwtSupport {

  private final val HEADER_NAME = CaseInsensitiveString("Authorization")
  private final val HEADER_VALUE_PREFIX = "Token"
  private final val HEADER_VALUE_SEPARATOR = " "
  private final val HEADER_VALUE_START_INDEX =
    HEADER_VALUE_PREFIX.length + HEADER_VALUE_SEPARATOR.length

  private val authUser = Kleisli[OptionT[F, *], Request[F], Username] { req =>
    OptionT.fromOption {
      req.headers
        .find(authorizationHeader)
        .map(getTokenValue)
        .flatMap(decodeToken)
    }
  }

  private val middleware = AuthMiddleware(authUser)

  def apply(authedService: AuthedRoutes[Username, F]): HttpRoutes[F] = middleware(authedService)

  private[this] def authorizationHeader(header: Header): Boolean =
    header.name == HEADER_NAME &&
    header.value.startsWith(HEADER_VALUE_PREFIX + HEADER_VALUE_SEPARATOR)

  private[this] def getTokenValue(header: Header): JwtToken =
    JwtToken(header.value.substring(HEADER_VALUE_START_INDEX))
}
