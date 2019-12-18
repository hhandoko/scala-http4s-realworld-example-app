package com.hhandoko.realworld

import java.util.UUID

import cats.effect.{ContextShift, IO}
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway
import org.specs2.specification.{BeforeAll, BeforeEach}

trait RepoSpecSupport extends BeforeAll
  with BeforeEach {

  def instance: String

  private[this] final val TABLES = Seq("profile", "auth")

  private[this] final val SCHEMA_LOCATION = "filesystem:db/migration/h2"
  private[this] final val DRIVER_CLASS_NAME = "org.h2.Driver"
  private[this] final val USERNAME = "sa"
  private[this] final val PASSWORD = ""

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

  lazy final val url: String =
    s"jdbc:h2:./target/db/${instance}_test_${UUID.randomUUID().toString};MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"

  lazy final val transactor =
    Transactor.fromDriverManager[IO](DRIVER_CLASS_NAME, url, USERNAME, PASSWORD)

  def beforeAll(): Unit = {
    Flyway.configure()
      .dataSource(url, USERNAME, PASSWORD)
      .locations(SCHEMA_LOCATION)
      .load()
      .migrate()
    ()
  }

  def before: Unit =
    TABLES.foreach { table =>
      sql"""TRUNCATE TABLE ${table} RESTART IDENTITY CASCADE""".update.run
    }

}
