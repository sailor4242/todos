import Dependencies._

lazy val root = project.in(file("."))
  .settings(Seq(
    name := "todos",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.12.6",
    libraryDependencies ++= all,
    mainClass in (Compile, run) := Some("todo.Main"),
    mainClass in assembly := Some("todo.Main"),
    assemblyJarName in assembly := "todos.jar",
    scalacOptions ++= Seq(
      "-encoding",
      "UTF-8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint",
      "-language:higherKinds",
      "-Ywarn-unused-import",
      "-Ywarn-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-inaccessible",
      "-Ywarn-nullary-override",
      "-Ywarn-numeric-widen",
      "-Xfatal-warnings")
  ))

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
resolvers += "Typesafe" at "https://repo.typesafe.com/typesafe/releases/"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
testFrameworks += new TestFramework("utest.runner.Framework")

assemblyMergeStrategy in assembly := {
  case x if x.endsWith("io.netty.versions.properties") => MergeStrategy.first
  case x => MergeStrategy.defaultMergeStrategy(x)
}

val validateCommands = List(
  "clean",
  "compile",
  "test:compile",
  "test"
)

addCommandAlias("validate", validateCommands.mkString(";", ";", ""))
