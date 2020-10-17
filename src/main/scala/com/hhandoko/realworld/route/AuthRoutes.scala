package com.hhandoko.realworld.route

import cats.Applicative
import cats.effect.{ContextShift, Sync}
import cats.implicits._
import io.circe.{Encoder, Json}
import io.circe.generic.auto._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityEncoder, HttpRoutes}

import com.hhandoko.realworld.auth.{AuthService, UnauthorizedResponseSupport}

object AuthRoutes extends UnauthorizedResponseSupport {

  def apply[F[_]: ContextShift: Sync](authService: AuthService[F]): HttpRoutes[F] = {
    object dsl extends Http4sDsl[F]; import dsl._

    implicit val loginPostDecoder = jsonOf[F, LoginPost]

    HttpRoutes.of[F] {
      // TODO: Implement login form data validation
      case req @ POST -> Root / "api" / "users" / "login" =>
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

  // TODO: Duplicated in UserRoutes, consolidate
  final case class UserResponse(email: String, token: String, username: String, bio: Option[String], image: Option[String])

  object UserResponse {
    implicit val encoder: Encoder[UserResponse] = (r: UserResponse) => Json.obj(
      "user" -> Json.obj(
        "email"    -> Json.fromString(r.email),
        "token"    -> Json.fromString(r.token),
        "username" -> Json.fromString(r.username),
        "bio"      -> r.bio.fold(Json.Null)(Json.fromString),
        "image"    -> r.image.fold(Json.Null)(Json.fromString)
      )
    )

    implicit def entityEncoder[F[_]: Applicative]: EntityEncoder[F, UserResponse] =
      jsonEncoderOf[F, UserResponse]
  }
}
