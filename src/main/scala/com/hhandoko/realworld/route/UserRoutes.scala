package com.hhandoko.realworld.route

import cats.effect.{ContextShift, Sync}
import cats.implicits._
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, HttpRoutes}

import com.hhandoko.realworld.auth.RequestAuthenticator
import com.hhandoko.realworld.core.Username
import com.hhandoko.realworld.route.common.UserResponse
import com.hhandoko.realworld.service.UserService

object UserRoutes {

  def apply[F[_]: ContextShift: Sync](authenticated: RequestAuthenticator[F], userService: UserService[F]): HttpRoutes[F] = {
    object dsl extends Http4sDsl[F]; import dsl._

    authenticated {
      AuthedRoutes.of[Username, F] {
        case GET -> Root / "user" as username =>
          for {
            usrOpt <- userService.get(username)
            res    <- usrOpt.fold(NotFound()) { usr =>
              Ok(UserResponse(usr.email, usr.token.value, usr.username.value, usr.bio, usr.image))
            }
          } yield res
      }
    }
  }
}
