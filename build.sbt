name := """wicked-playground"""

import Dependencies._
import org.scalajs.sbtplugin.cross.CrossProject
import spray.revolver.RevolverPlugin._

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
  .settings(Revolver.settings: _*)
  .settings(mainClass in Revolver.reStart := Some("wp.ServerMain"))
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
    scalaTest % Test,
    "com.lihaoyi" %% "scalatags" % "0.6.0"
  ))
  .settings((resourceGenerators in Compile) <+=
    (fastOptJS in Compile in frontend,
      packageScalaJSLauncher in Compile in frontend)
      .map((f1, f2) => Seq(f1.data, f2.data)),
    watchSources <++= (watchSources in frontend))
  .dependsOn(jvmCp, core, testGoodies % "test->test")

lazy val frontend = project.in(file("modules/frontend"))
  .enablePlugins(ScalaJSPlugin)
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
    persistLauncher in Compile := true,
    persistLauncher in Test := false,
    testFrameworks += new TestFramework("utest.runner.Framework"),
    jsDependencies += RuntimeDOM
//    ,scalaJSUseRhino in Global := false
  )
  .dependsOn(jsCp, core, testGoodies % "test->test")

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

lazy val shared =
  CrossProject("shared", file("shared"), CrossType.Pure)
    .settings(Common.settings: _*)
    .settings(
      libraryDependencies ++= Seq(
        "org.scalatest" %%% "scalatest" % "3.0.0" % "test"
      )
    )

lazy val sharedJvm = shared.jvm

lazy val jvmCp =
  sharedJvm % "compile -> compile; test -> test"

lazy val sharedJs = shared.js

lazy val jsCp =
  sharedJs % "compile -> compile; test -> test"
