__author__ = 'alex'
from testDescriptions import TestDescriptions
from alexandritTestCases.ApacheAuthTests.decorators import TestFunction
from SpecialTest import SpecialTest
from alexandritTestCases.LogTests.LogChecker import LogChecker
from alexandritTestCases.LogTests import CheckFunctions


class SpecialCommonTest(object):
    def __init__(self, test, authorizer):
        self.special_test = SpecialTest(authorizer)
        self.tests = []
        for testName, desc in test.items():
            function = TestFunction(desc["description"])(
                (lambda name, description:
                 lambda: self.special_test.__getattribute__(name)(description))(testName, desc["expectedStatus"]))
            if desc.has_key("checkNSD"):
                function = (lambda username, description: LogChecker(description, CheckFunctions.checkNSD(username))(function))(
                    authorizer.user.getName(), desc["checkNSD"]["description"])
            self.tests.append(function)


    def test(self):
        try:
            self.special_test.webAuthorizer.login()
            result = map(lambda t: t(), self.tests)
        finally:
            self.special_test.webAuthorizer.logout()
        return result

    @staticmethod
    def getTests(adminAuthorizer, operatorAuthorizer, userAuthorizer):
        desc = {
            "admin": (adminAuthorizer,
                      {
                          "testGet": {
                              "expectedStatus": 200,
                              "description": TestDescriptions["testWebAdminDBAccessRead"]
                          }
                          , "testSet": {
                          "expectedStatus": 200,
                          "description": TestDescriptions["testWebAdminDBAccessSet"]
                      }
                          , "testRemove": {
                          "expectedStatus": 200,
                          "description": TestDescriptions["testWebAdminDBAccessRemove"]
                      }
                      }),
            "operator": (operatorAuthorizer,
                         {
                             "testGet": {
                                 "expectedStatus": 200,
                                 "description": TestDescriptions["testWebOperatorDBAccessRead"]
                             }
                             , "testSet": {
                             "expectedStatus": 200,
                             "description": TestDescriptions["testWebOperatorDBAccessSet"]
                         }
                             , "testRemove": {
                             "expectedStatus": 500,
                             "description": TestDescriptions["testWebOperatorDBAccessRemove"],
                             "checkNSD": {
                                 "description": TestDescriptions["testLogRemoveEventOperator"]
                             }
                         }
                         }),
            "user": (userAuthorizer,
                     {
                         "testGet": {
                             "expectedStatus": 200,
                             "description": TestDescriptions["testWebUserDBAccessRead"]
                         }
                         , "testSet": {
                         "expectedStatus": 500,
                         "description": TestDescriptions["testWebUserDBAccessSet"],
                         "checkNSD": {
                             "description": TestDescriptions["testLogRemoveEventUser"]
                         }
                     }
                         , "testRemove": {
                         "expectedStatus": 500,
                         "description": TestDescriptions["testWebUserDBAccessRemove"]
                     }
                     })
        }
        result = reduce(lambda accum, (authorizer, tests): accum + [SpecialCommonTest(tests, authorizer)],
                        desc.values(), [])
        return result
