package com.hhandoko.realworld.config

final case class DbConfig(
  driver: String,
  url: String,
  user: String,
  password: String,
  pool: Int
)
