import sbt.Keys._
import sbt.Tests.Argument
import sbt._

object Common {
  val appVersion = "0.1-SNAPSHOT"

  val settings: Seq[Def.Setting[_]] = Seq(
    version := appVersion,
    scalaVersion := "2.11.8",
    organization := "jawp",
    scalacOptions ++= Seq(
      //based on http://blog.threatstack.com/useful-scalac-options-for-better-scala-development-part-1
      //google for "scalac man apge"
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
    scalacOptions in Test ++= Seq(
      //less moaning options for tests
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
//      "-unchecked",
//      "-deprecation",
      "-feature",
      "-Xfuture"
//      "-Yno-adapted-args",
//      "-Ywarn-dead-code",
//      "-Ywarn-numeric-widen",
//      "-Ywarn-value-discard",
//      "-Ywarn-unused"
    ),
    testOptions in Test += Tests.Argument("-oD", "-F", "10")   //org.scalatest.tools.Runner - description of options
  )


  /**
    * replace '-F <timeSpan>' options, they are not supported in scalatest-js
    * @return
    */
  def replaceSpanFactor(options: Seq[sbt.TestOption]) = options.map {

    case a: Argument if a.args.contains("-F") => a.copy(args = a.args.patch(a.args.indexOf("-F"), Nil, 2))
    case x => x
  }
}