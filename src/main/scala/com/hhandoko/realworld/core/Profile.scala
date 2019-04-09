package com.hhandoko.realworld.core

final case class Profile(
  username: Username,
  bio: Option[String],
  image: Option[String]
)
