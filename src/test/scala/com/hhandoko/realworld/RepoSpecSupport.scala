package com.hhandoko.realworld

import java.util.UUID
import javax.sql.DataSource

import cats.effect.{ContextShift, IO, Resource}
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway
import org.h2.jdbcx.JdbcDataSource
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
    s"jdbc:h2:mem:${instance}_test_${UUID.randomUUID().toString};MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"

  lazy final val ds: DataSource =
    new JdbcDataSource {
      this.setUrl(url)
      this.setUser(USERNAME)
      this.setPassword(PASSWORD)
    }

  lazy final val transactor =
    Transactor.fromDriverManager[IO](DRIVER_CLASS_NAME, url, USERNAME, PASSWORD)

  def beforeAll(): Unit = {
    Flyway.configure()
      .dataSource(ds)
      .locations(SCHEMA_LOCATION)
      .load()
      .migrate()
    ()
  }

  def before: Unit =
    Resource.fromAutoCloseable(IO(ds.getConnection())).use { conn =>
      IO {
        TABLES
          .reverse
          .foreach { tableName =>
            conn.nativeSQL(s"TRUNCATE TABLE ${tableName} RESTART IDENTITY")
            conn.commit()
          }
      }
    }.unsafeRunSync()

  protected def execute(fr: Fragment): Unit = {
    fr.update.run.transact(transactor).unsafeRunSync()
    ()
  }
}
