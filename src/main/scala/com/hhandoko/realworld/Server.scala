package com.hhandoko.realworld

import cats.effect.{Async, Blocker, ConcurrentEffect, ContextShift, Resource, Sync, Timer}
import cats.implicits._
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.http4s.HttpRoutes
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import org.http4s.server.{Server => BlazeServer}
import pureconfig.module.catseffect.loadConfigF

import com.hhandoko.realworld.article.ArticleService
import com.hhandoko.realworld.auth.{AuthService, RequestAuthenticator}
import com.hhandoko.realworld.config.{Config, DbConfig}
import com.hhandoko.realworld.profile.ProfileService
import com.hhandoko.realworld.route.{ArticleRoutes, AuthRoutes, ProfileRoutes, TagRoutes, UserRoutes}
import com.hhandoko.realworld.tag.TagService
import com.hhandoko.realworld.user.UserService

object Server {

  def run[F[_]: ConcurrentEffect: ContextShift: Timer]: Resource[F, BlazeServer[F]] = {
    val authenticator = new RequestAuthenticator[F]()
    val articleService = ArticleService.impl[F]
    val authService = AuthService.impl[F]
    val profileService = ProfileService.impl[F]
    val tagService = TagService.impl[F]
    val userService = UserService.impl[F]
    val routes =
      ArticleRoutes[F](articleService) <+>
      AuthRoutes[F](authService) <+>
      ProfileRoutes[F](profileService) <+>
      TagRoutes[F](tagService) <+>
      UserRoutes[F](authenticator, userService)

    for {
      conf <- config[F]
      _    <- transactor[F](conf.db)
      rts   = loggedRoutes(conf, routes)
      svr  <- server[F](conf, rts)
    } yield svr
  }

  private[this] def config[F[_]: ContextShift: Sync]: Resource[F, Config] = {
    import pureconfig.generic.auto._

    for {
      be <- Blocker[F]
      re <- Resource.liftF(loadConfigF[F, Config](be))
    } yield re
  }

  private[this] def loggedRoutes[F[_]: ConcurrentEffect](conf: Config, routes: HttpRoutes[F]): HttpRoutes[F] =
    Logger.httpRoutes(conf.log.httpHeader, conf.log.httpBody) { routes }

  private[this] def server[F[_]: ConcurrentEffect: ContextShift: Timer](
    config: Config,
    routes: HttpRoutes[F]
  ): Resource[F, BlazeServer[F]] = {
    import org.http4s.implicits._

    BlazeServerBuilder[F](scala.concurrent.ExecutionContext.global)
      .bindHttp(config.server.port, config.server.host)
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
