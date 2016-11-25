__author__ = 'alex'
from alexandritTestCases.ApacheAuthTests.decorators import TestFunction
class UnathorizedTest(object):
    def __init__(self, user):
        self.user = user

    def test(self):
        return self.testUnAuthorizedAccess()

    @TestFunction()
    def testUnAuthorizedAccess(self):
        return self.user.tryGet().status == 401