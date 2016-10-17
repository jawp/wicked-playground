import sbt.Keys._
import sbt._

object Common {
  val appVersion = "0.1-SNAPSHOT"

  var settings: Seq[Def.Setting[_]] = Seq(
    version := appVersion,
    scalaVersion := "2.11.8",
    organization := "jawp",
    scalacOptions ++= Seq( //based on http://blog.threatstack.com/useful-scalac-options-for-better-scala-development-part-1
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
      "-unchecked",
      "-deprecation",
      "-feature",
      "-Xfuture",
//      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Ywarn-unused"
    ),
    testOptions in Test += Tests.Argument("-oD", "-F", "10")   //org.scalatest.tools.Runner - description of options
  )
}