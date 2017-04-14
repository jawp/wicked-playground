import sbt._

object Dependencies {

  val akkaVersion = "2.5.0"
  val akkaHttpVersion = "10.0.5"
  val logbackVersion = "1.1.7"
  val scalatestVersion = "3.0.1"
  val scalacheckVersion = "1.13.2"
  val catsVersion = "0.9.0"
  val scalazVersion = "7.2.11"
  val spireMathVerion = "0.13.0"
  val json4sVersion = "3.5.1"
  val nScalaTimeVersion = "2.10.0"
  val playScalaJsScriptsVersion = "0.4.0"
  val slickVersion = "3.1.1"
  val playSlickVersion = "2.0.0"

  val uPicleVersion = "0.4.4"
  val webJarsPlayVersion = "2.5.0"
  val bootstrapVersion = "3.3.6"
  val webJarsJQueryVersion = "2.2.2"
  val webJarsFontAwesomeVersion = "4.5.0"

  val scalajsDomVersion = "0.9.1"
  val scalaTagsVersion = "0.6.3" //https://repo1.maven.org/maven2/com/lihaoyi/scalatags_sjs0.6_2.11/
  val scalaRxVersion = "0.3.2"
  val uTestVersion = "0.4.5"
  val doeraeneScalajsJQueryVersion = "0.9.1"
  val scalaJsReactVersion = "0.11.3"
  val reactVersion  = "15.3.2"

  val breezeVersion = "0.12"
  val sparkVersion = "2.0.1"
  val sparkTestingBaseVersion = "2.0.1_0.4.7" //https://mvnrepository.com/artifact/com.holdenkarau
  val specs2Version = "3.7.2"
  val akkaHttpCirce = "1.10.0" //todo update and remove package de.heiko.... https://dl.bintray.com/hseeberger/maven/de/heikoseeberger/akka-http-circe_2.11/1.10.0-1-ga514d78/
  val paradiseVersion = "2.1.0"
  val kindProjectorVersion = "0.9.3"
  val disciplineVersion = "0.7.3" //cats-kernel-laws introduces old version of discipline and scalacheck. watch out!
  val databricksVersion = "1.5.0" //https://github.com/databricks/spark-csv
  val macWireVersion = "2.3.0"

  val `akka-actor` = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val `akka-agent` = "com.typesafe.akka" %% "akka-agent" % akkaVersion
  val `akka-stream` = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val `akka-stream-testkit` = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
  val `akka-slf4j` = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  val `akka-http` = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  val `akka-http-testkit` = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
  val `logback-classick` = "ch.qos.logback" % "logback-classic" % logbackVersion
  val scalaTest = "org.scalatest" %% "scalatest" % scalatestVersion
  val scalaCheck = "org.scalacheck" %% "scalacheck" % scalacheckVersion
  val cats = "org.typelevel" %% "cats" % catsVersion
  val discipline = "org.typelevel" %% "discipline" % disciplineVersion
  val paradiseCompilerPlugin = compilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)
  val kindProjectorCompilerPlugin = compilerPlugin("org.spire-math" %% "kind-projector" % kindProjectorVersion)

  val scalazCore = "org.scalaz" %% "scalaz-core" % scalazVersion
  val scalazEffect = "org.scalaz" %% "scalaz-effect" % scalazVersion
  val scalazConcurrent = "org.scalaz" %% "scalaz-concurrent" % scalazVersion
  val scalazIteratee = "org.scalaz" %% "scalaz-iteratee" % scalazVersion
  val spireMath = "org.spire-math" %% "spire" % spireMathVerion
  val `play-scalajs-scripts` = "com.vmunier" %% "play-scalajs-scripts" % playScalaJsScriptsVersion
  val slick = "com.typesafe.slick" %% "slick" % slickVersion
  val `play-slick` = "com.typesafe.play" %% "play-slick" % playSlickVersion
  val upicle = "com.lihaoyi" %% "upickle" % uPicleVersion
  val `webjars-play` = "org.webjars" %% "webjars-play" % webJarsPlayVersion
  val `webjars-bootstrap` = "org.webjars" % "bootstrap" % bootstrapVersion
  val `webjars-query` = "org.webjars" % "jquery" % webJarsJQueryVersion
  val `webjars-font-awesome` = "org.webjars" % "font-awesome" % webJarsFontAwesomeVersion
  val `json4s-jackson` = "org.json4s" %% "json4s-jackson" % json4sVersion
  val `json4s-ext` = "org.json4s" %% "json4s-ext" % json4sVersion
  val `nscala-time` = "com.github.nscala-time" %% "nscala-time" % nScalaTimeVersion
  val scalatags = "com.lihaoyi" %% "scalatags" % scalaTagsVersion
  val scalarx = "com.lihaoyi" %% "scalarx" % scalaRxVersion

  val breeze = "org.scalanlp" %% "breeze" % breezeVersion
  val breezeNatives = {
    // native libraries are not included by default. add this if you want them (as of 0.7)
    // native libraries greatly improve performance, but increase jar sizes.
    // It also packages various blas implementations, which have licenses that may or may not
    // be compatible with the Apache License. No GPL code, as best I know.
    "org.scalanlp" %% "breeze-natives" % breezeVersion
  }
  val breezeViz = {
    // the visualization library is distributed separately as well.
    // It depends on LGPL code.
    "org.scalanlp" %% "breeze-viz" % breezeVersion
  }
  val spark = "org.apache.spark" %% "spark-core" % sparkVersion
  val sparkSql = "org.apache.spark" %% "spark-sql" % sparkVersion
  val sparkCsv = "org.apache.spark" %% "spark-csv" % sparkVersion
  val dataBricksCsv = "com.databricks" %% "spark-csv" % databricksVersion
  val mllib = "org.apache.spark" %% "spark-mllib" % sparkVersion
  val mllibLocal = "org.apache.spark" %% "spark-mllib-local" % sparkVersion
  val sparkHive = "org.apache.spark" %% "spark-hive" % sparkVersion
  val sparkTestingBase = "com.holdenkarau" %% "spark-testing-base" % sparkTestingBaseVersion

  // https://mvnrepository.com/artifact/com.softwaremill.macwire/macros_2.12
  val macwireMacros = "com.softwaremill.macwire" %% "macros" % macWireVersion  % "provided"
  val macwireUtil = "com.softwaremill.macwire" %% "util" % macWireVersion
  val macwireProxy = "com.softwaremill.macwire" %% "proxy" % macWireVersion


}
