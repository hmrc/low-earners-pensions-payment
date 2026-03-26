import AppDependencies.playVersion
import sbt.*

object AppDependencies {

  private val bootstrapVersion = "10.4.0"
  private val playVersion = "play-30"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                   %% "bootstrap-backend-play-30"  % bootstrapVersion,
    "org.typelevel"                 %% "cats-core"                  % "2.13.0",
    "com.fasterxml.jackson.module"  %% "jackson-module-scala"       % "2.20.0",
    "uk.gov.hmrc"                   %% s"sca-wrapper-$playVersion"  % "4.12.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-test-play-30"      % bootstrapVersion,
  ).map(_ % Test)
}
