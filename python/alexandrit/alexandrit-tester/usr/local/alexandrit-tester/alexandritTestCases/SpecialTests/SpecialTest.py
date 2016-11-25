__author__ = 'alex'

from randomword import randomword
class SpecialTest(object):
    SPECIAL_TEST_PATH = "/php/test_mode/test.php"

    def __init__(self, authorizer):
        self.webAuthorizer = authorizer.webAuthorizer
        self.authorizer = authorizer

    def testGet(self, expectedStatus):
        response = self.webAuthorizer.get(SpecialTest.SPECIAL_TEST_PATH)
        return response.status == expectedStatus


    def testSet(self, expectedStatus):
        response = self.webAuthorizer.post(SpecialTest.SPECIAL_TEST_PATH,
                                           {'action': 'set', 'var_name': randomword(10), 'var_vale': randomword(15)})
        return response.status == expectedStatus


    def testRemove(self, expectedStatus):
        response = self.webAuthorizer.post(SpecialTest.SPECIAL_TEST_PATH,
                                           {'action': 'remove', 'var_name': 'test'})
        return response.status == expectedStatus
