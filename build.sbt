import uk.gov.hmrc.DefaultBuildSettings

inThisBuild(
  List(
    scalaVersion := "3.3.7",
    majorVersion := 0
  )
)

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
  .settings(CodeCoverageSettings())

lazy val commonScalacOptions = Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Werror",
  "-Wconf:msg=unused&src=routes/.*:s",
  "-language:noAutoTupling",
  "-Wvalue-discard",
  "-Xfatal-warnings",
  "-Wconf:msg=Flag.*repeatedly:s"
)

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

addCommandAlias("runAllTests", "; clean ; coverage ; test ; it/test ; coverageReport ;")
