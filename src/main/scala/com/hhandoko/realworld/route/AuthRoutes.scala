package com.hhandoko.realworld.route

import cats.Applicative
import cats.effect.{ContextShift, Sync}
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityEncoder, HttpRoutes}

import com.hhandoko.realworld.auth.UnauthorizedResponseSupport
import com.hhandoko.realworld.route.common.UserResponse
import com.hhandoko.realworld.service.AuthService

object AuthRoutes extends UnauthorizedResponseSupport {

  def apply[F[_]: ContextShift: Sync](authService: AuthService[F]): HttpRoutes[F] = {
    object dsl extends Http4sDsl[F]; import dsl._

    implicit val loginPostDecoder = jsonOf[F, LoginPost]

    HttpRoutes.of[F] {
      // TODO: Implement login form data validation
      case req @ POST -> Root / "users" / "login" =>
        for {
          data   <- req.as[LoginPost]
          authed <- authService.verify(data.user.email, data.user.password)
          res    <- authed.fold(
            err => Unauthorized(withChallenge(err)),
            usr => Ok(UserResponse(usr.email, usr.token.value, usr.username.value, usr.bio, usr.image))
          )
        } yield res
    }
  }

  final case class LoginPost(user: LoginPostPayload)
  object LoginPost {
    implicit def entityEncoder[F[_]: Applicative]: EntityEncoder[F, LoginPost] =
      jsonEncoderOf[F, LoginPost]
  }

  final case class LoginPostPayload(email: String, password: String)
}
