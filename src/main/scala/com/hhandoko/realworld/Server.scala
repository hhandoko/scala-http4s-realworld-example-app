package com.hhandoko.realworld

import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, Timer}
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import com.hhandoko.realworld.tag.{TagRoutes, TagService}

object Server {

  def stream[F[_]: ConcurrentEffect: ContextShift: Timer]: Stream[F, ExitCode] = {
    val tagService = TagService.impl[F]
    val routes     = TagRoutes[F](tagService).orNotFound
    val httpApp    = Logger.httpApp(logHeaders = true, logBody = true)(routes)

    BlazeServerBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(httpApp)
      .serve
  }

}
