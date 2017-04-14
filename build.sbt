name := """wicked-playground-xxx"""

import Dependencies._
import org.scalajs.sbtplugin.cross.CrossProject
import spray.revolver.RevolverPlugin._

lazy val root = project.in(file("."))
  .settings(Common.settings)
  .aggregate(sharedJs, sharedJvm, server, frontend, clapi)

lazy val server = project.in(file("modules/server"))
  .settings(Revolver.settings: _*)
  .settings(mainClass in Revolver.reStart := Some("wp.ServerMain"))
  .settings(Common.settings)
  .settings(libraryDependencies ++= Seq(
    `akka-actor`, `akka-stream`, `akka-slf4j`, `akka-agent`,
    `akka-http`, `akka-http-testkit`,
    `logback-classick`,
    `json4s-jackson`,
    `json4s-ext`,
    upicle,
    scalaTest % Test,
    scalarx,
    scalatags,
    cats, // exclude("org.scalacheck", "scalacheck_2.11" /*1.12.5*/),
    scalazCore, scalazEffect, scalazConcurrent, scalazEffect,
    spireMath,
    paradiseCompilerPlugin,
    kindProjectorCompilerPlugin,
    macwireMacros,
    macwireUtil,
    macwireProxy
  ))
  .settings((resourceGenerators in Compile) <+=
    (fastOptJS in Compile in frontend,
      packageScalaJSLauncher in Compile in frontend)
      .map((f1, f2) => Seq(f1.data, f2.data)),
    watchSources <++= (watchSources in frontend)) //TODO watchSources ++= (watchSources in frontend).value)
  .dependsOn(sharedJvmCp)

lazy val frontend = project.in(file("modules/frontend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(Common.settings)
  .settings(libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "scalatags" % scalaTagsVersion,
    "com.lihaoyi" %%% "scalarx" % scalaRxVersion,
    "be.doeraene" %%% "scalajs-jquery" % doeraeneScalajsJQueryVersion,
    "org.scala-js" %%% "scalajs-dom" % scalajsDomVersion,
    "org.scalatest" %%% "scalatest" % scalatestVersion % Test,
    "com.lihaoyi" %%% "utest" % uTestVersion,
    "com.github.japgolly.scalajs-react" %%% "core" % scalaJsReactVersion
  ))
  .settings(jsDependencies ++= Seq(
    RuntimeDOM,
    "org.webjars.bower" % "react" % reactVersion
      /        "react-with-addons.js"
      minified "react-with-addons.min.js"
      commonJSName "React",

    "org.webjars.bower" % "react"  % reactVersion
      /         "react-dom.js"
      minified  "react-dom.min.js"
      dependsOn "react-with-addons.js"
      commonJSName "ReactDOM",

    "org.webjars.bower" % "react" % reactVersion
      /         "react-dom-server.js"
      minified  "react-dom-server.min.js"
      dependsOn "react-dom.js"
      commonJSName "ReactDOMServer"
  ))
  .settings(
    persistLauncher in Compile := true,
    persistLauncher in Test := false,
    mainClass in Compile := Some("wp.PigeonsApp"),
    testFrameworks += new TestFramework("utest.runner.Framework"),
    testOptions in Test := Common.replaceSpanFactor(testOptions.value)
  )
  .dependsOn(sharedJsCp)

lazy val shared =
  CrossProject("shared", file("modules/shared"), CrossType.Pure)
    .settings(Common.settings: _*)
    .jsSettings(
      testOptions in Test := Common.replaceSpanFactor(testOptions.value),
      libraryDependencies ++= Seq(
        "org.scalatest" %%% "scalatest" % scalatestVersion % Test
      )
    )
    .jvmSettings(
      libraryDependencies ++= Seq(
        scalaTest % Test,
        scalaCheck % Test,
        discipline % Test
      )
    )

lazy val sharedJvm = shared.jvm

lazy val sharedJvmCp =
  sharedJvm % "compile -> compile; test -> test"

lazy val sharedJs = shared.js

lazy val sharedJsCp =
  sharedJs % "compile -> compile; test -> test"

lazy val clapi = project.in(file("modules/clapi"))
  .settings(Common.settings)
  .settings(libraryDependencies ++= Seq(
    `json4s-jackson`,
    `json4s-ext`,
    scalaTest % Test
  ))
  .dependsOn(sharedJvmCp)

