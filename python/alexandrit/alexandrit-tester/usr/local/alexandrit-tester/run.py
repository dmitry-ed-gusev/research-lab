# coding=utf-8
from alexandritTestCases.TestCreator import TestCreator
__author__ = 'alex'
import traceback
from DataBase.DataBase import DataBase
import testDescriptions
class TestResult(object):
    def __init__(self, description, result):
        self.result = result
        self.description = description

class AllTestPassedResult(TestResult):
    def __init__(self):
        super(AllTestPassedResult, self).__init__(testDescriptions.AllTestPassedSuccessfulMessage, True)

def runTests():
    try:
        for test in TestCreator.createAllTests():
            test.test()
        logResult(AllTestPassedResult())
    except TestException, exc:
        logResult(TestResult(exc.message, False))
    except Exception, exc:
        print "System error", exc, type(exc)
        traceback.print_exc()
    finally:
        with DataBase() as db:
            pass
            db.cleanAllAuthEvents()

def logResult(res):
    with DataBase() as db:
        db.logTestResult(res)

def runTest(test):
    try:
        return test.test()
    except TestException, exc:
        return exc


if __name__ == "__main__":
    from alexandritTestCases.ApacheAuthTests.decorators import TestException
    runTests()