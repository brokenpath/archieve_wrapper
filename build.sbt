
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
      "org.apache.commons" % "commons-compress" % "1.20"
    )
  )
