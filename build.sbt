
lazy val commonSettings = Seq(
  organization := "xyz.nowhere",
  scalaVersion := "2.13.3"
)

lazy val rootProject = (project in file("."))
  .settings(commonSettings: _*)
  .settings(publishArtifact := false, name := "graalvm-archivewrapper")
  .aggregate(archivewrapper)


lazy val archivewrapper: Project = (project in file("archivewrapper"))
  .settings(commonSettings: _*)
  .enablePlugins(GraalVMNativeImagePlugin)
  .settings(
    name := "archivewrapper",
   libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-compress" % "1.20",
      "commons-io" % "commons-io" % "2.8.0",
      "com.lihaoyi" % "ammonite" % "2.3.8" % "test" cross CrossVersion.full,
      "org.scalameta" %% "munit" % "0.7.19" % "test"
    ),
    testFrameworks += new TestFramework("munit.Framework"),
      sourceGenerators in Test += Def.task {
      val file = (sourceManaged in Test).value / "amm.scala"
      IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
      Seq(file)
    }.taskValue
  )
  



// Optional, required for the `source` command to work
(fullClasspath in Test) ++= {
  (updateClassifiers in Test).value
    .configurations
    .find(_.configuration.name == Test.name)
    .get
    .modules
    .flatMap(_.artifacts)
    .collect{case (a, f) if a.classifier == Some("sources") => f}
}
