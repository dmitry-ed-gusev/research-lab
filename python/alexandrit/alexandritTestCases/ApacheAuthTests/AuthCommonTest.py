from alexandritTestCases.ApacheAuthTests.AuthTest import AuthTest

__author__ = 'alex'
from alexandritTestCases.ApacheAuthTests.decorators import TestFunction
from authorization.HTTPAuthorizationFactory import HTTPAuthorizationFactory
from testDescriptions import TestDescriptions
from alexandritTestCases.ApacheAuthTests.UnauthorizedTest import UnathorizedTest


class AuthCommonTest(AuthTest):
    def __init__(self, user, fakeUser, successfulTestDescription, unSuccessfulTestDescription):
        super(AuthCommonTest, self).__init__(user, fakeUser)
        self.unSuccessfulTestDescription = unSuccessfulTestDescription
        self.successfulTestDescription = successfulTestDescription
        self.testFunctions = [TestFunction(desc)(func) for func, desc in
                              [(self.testSuccessfulAuthorization, self.successfulTestDescription),
                               (self.testUnsuccessfulAuthorization, self.unSuccessfulTestDescription)]]

    def test(self):
        map(lambda f: f(), self.testFunctions)
        return True

    @staticmethod
    def getAuthTestsList(adminAuthorizer, operatorAuthorizer, userAuthorizer):
        users = {
            "admin": {
                "user": adminAuthorizer,
                "fake": HTTPAuthorizationFactory.getAuthorizationFakeAdminObject(),
                "successful": TestDescriptions["testSuccessfullAdminAuthorization"],
                "unsuccessful": TestDescriptions["testUnSuccessfullAdminAuthorization"]
            },
            "oper": {
                "user": operatorAuthorizer,
                "fake": HTTPAuthorizationFactory.getAuthorizationFakeOperatorObject(),
                "successful": TestDescriptions["testSuccessfullOperatorAuthorization"],
                "unsuccessful": TestDescriptions["testUnSuccessfullOperatorAuthorization"]
            },
            "user": {
                "user": userAuthorizer,
                "fake": HTTPAuthorizationFactory.getAuthorizationFakeUserObject(),
                "successful": TestDescriptions["testSuccessfullUserAuthorization"],
                "unsuccessful": TestDescriptions["testUnSuccessfullUserAuthorization"]
            }
        }
        tests = map(lambda desc: AuthCommonTest(user=desc["user"], fakeUser=desc["fake"],
                                                successfulTestDescription=desc["successful"],
                                                unSuccessfulTestDescription=desc["unsuccessful"]), users.values())
        tests.append(UnathorizedTest(user=HTTPAuthorizationFactory.getAuthorizationAdminObject()))
        return tests






