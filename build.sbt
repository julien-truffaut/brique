import pl.project13.scala.sbt.SbtJmh._

lazy val buildSettings = Seq(
  organization := "org.julien-truffaut",
  scalaVersion := "2.11.6"
)

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:experimental.macros",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xlint",
    "-Yno-adapted-args",
    "-Yno-predef",
    "-Yno-imports",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Xfuture"
  ),
  resolvers ++= Seq(
    "bintray/non" at "http://dl.bintray.com/non/maven",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),
  libraryDependencies ++= Seq(
    "org.spire-math" %% "cats-core" % "0.1.0-SNAPSHOT"
  )
)

lazy val briqueSettings = buildSettings ++ commonSettings

lazy val disciplineDependencies = Seq(
  "org.scalacheck" %% "scalacheck" % "1.11.3"
)

lazy val aggregate = project.in(file("."))
  .settings(briqueSettings: _*)
  .aggregate(core, tests, bench)
  .dependsOn(core, tests, bench)

lazy val core = project
  .settings(moduleName := "brique-core")
  .settings(briqueSettings: _*)

lazy val tests = project.dependsOn(core)
  .settings(moduleName := "brique-tests")
  .settings(briqueSettings: _*)
  .settings(
    libraryDependencies ++= disciplineDependencies ++ Seq(
      "org.scalatest" %% "scalatest" % "2.1.3" % "test"
    )
  )

lazy val bench = project.dependsOn(core)
  .settings(moduleName := "brique-bench")
  .settings(briqueSettings: _*)
  .settings(jmhSettings: _*)
