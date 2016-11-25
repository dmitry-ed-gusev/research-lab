__author__ = 'alex'
import users
from WebAuthTest import WebAuthTest
from alexandritTestCases.ApacheAuthTests.decorators import TestFunction
from testDescriptions import TestDescriptions
from alexandritTestCases.LogTests.LogChecker import LogChecker
from alexandritTestCases.LogTests.CheckFunctions import checkSuccessfulLogin, checkUnSuccessfulLogin


class WebAuthCommonTest(object):
    def __init__(self, authorizer, webUser, expectedResult, description, logOutDescription=None,
                 logTestDescription=None, unsuccessfulLogTestDescription=None):
        authTest = WebAuthTest(authorizer=authorizer, webUser=webUser)
        testFunction = TestFunction(description=description)(lambda: authTest.tryAuth() == expectedResult)
        hasLogTest = logTestDescription or unsuccessfulLogTestDescription
        eventLogTestDescription = (
            logTestDescription if logTestDescription else unsuccessfulLogTestDescription) if hasLogTest else None
        testFunctionEvent = (
            checkSuccessfulLogin(webUser.getName()) if logTestDescription
            else checkUnSuccessfulLogin(webUser.getName(), webUser.getPassword())) if hasLogTest else None
        self.testFunction = LogChecker(description=eventLogTestDescription,
                                       checkFunction=testFunctionEvent)(
            testFunction) if hasLogTest else testFunction

        self.logoutFunction = TestFunction(description=logOutDescription)(
            lambda: authTest.tryLogout()) if expectedResult else None

    def test(self):
        self.testFunction() and (self.logoutFunction() if self.logoutFunction else True)

    @staticmethod
    def getAllWebAuthTests(adminAuthorizer, operatorAuthorizer, userAuthorizer):
        tests = {
            "admin": (adminAuthorizer, {
                "adminWithWebAdmin": {
                    "webUser": users.WebAdmin,
                    "expectedResult": True,
                    "description": TestDescriptions["testWebAdminAuthorizationWithAdmin"],
                    "logOutDescription": TestDescriptions["testWebAdminLogout"],
                    "logTestDescription": TestDescriptions["testLogEventAdminAuth"]
                },
                "adminWithWebOperator": {
                    "webUser": users.WebOperator,
                    "expectedResult": False,
                    "description": TestDescriptions["testWebOperatorAuthorizationWithAdmin"],
                    "unsuccessfulLogOutDescription" : TestDescriptions["testLogEventAdminAuthFail"]
                },
                "adminWithWebUser": {
                    "webUser": users.WebUser,
                    "expectedResult": False,
                    "description": TestDescriptions["testWebOperatorAuthorizationWithAdmin"]
                }
            }),
            "operator": (
                operatorAuthorizer, {
                    "operatorWithWebAdmin": {
                        "webUser": users.WebAdmin,
                        "expectedResult": False,
                        "description": TestDescriptions["testWebAdminAuthorizationWithOperator"],
                        "unsuccessfulLogOutDescription": TestDescriptions["testLogEventOperatorAuthFail"]
                    },
                    "operatorWithWebOperator": {
                        "webUser": users.WebOperator,
                        "expectedResult": True,
                        "description": TestDescriptions["testWebOperatorAuthorizationWithOperator"],
                        "logOutDescription": TestDescriptions["testWebOperatorLogout"],
                        "logTestDescription": TestDescriptions["testLogEventOperatorAuth"]
                    },
                    "operatorWithWebUser": {
                        "webUser": users.WebUser,
                        "expectedResult": False,
                        "description": TestDescriptions["testWebUserAuthorizationWithOperator"]
                    }
                }
            )
        }
        res = reduce(lambda accum, (authorizer, testGroup): accum + map(
            lambda test: WebAuthCommonTest(authorizer, test["webUser"], test["expectedResult"],
                                           test["description"],
                                           logOutDescription=(
                                               test["logOutDescription"] if test["expectedResult"] else None),
                                           logTestDescription=(
                                               test["logOutDescription"] if test.has_key(
                                                   "logOutDescription") else None),
                                           unsuccessfulLogTestDescription=(test["unsuccessfulLogOutDescription"] if test.has_key(
                                                   "unsuccessfulLogOutDescription") else None)), testGroup.values()),
                     tests.values(),
            [])
        return res