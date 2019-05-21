package com.hhandoko.realworld.core

import java.time.ZonedDateTime

final case class Article(
  slug: String,
  title: String,
  description: String,
  body: String,
  tagList: Set[String],
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime,
  favorited: Boolean,
  favoritesCount: Long,
  author: Author
)
