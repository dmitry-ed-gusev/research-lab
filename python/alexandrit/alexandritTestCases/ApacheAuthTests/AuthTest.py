__author__ = 'alex'
class AuthTest(object):
    def __init__(self, user, fakeUser):
        self.fakeUser = fakeUser
        self.user = user

    def testSuccessfulAuthorization(self):
        result = self.user.tryAuth()
        return result

    def testUnsuccessfulAuthorization(self):
        return not self.fakeUser.tryAuth()