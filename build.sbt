name := """wicked-playground"""

import Dependencies._

lazy val root = project.in(file("."))
  .settings(Common.settings)
  .aggregate(core, clapi, server, workbenchScalajs)

lazy val core = project.in(file("modules/core"))
  .settings(Common.settings)
  .settings(libraryDependencies ++= Seq())

lazy val clapi = project.in(file("modules/clapi"))
  .settings(Common.settings)
  .settings(libraryDependencies ++= Seq(
    `json4s-jackson`,
    `json4s-ext`,
    scalaTest % Test
  ))
  .dependsOn(core)

lazy val server = project.in(file("modules/server"))
  .settings(Common.settings)
  .settings(libraryDependencies ++= Seq(
    `akka-http-experimental`,
    cats,
    scalaz,
    `akka-slf4j`,
    `logback-classick`,
    `json4s-jackson`,
    `json4s-ext`,
    upicle,
    `akka-http-circe`,
    scalaTest % Test
  ))
  .dependsOn(core)


import com.lihaoyi.workbench.Plugin._

lazy val workbenchScalajs = project.in(file("modules/workbenchScalajs"))
  .settings(Common.settings)
  .settings(libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "scalatags" % scalaTagsVersion,
    "com.lihaoyi" %%% "scalarx" % scalaRxVersion,
    "be.doeraene" %%% "scalajs-jquery" % doeraeneScalajsJQueryVersion,
    "org.scala-js" %%% "scalajs-dom" % scalajsDomVersion
  ))
  .enablePlugins(org.scalajs.sbtplugin.ScalaJSPlugin)
  .settings(workbenchSettings)
  .settings(
    refreshBrowsers <<= refreshBrowsers.triggeredBy(fastOptJS in Compile),
    bootSnippet := "wp.WorkbenchApp().main(document.getElementById('mainDiv'));"
  )
  .dependsOn(core)
