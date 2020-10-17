import java.io.Closeable
import scala.io.Source

import io.github.davidmweber.FlywayPlugin.autoImport._
import sbt.Keys._
import sbt._

object Common {

  // Dependency versions
  private val doobieVersion     = "0.9.2"
  private val flywayVersion     = "7.0.2"
  private val http4sVersion     = "0.21.7"
  private val jansiVersion      = "1.18"
  private val oauthJwtVersion   = "3.11.0"
  private val pureConfigVersion = "0.14.0"

  // Transient dependency versions
  // ~ doobie
  private val h2Version       = "1.4.200"
  private val postgresVersion = "42.2.14"
  // ~ http4s
  private val circeVersion   = "0.13.0"
  private val logbackVersion = "1.2.3"
  private val specs2Version  = "4.10.2"

  // Compiler plugin (incl. Graal) dependency versions
  private val betterMonadicForVersion = "0.3.1"
  private val kindProjectorVersion    = "0.11.0"
  private val graalVmVersion          = "20.2.0"

  final val settings: Seq[Setting[_]] =
    projectSettings ++ dependencySettings ++ flywaySettings ++ compilerPlugins

  private[this] def projectSettings = Seq(
    organization := "com.hhandoko",
    name := "realworld",
    version := using(Source.fromFile("VERSION.txt")) { _.mkString },
    scalaVersion := "2.13.3",
    mainClass in Compile := Some("com.hhandoko.realworld.Application")
  )

  private[this] def dependencySettings = Seq(
    libraryDependencies ++= Seq(
      "ch.qos.logback"          %  "logback-classic"        % logbackVersion,
      "com.auth0"               %  "java-jwt"               % oauthJwtVersion,
      "com.github.pureconfig"   %% "pureconfig"             % pureConfigVersion,
      "com.github.pureconfig"   %% "pureconfig-cats-effect" % pureConfigVersion,
      "com.h2database"          %  "h2"                     % h2Version % Test,
      "io.circe"                %% "circe-generic"          % circeVersion,
      "org.fusesource.jansi"    %  "jansi"                  % jansiVersion,
      "org.flywaydb"            %  "flyway-core"            % flywayVersion % Test,
      "org.graalvm.nativeimage" % "svm"                     % graalVmVersion % Provided,
      "org.http4s"              %% "http4s-blaze-server"    % http4sVersion,
      "org.http4s"              %% "http4s-circe"           % http4sVersion,
      "org.http4s"              %% "http4s-dsl"             % http4sVersion,
      "org.postgresql"          %  "postgresql"             % postgresVersion,
      "org.scalameta"           %% "svm-subs"               % graalVmVersion,
      "org.specs2"              %% "specs2-core"            % specs2Version % Test,
      "org.tpolecat"            %% "doobie-core"            % doobieVersion,
      "org.tpolecat"            %% "doobie-h2"              % doobieVersion % Test,
      "org.tpolecat"            %% "doobie-hikari"          % doobieVersion,
      "org.tpolecat"            %% "doobie-postgres"        % doobieVersion,
      "org.tpolecat"            %% "doobie-specs2"          % doobieVersion % Test
    )
  )

  private[this] def compilerPlugins = Seq(
    // Add syntax for type lambdas
    // See: https://github.com/non/kind-projector
    addCompilerPlugin("org.typelevel" %% "kind-projector" % kindProjectorVersion cross CrossVersion.full),

    // Desugaring scala `for` without implicit `withFilter`s
    // See: https://github.com/oleg-py/better-monadic-for
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % betterMonadicForVersion)
  )

  private[this] def flywaySettings = Seq(
    // Flyway database schema migrations
    flywayUrl := "jdbc:postgresql://0.0.0.0:5432/postgres",
    flywayUser := "postgres",
    flywayPassword := "S3cret!",

    // Separate the schema and seed, as unit tests does not require seed test data
    flywayLocations := Seq("filesystem:db/migration/postgresql", "filesystem:db/seed")
  )

  /**
   * Basic auto-closing implementation for closeable resource.
   *
   * Required as sbt 1.4.x is still on Scala 2.12.
   * 
   * @param res Closeable resource.
   * @param fn Lambda function performing resource operations.
   * @tparam T Resource type parameters.
   * @tparam U Lambda function result type parameters.
   * @return Lambda function result.
   */
  private[this] def using[T <: Closeable, U](res: T)(fn: T => U): U =
    try { fn(res) } finally { res.close() }
}
