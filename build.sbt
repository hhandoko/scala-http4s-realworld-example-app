enablePlugins(FlywayPlugin)
enablePlugins(JavaAppPackaging)

lazy val realworld =
  (project in file("."))
    .settings(Common.settings: _*)
