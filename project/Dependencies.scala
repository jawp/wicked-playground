import sbt._

object Dependencies {

  val akkaVersion = "2.4.10"
  val logbackVersion = "1.1.7"
  val scalatestVersion = "3.0.0"
  val scalacheckVersion = "1.13.2"
  val catsVersion = "0.7.2"
  val scalazVersion = "7.2.2"
  val spireMathVerion = "0.12.0"
  val json4sVersion = "3.3.0"
  val nScalaTimeVersion = "2.10.0"
  val playScalaJsScriptsVersion = "0.4.0"
  val slickVersion = "3.1.1"
  val playSlickVersion = "2.0.0"
  val uPicleVersion = "0.3.9"
  val webJarsPlayVersion = "2.5.0"
  val bootstrapVersion = "3.3.6"
  val webJarsJQueryVersion = "2.2.2"
  val webJarsFontAwesomeVersion = "4.5.0"
  val scalajsDomVersion = "0.9.0"
  val scalaTagsVersion = "0.5.4"
  val scalaRxVersion = "0.3.1"
  val doeraeneScalajsJQueryVersion = "0.9.0"
  val breezeVersion = "0.12"
  val spireVersion = "0.11.0"
  val sparkVersion = "1.6.1"
  val specs2Version = "3.7.2"

  val `akka-core` = "com.typesafe.akka" %% "akka-http-core" % akkaVersion
  val `akka-http-experimental` = "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion
  val `akka-http-spray-json-experimental` = "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion
  val `akka-http-testkit` = "com.typesafe.akka" %% "akka-http-testkit" % akkaVersion
  val `akka-stream-testkit` = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
  val `akka-slf4j` = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  val `logback-classick` = "ch.qos.logback" % "logback-classic" % logbackVersion
  val scalaTest = "org.scalatest" %% "scalatest" % scalatestVersion
  val scalaCheck = "org.scalacheck" %% "scalacheck" % scalacheckVersion
  val cats = "org.typelevel" %% "cats" % catsVersion
  val scalaz = "org.scalaz" %% "scalaz-core" % scalazVersion
  val `spire-math` = "org.spire-math" %% "spire" % spireMathVerion
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


  import org.scalajs.sbtplugin.ScalaJSPlugin
  import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

  //  val `scalajs-dom` = "org.scala-js"                            %%% "scalajs-dom"     % scalajsDomVersion
  //  val scalatags = "com.lihaoyi"                             %%% "scalatags"       % scalaTagsVersion
  //  val scalarx = "com.lihaoyi"                             %%% "scalarx"         % scalaRxVersion
  //  val `scalajs-jquery` = "be.doeraene"                             %%% "scalajs-jquery"  % doeraeneScalajsJQueryVersion
}
