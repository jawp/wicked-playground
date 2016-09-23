import sbt.Keys._
import sbt._

object Common {
  val appVersion = "0.1-SNAPSHOT"

  var settings: Seq[Def.Setting[_]] = Seq(
    version := appVersion,
    scalaVersion := "2.11.8",
    organization := "jawp",
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8"),
    testOptions in Test += Tests.Argument("-oD", "-F", "10")   //org.scalatest.tools.Runner - description of options
  )
}