__author__ = 'alex'
from DataBase.DataBase import DataBase
from WebAuthorizer import WebAuthorizer



class WebAuthTest(object):
    def __init__(self, authorizer, webUser):
        self.authorizer = authorizer
        self.authorizer.setWebAuthorizer(webUser)
        self.webAuthorizer = self.authorizer.webAuthorizer
        self.webUser = webUser



    def tryAuth(self):
        self.webAuthorizer.login()
        return self.checkUserSession()

    def tryLogout(self):
        self.webAuthorizer.logout()
        return not self.checkUserSession()

    def checkUserSession(self):
        with DataBase() as db:
            res = db.getUserSession(self.webUser.getName(), self.authorizer.sessid)
            return len(res) != 0

    def cleanAuthInfo(self):
        with DataBase() as db:
            db.cleanUserSession(self.webUser.getName())








