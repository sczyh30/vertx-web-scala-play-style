import sbt._

object Version {
  final val Scala = "2.12.1"
  final val Vertx = "3.4.1"
}

object Library {
  val vertxLangScala = "io.vertx" %% "vertx-lang-scala" % Version.Vertx
  val vertxWeb = "io.vertx" %% "vertx-web-scala" % Version.Vertx
}