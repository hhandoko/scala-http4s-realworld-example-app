package com.hhandoko.realworld

import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, Timer}
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import com.hhandoko.realworld.tag.{TagRoutes, TagService}

object Server {

  def stream[F[_]: ConcurrentEffect: ContextShift: Timer]: Stream[F, ExitCode] = {
    val host = Option(System.getenv("APP_HOST")).getOrElse("0.0.0.0")
    val port = Option(System.getenv("APP_PORT")).map(_.toInt).getOrElse(8080)

    val tagService   = TagService.impl[F]
    val routes       = TagRoutes[F](tagService)
    // TODO: Use configuration to enable header and body logging for development only
    val loggedRoutes = Logger.httpRoutes(logHeaders = true, logBody = true) { routes }

    BlazeServerBuilder[F]
      .bindHttp(port, host)
      .withHttpApp(loggedRoutes.orNotFound)
      .serve
  }

}
