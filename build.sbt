
lazy val commonSettings = Seq(
  organization := "xyz.nowhere",
  scalaVersion := "2.13.3"
)

lazy val rootProject = (project in file("."))
  .settings(commonSettings: _*)
  .settings(publishArtifact := false, name := "graalvm-archievewrapper")
  .aggregate(archievewrapper)


lazy val archievewrapper: Project = (project in file("archievewrapper"))
  .settings(commonSettings: _*)
  .enablePlugins(GraalVMNativeImagePlugin)
  .settings(
    name := "archievewrapper",
   libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-compress" % "1.20"
    )
  )
