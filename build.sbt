import uk.gov.hmrc.DefaultBuildSettings

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.3.6"
ThisBuild / scalacOptions += "-Wconf:msg=Flag.*repeatedly:s"

lazy val microservice = Project("low-earners-pensions-payment", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalafmtOnCompile := true,
    PlayKeys.playDefaultPort := 7504
  )
  .settings(scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-Wconf:msg=unused import&src=conf/.*:s",
    "-Wconf:msg=Flag.*repeatedly:s",
    "-Wconf:src=routes/.*:s")
  )
  .settings(CodeCoverageSettings.settings *)

lazy val it = project
  .in(file("it"))
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings())
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    Test / fork := true,
    Test / scalafmtOnCompile := true,
    Test / unmanagedResourceDirectories += baseDirectory.value / "it" / "test" / "resources"
  )

addCommandAlias("testc", "; clean ; coverage ; test ; it/test ; coverageReport ;")
