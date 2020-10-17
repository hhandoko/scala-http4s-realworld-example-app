package com.hhandoko.realworld

import scala.concurrent.ExecutionContext

import cats.effect.{Async, Blocker, ConcurrentEffect, ContextShift, Resource, Sync, Timer}
import cats.implicits._
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.http4s.HttpRoutes
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import org.http4s.server.{Server => BlazeServer}
import pureconfig.module.catseffect.loadConfigF

import com.hhandoko.realworld.auth.RequestAuthenticator
import com.hhandoko.realworld.config.{Config, DbConfig, LogConfig, ServerConfig}
import com.hhandoko.realworld.route.{ArticleRoutes, AuthRoutes, ProfileRoutes, TagRoutes, UserRoutes}
import com.hhandoko.realworld.service.{ArticleService, AuthService, ProfileService, TagService, UserService}

object Server {

  def run[F[_]: ConcurrentEffect: ContextShift: Timer]: Resource[F, BlazeServer[F]] = {
    val articleService = ArticleService[F]
    val authService    = AuthService[F]
    val profileService = ProfileService[F]
    val tagService     = TagService[F]
    val userService    = UserService[F]

    val authenticator = new RequestAuthenticator[F]()

    val routes =
      ArticleRoutes[F](articleService) <+>
      AuthRoutes[F](authService) <+>
      ProfileRoutes[F](profileService) <+>
      TagRoutes[F](tagService) <+>
      UserRoutes[F](authenticator, userService)

    for {
      conf <- config[F]
      _    <- transactor[F](conf.db)
      rts   = loggedRoutes(conf.log, routes)
      svr  <- server[F](conf.server, rts)
    } yield svr
  }

  private[this] def config[F[_]: ContextShift: Sync]: Resource[F, Config] = {
    import pureconfig.generic.auto._

    for {
      blocker <- Blocker[F]
      config  <- Resource.liftF(loadConfigF[F, Config](blocker))
    } yield config
  }

  private[this] def loggedRoutes[F[_]: ConcurrentEffect](config: LogConfig, routes: HttpRoutes[F]): HttpRoutes[F] =
    Logger.httpRoutes(config.httpHeader, config.httpBody) { routes }

  private[this] def server[F[_]: ConcurrentEffect: ContextShift: Timer](
    config: ServerConfig,
    routes: HttpRoutes[F]
  ): Resource[F, BlazeServer[F]] = {
    import org.http4s.implicits._

    BlazeServerBuilder[F](ExecutionContext.global)
      .bindHttp(config.port, config.host)
      .withHttpApp(routes.orNotFound)
      .resource
  }

  private[this] def transactor[F[_]: Async: ContextShift](config: DbConfig): Resource[F, HikariTransactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool(config.pool)
      be <- Blocker[F]
      tx <-
        HikariTransactor.newHikariTransactor(
          config.driver,
          config.url,
          config.user,
          config.password,
          ce,
          be)
    } yield tx
}
