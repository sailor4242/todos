import sbt._

object Dependencies {
  lazy val finchVersion = "0.25.0"
  lazy val circeVersion = "0.10.1"

  lazy val finch = "com.github.finagle" %% "finch-core" % finchVersion
  lazy val finchCirce = "com.github.finagle" %% "finch-circe" % finchVersion
  lazy val circe = "io.circe" %% "circe-generic" % circeVersion
  lazy val slick = "com.typesafe.slick" %% "slick" % "3.2.3"
  lazy val slickHikari = "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3"
  lazy val slickjoda = "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0"
  lazy val jodatime = "joda-time" % "joda-time" % "2.7"
  lazy val jodaconvert = "org.joda" % "joda-convert" % "1.7"
  lazy val h2 = "com.h2database" % "h2" % "1.4.190"
  lazy val scalacheck = "org.scalacheck" %% "scalacheck" % "1.14.0" % Test
  lazy val microtest = "com.lihaoyi" %% "utest" % "0.6.5" % Test

  val all = Seq(finch, finchCirce, circe, microtest, scalacheck, slick, slickHikari,
    slickjoda, jodatime, jodaconvert, h2)
}