import AppDependencies.playVersion
import sbt.*

object AppDependencies {

  private val bootstrapVersion = "10.8.0"
  private val playVersion = "play-30"
  private val hmrcMongoVersion = "2.14.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                   %% "bootstrap-backend-play-30"  % bootstrapVersion,
    "uk.gov.hmrc.mongo"             %% "hmrc-mongo-work-item-repo-play-30" % hmrcMongoVersion,
    "org.typelevel"                 %% "cats-core"                  % "2.13.0",
    "com.fasterxml.jackson.module"  %% "jackson-module-scala"       % "2.22.2",
    "uk.gov.hmrc"                   %% s"sca-wrapper-$playVersion"  % "6.3.0",
    "com.softwaremill.retry"        %% "retry"                      % "0.3.6",
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-test-play-30"      % bootstrapVersion,
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-test-play-30"     % hmrcMongoVersion
  ).map(_ % Test)
}
