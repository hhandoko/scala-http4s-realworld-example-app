package com.hhandoko.realworld.route

import cats.Applicative
import cats.effect.{ContextShift, Sync}
import cats.implicits._
import io.circe.{Encoder, Json}
import org.http4s.circe.jsonEncoderOf
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityEncoder, HttpRoutes}

import com.hhandoko.realworld.core.Username
import com.hhandoko.realworld.service.ProfileService

object ProfileRoutes {

  def apply[F[_]: ContextShift: Sync](profileService: ProfileService[F]): HttpRoutes[F] = {
    object dsl extends Http4sDsl[F]; import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "api" / "profiles" / username =>
        for {
          prfOpt <- profileService.get(Username(username))
          res    <- prfOpt.fold(NotFound()) { prf =>
            Ok(ProfileResponse(prf.username.value, prf.bio, prf.image, following = false))
          }
        } yield res
    }
  }

  final case class ProfileResponse(username: String, bio: Option[String], image: Option[String], following: Boolean)

  object ProfileResponse {
    implicit val encoder: Encoder[ProfileResponse] = (r: ProfileResponse) => Json.obj(
      "profile" -> Json.obj(
        "username"  -> Json.fromString(r.username),
        "bio"       -> r.bio.fold(Json.Null)(Json.fromString),
        "image"     -> r.image.fold(Json.Null)(Json.fromString),
        "following" -> Json.fromBoolean(r.following)
      )
    )

    implicit def entityEncoder[F[_] : Applicative]: EntityEncoder[F, ProfileResponse] =
      jsonEncoderOf[F, ProfileResponse]
  }
}
