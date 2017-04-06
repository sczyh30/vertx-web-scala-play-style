import sbt.Keys._
import sbt._

object Build extends AutoPlugin {

  override def trigger = allRequirements

  override def projectSettings =
    Vector(
      scalaVersion := Version.Scala,
      scalacOptions ++= Vector(
        "-unchecked",
        "-deprecation",
        "-language:_",
        "-target:jvm-1.8",
        "-encoding", "UTF-8"
      )
    )
}