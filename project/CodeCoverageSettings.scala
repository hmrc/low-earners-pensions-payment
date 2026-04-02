import scoverage.ScoverageKeys

object CodeCoverageSettings {

  def apply() = Seq( // Semicolon-separated list of regexes matching classes to exclude
    ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;.*(config|views.*);.*(AuthService|BuildInfo|Routes).*;app.*",
    ScoverageKeys.coverageExcludedFiles := Seq(
      "" +
        "<empty>",
      "Reverse.*",
      "uk.gov.hmrc.BuildInfo",
      "app.*",
      "prod.*",
      "testOnly.*",
      "testOnlyDoNotUseInAppConf.*",
      ".*BuildInfo.*",
      ".*javascript.*",
      ".*Routes.*",
      ".*Test.*"
    ).mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 95,
    ScoverageKeys.coverageMinimumBranchTotal := 90,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}
