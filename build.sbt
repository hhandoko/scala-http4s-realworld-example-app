enablePlugins(FlywayPlugin)

lazy val realworld =
  (project in file("."))
    .settings(Common.settings: _*)
