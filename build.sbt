name := "cube-range-sum"

scalaVersion := "2.11.6"

version := "1.0"

resolvers ++= Seq(
  "bintray-sbt-plugins" at "http://dl.bintray.com/sbt/sbt-plugin-releases",
  "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "Artima Maven Repository" at "http://repo.artima.com/releases"
)

libraryDependencies  ++= Seq(
  "com.github.finagle" % "finch-core_2.11" % "0.12.0",
  "com.twitter" %% "twitter-server" % "1.26.0",
  "com.github.finagle" %% "finch-circe" % "0.12.0",
  "io.circe" %% "circe-generic" % "0.7.0",
  "io.circe" %% "circe-parser" % "0.7.0",
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
)

val meta = """META.INF(.)*""".r

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case "BUILD" => MergeStrategy.discard
  case meta(_)  => MergeStrategy.last // or MergeStrategy.discard, your choice
  case other => MergeStrategy.defaultMergeStrategy(other)
}

// for debugging sbt problems
logLevel := Level.Debug

scalacOptions += "-deprecation"

mainClass in (Compile, run) := Some("cubesum.Server")

mainClass in (Compile, packageBin) := Some("cubesum.Server")

mainClass in assembly := Some("cubesum.Server")

exportJars:= true

