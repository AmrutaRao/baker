import sbt._
import Keys._
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._

object SonatypePublish {

  protected def isSnapshot(s: String) = s.trim endsWith "SNAPSHOT"

  protected val nexus = "https://oss.sonatype.org/"
  protected val ossSnapshots = "Sonatype OSS Snapshots" at nexus + "content/repositories/snapshots/"
  protected val ossStaging = "Sonatype OSS Staging" at nexus + "service/local/staging/deploy/maven2/"

  val settings = Seq(
    licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
    homepage := Some(url("https://github.com/ing-bank/baker")),
    pomExtra := (
      <scm>
        <url>git@github.com:ing-bank/baker.git</url>
        <connection>scm:git:git@github.com:ing-bank/baker</connection>
      </scm>
        <developers>
          <developer>
            <id>Apollo</id>
            <name>Squad Apollo</name>
          </developer>
        </developers>
      ),
    publishMavenStyle := true,
    publishTo <<= version((v: String) => Some(if (isSnapshot(v)) ossSnapshots else ossStaging)),
    publishArtifact in Test := false,
    pomIncludeRepository := (_ => false),
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      ReleaseStep(action = Command.process("publishSigned", _)),
      setNextVersion,
      commitNextVersion,
      ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
      pushChanges
    )
  )
}