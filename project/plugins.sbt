// Load environment variables for local development
// See: https://github.com/mefellows/sbt-dotenv
addSbtPlugin("au.com.onegeek" %% "sbt-dotenv" % "2.1.146")

// Add `assembly` task for creating uber-jar
// See: https://github.com/sbt/sbt-assembly
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.10")

// Check Maven and Ivy repositories for dependency updates
// See: https://github.com/rtimush/sbt-updates
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.5.1")

// Pass recommended Scala compiler flags
// See: https://github.com/DavidGregory084/sbt-tpolecat
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.11")

// Flyway migrations support in SBT
// See: https://github.com/flyway/flyway-sbt
addSbtPlugin("io.github.davidmweber" % "flyway-sbt" % "5.2.0")

// Enable app restarts for better development experience
// See: https://github.com/spray/sbt-revolver
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

// Pass partial unification Scala compiler flag
// See: https://github.com/fiadliel/sbt-partial-unification
addSbtPlugin("org.lyranthe.sbt" % "partial-unification" % "1.1.2")
