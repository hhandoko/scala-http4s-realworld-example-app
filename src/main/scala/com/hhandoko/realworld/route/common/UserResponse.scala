package com.hhandoko.realworld.route.common

import cats.Applicative
import io.circe.{Encoder, Json}
import org.http4s.circe.jsonEncoderOf
import org.http4s.EntityEncoder

final case class UserResponse(
  email: String,
  token: String,
  username: String,
  bio: Option[String],
  image: Option[String]
)

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

  implicit def entityEncoder[F[_] : Applicative]: EntityEncoder[F, UserResponse] =
    jsonEncoderOf[F, UserResponse]
}
