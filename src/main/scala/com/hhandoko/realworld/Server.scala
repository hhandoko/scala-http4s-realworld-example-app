package com.hhandoko.realworld

import cats.effect.{ConcurrentEffect, ContextShift, Resource, Sync, Timer}
import org.http4s.HttpRoutes
import org.http4s.server.{Server => BlazeServer}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import pureconfig.module.catseffect.loadConfigF

import com.hhandoko.realworld.config.Config
import com.hhandoko.realworld.tag.{TagRoutes, TagService}

object Server {

  def run[F[_]: ConcurrentEffect: ContextShift: Timer]: Resource[F, BlazeServer[F]] = {
    val tagService   = TagService.impl[F]
    val routes       = TagRoutes[F](tagService)
    // TODO: Use configuration to enable header and body logging for development only
    val loggedRoutes = Logger.httpRoutes(logHeaders = true, logBody = true) { routes }

    for {
      conf <- config[F]
      svr  <- server[F](conf, loggedRoutes)
    } yield svr
  }

  private[this] def config[F[_]: Sync]: Resource[F, Config] = {
    import pureconfig.generic.auto._

    Resource.liftF(loadConfigF[F, Config])
  }

  private[this] def server[F[_]: ConcurrentEffect: ContextShift: Timer](
    config: Config,
    routes: HttpRoutes[F]
  ): Resource[F, BlazeServer[F]] = {
    import org.http4s.implicits._

    BlazeServerBuilder[F]
      .bindHttp(config.server.port, config.server.host)
      .withHttpApp(routes.orNotFound)
      .resource
  }

}
