name := """wicked-playground"""

lazy val `root` = project.in(file("."))
  .settings(Common.settings)
  .aggregate(`core`, `clapi`, `server`)

lazy val `core` = project.in(file("modules/core"))
  .settings(Common.settings)
  .settings(libraryDependencies ++= Dependencies.coreDependencies)

lazy val `clapi` = project.in(file("modules/clapi"))
  .settings(Common.settings)
  .settings(libraryDependencies ++= Dependencies.clientDependencies)
  .dependsOn(`core`)

lazy val `server` = project.in(file("modules/server"))
  .settings(Common.settings)
  .settings(libraryDependencies ++= Dependencies.serverDependencies)
  .dependsOn(`core`)
