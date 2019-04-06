package com.hhandoko.realworld

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import com.hhandoko.realworld.tag.{TagRoutes, TagService}

object RealworldServer {

  def stream[F[_]: ConcurrentEffect: ContextShift](implicit T: Timer[F]): Stream[F, Nothing] = {
    val tagService   = TagService.impl[F]
    val httpApp      = TagRoutes[F](tagService).orNotFound
    val finalHttpApp = Logger.httpApp(logHeaders = true, logBody = true)(httpApp)

    for {
      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain

}