package com.hhandoko.realworld.core

final case class Profile(
  username: Username,
  bio: Option[String],
  image: Option[String]
)

final case class User(
  email: String,
  token: String, // TODO: To update with JWT token
  username: Username,
  bio: Option[String],
  image: Option[String]
)
