import com.typesafe.sbt.pgp.PgpKeys.publishSigned
import pl.project13.scala.sbt.SbtJmh._
import sbtrelease.ReleaseStep
import sbtrelease.ReleasePlugin.ReleaseKeys.releaseProcess
import sbtrelease.ReleaseStateTransformations._
import sbtrelease.Utilities._

lazy val buildSettings = Seq(
  organization := "org.julien-truffaut",
  scalaVersion := "2.11.5",
  crossScalaVersions := Seq("2.11.5")
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
    "org.spire-math" %% "algebra" % "0.2.0-SNAPSHOT" from "http://plastic-idolatry.com/jars/algebra_2.11-0.2.0-SNAPSHOT.jar",
    "org.typelevel" %% "machinist" % "0.3.0",
    compilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full),
    compilerPlugin("org.spire-math" %% "kind-projector" % "0.5.2")
  ),
  scmInfo := Some(ScmInfo(url("https://github.com/julien-truffaut/brique"),
    "git@github.com:julien-truffaut/brique.git"))
)

lazy val briqueSettings = buildSettings ++ commonSettings ++ publishSettings ++ releaseSettings ++ miniBoxingSettings

lazy val miniBoxingSettings = Seq(
  libraryDependencies += "org.scala-miniboxing.plugins" %% "miniboxing-runtime" % "0.4-SNAPSHOT",
  addCompilerPlugin(     "org.scala-miniboxing.plugins" %% "miniboxing-plugin"  % "0.4-SNAPSHOT")
)

lazy val aggregate = project.in(file("."))
  .settings(briqueSettings: _*)
  .settings(noPublishSettings: _*)
  .aggregate(core, tests, bench)
  .dependsOn(core, tests, bench)

lazy val core = project
  .settings(moduleName := "brique-core")
  .settings(briqueSettings: _*)

lazy val tests = project.dependsOn(core)
  .settings(moduleName := "brique-tests")
  .settings(briqueSettings: _*)
  .settings(noPublishSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest"  %% "scalatest"  % "2.1.3" % "test",
      "org.scalacheck" %% "scalacheck" % "1.11.3"
    ),
    scalacOptions += "-P:minibox:warn-off"
  )

lazy val bench = project.dependsOn(core)
  .settings(moduleName := "brique-bench")
  .settings(briqueSettings: _*)
  .settings(noPublishSettings: _*)
  .settings(jmhSettings: _*)

lazy val publishSettings = Seq(
  homepage := Some(url("https://github.com/julien-truffaut/brique")),
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  autoAPIMappings := true,
  publishMavenStyle := true,
  publishArtifact in packageDoc := false,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  publishTo <<= version { (v: String) =>
    val nexus = "https://oss.sonatype.org/"

    if (v.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := (
    <scm>
      <url>git@github.com:julien-truffaut/brique.git</url>
      <connection>scm:git:git@github.com:julien-truffaut/brique.git</connection>
    </scm>
    <developers>
      <developer>
        <id>julien-truffaut</id>
        <name>Julien Truffaut</name>
        <url>http://github.com/julien-truffaut/</url>
      </developer>
    </developers>
  ),
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishSignedArtifacts,
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
)

lazy val publishSignedArtifacts = ReleaseStep(
  action = { st =>
    val extracted = st.extract
    val ref = extracted.get(thisProjectRef)
    extracted.runAggregated(publishSigned in Global in ref, st)
  },
  check = { st =>
    // getPublishTo fails if no publish repository is set up.
    val ex = st.extract
    val ref = ex.get(thisProjectRef)
    Classpaths.getPublishTo(ex.get(publishTo in Global in ref))
    st
  },
  enableCrossBuild = true
)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)
