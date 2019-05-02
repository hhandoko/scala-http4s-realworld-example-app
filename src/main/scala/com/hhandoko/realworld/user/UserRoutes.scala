package com.hhandoko.realworld.user

import cats.Applicative
import cats.effect.{ContextShift, Sync}
import cats.implicits._
import io.circe.{Encoder, Json}
import org.http4s.circe.jsonEncoderOf
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedService, EntityEncoder, HttpRoutes}

import com.hhandoko.realworld.auth.RequestAuthenticator

object UserRoutes {

  def apply[F[_]: Sync: ContextShift](authenticated: RequestAuthenticator[F], userService: UserService[F]): HttpRoutes[F] = {
    object dsl extends Http4sDsl[F]; import dsl._

    authenticated {
      AuthedService {
        // TODO: Implement authentication check
        case GET -> Root / "api" / "user" as username =>
          for {
            usrOpt <- userService.get(username)
            // FIXME: Unauthorized fails to compile (requires a `WWW-Authenticate` header value), so we use 403 error code instead for now
            // See: https://github.com/twilio/guardrail/issues/179
            res    <- usrOpt.fold(NotFound()) { usr =>
              Ok(UserResponse(usr.email, usr.token.value, usr.username.value, usr.bio, usr.image))
            }
          } yield res
      }
    }
  }

  // TODO: Duplicated in UserRoutes, consolidate
  final case class UserResponse(email: String, token: String, username: String, bio: Option[String], image: Option[String])

  object UserResponse {
    implicit val encoder: Encoder[UserResponse] = (r: UserResponse) => Json.obj(
      "user" -> Json.obj(
        "email" -> Json.fromString(r.email),
        "token" -> Json.fromString(r.token),
        "username"  -> Json.fromString(r.username),
        "bio"       -> r.bio.fold(Json.Null)(Json.fromString),
        "image"     -> r.image.fold(Json.Null)(Json.fromString)
      )
    )

    implicit def entityEncoder[F[_]: Applicative]: EntityEncoder[F, UserResponse] =
      jsonEncoderOf[F, UserResponse]
  }

}
