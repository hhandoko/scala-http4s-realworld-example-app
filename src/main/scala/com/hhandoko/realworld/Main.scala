package com.hhandoko.realworld

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    Server.run[IO]
      .use(_ => IO.never.as(ExitCode.Success))

}
