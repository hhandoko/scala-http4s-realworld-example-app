package com.hhandoko.realworld

import scala.concurrent.ExecutionContext.global

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import com.hhandoko.realworld.tag.{TagRoutes, TagService}

object RealworldServer {

  def stream[F[_]: ConcurrentEffect: ContextShift](implicit T: Timer[F]): Stream[F, Nothing] = {
    for {
      client        <- BlazeClientBuilder[F](global).stream
      helloWorldAlg  = HelloWorld.impl[F]
      jokeAlg        = Jokes.impl[F](client)
      tagService     = TagService.impl[F]

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        RealworldRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
        RealworldRoutes.jokeRoutes[F](jokeAlg) <+>
        TagRoutes[F](tagService)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(logHeaders = true, logBody = true)(httpApp)

      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}