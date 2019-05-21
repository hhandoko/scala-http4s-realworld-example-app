package com.hhandoko.realworld.core

sealed trait ProfileAttributes {
  def username: Username
  def bio: Option[String]
  def image: Option[String]
}

final case class Profile(
  username: Username,
  bio: Option[String],
  image: Option[String]
) extends ProfileAttributes

final case class User(
  username: Username,
  bio: Option[String],
  image: Option[String],
  email: String,
  token: JwtToken,
) extends ProfileAttributes

final case class Author(
  username: Username,
  bio: Option[String],
  image: Option[String],
  following: Boolean
) extends ProfileAttributes
