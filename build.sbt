name := """wicked-playground"""

import Dependencies._

lazy val `root` = project.in(file("."))
  .settings(Common.settings)
  .aggregate(`core`, `clapi`, `server`)

lazy val `core` = project.in(file("modules/core"))
  .settings(Common.settings)
  .settings(libraryDependencies ++= Seq())

lazy val `clapi` = project.in(file("modules/clapi"))
  .settings(Common.settings)
  .settings(libraryDependencies ++= Seq(
    `json4s-jackson`,
    `json4s-ext`,
    scalaTest % Test
  ))
  .dependsOn(`core`)

lazy val `server` = project.in(file("modules/server"))
  .settings(Common.settings)
  .settings(libraryDependencies ++= Seq(
    `akka-http-experimental`,
    cats,
    `akka-slf4j`,
    `logback-classick`,
    `json4s-jackson`,
    `json4s-ext`,
    scalaTest % Test
  ))
  .dependsOn(`core`)
