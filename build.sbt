name := """wicked-playground"""

import Dependencies._

lazy val root = project.in(file("."))
  .settings(Common.settings)
  .aggregate(core, clapi, server, frontend, testGoodies, functorsAndFriends)

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
  .dependsOn(core, testGoodies % "test->test")

lazy val testGoodies = project.in(file("modules/testGoodies"))
  .settings(Common.settings)
  .settings(libraryDependencies ++= Seq(
    scalaTest % Test,
    scalaCheck % Test,
    discipline % Test
  ))

lazy val server = project.in(file("modules/server"))
  .settings(Common.settings)
  .settings(libraryDependencies ++= Seq(
    `akka-http-experimental`,
    cats,
    scalazCore,
    `akka-slf4j`,
    `logback-classick`,
    `json4s-jackson`,
    `json4s-ext`,
    upicle,
    `akka-http-circe`,
    scalaTest % Test
  ))
  .dependsOn(core, testGoodies % "test->test")


import com.lihaoyi.workbench.Plugin._

lazy val frontend = project.in(file("modules/frontend"))
  .settings(Common.settings)
  .settings(libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "scalatags" % scalaTagsVersion,
    "com.lihaoyi" %%% "scalarx" % scalaRxVersion,
    "be.doeraene" %%% "scalajs-jquery" % doeraeneScalajsJQueryVersion,
    "org.scala-js" %%% "scalajs-dom" % scalajsDomVersion,
    scalaTest % Test,
    "com.lihaoyi" %%% "utest" % uTestVersion
  ))
  .settings(
    testFrameworks += new TestFramework("utest.runner.Framework"),
    jsDependencies += RuntimeDOM
//    ,scalaJSUseRhino in Global := false
  )
  .enablePlugins(org.scalajs.sbtplugin.ScalaJSPlugin)
  .settings(workbenchSettings)
  .settings(
    refreshBrowsers <<= refreshBrowsers.triggeredBy(fastOptJS in Compile),
    bootSnippet := "wp.WorkbenchApp().main(document.getElementById('mainDiv'));"
  )
  .dependsOn(core, testGoodies % "test->test")


lazy val functorsAndFriends = (project in file("modules/functorsAndFriends"))
  .settings(Common.settings)
  .settings(
    libraryDependencies ++= Seq(
      cats,// exclude("org.scalacheck", "scalacheck_2.11" /*1.12.5*/),
      scalazCore, scalazEffect, scalazConcurrent, scalazEffect,
      spireMath,
      resetAllAttrs,
      paradiseCompilerPlugin,
      kindProjectorCompilerPlugin
    )
  )
  .dependsOn(testGoodies % "test->test")