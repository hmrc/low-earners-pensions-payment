package utils

import base.SpecBase

class OptionUtilsSpec extends SpecBase {
  "optFromSeq" - {
    "should return None for an empty list" in {
      OptionUtils.optFromSeq(Nil) mustBe None
    }

    "should return a list wrapped in an option for a non-empty list" in {
      OptionUtils.optFromSeq(Seq(1, 3, 5)) mustBe Some(Seq(1, 3, 5))
    }
  }

}
