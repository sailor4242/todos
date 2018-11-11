package com.dzavorin.todo

import org.scalacheck.{Prop, Test}
import org.scalacheck.util.Pretty
import utest._

trait UTestScalaCheck {

  protected[this] object UTestReporter extends Test.TestCallback {

    override def onTestResult(name: String, res: org.scalacheck.Test.Result) = {
      val scalaCheckResult = if (res.passed) "" else Pretty.pretty(res, Pretty.defaultParams)
      assert(scalaCheckResult.isEmpty)
    }
  }

  implicit protected[this] class PropWrapper(prop: Prop) {
    def checkUTest(): Unit = {
      prop.check(Test.Parameters.default
        .withMinSuccessfulTests(10)
        .withTestCallback(UTestReporter))
    }
  }

}
