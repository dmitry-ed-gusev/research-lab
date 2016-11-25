__author__ = 'alex'
from httpProvider import HTTPProvider
from alexandritTestCases.WebAuthTests.WebAuthorizer import WebAuthorizer
import hostSettings
import base64
import Cookie


class Authorizer(object):
    def __init__(self, user):
        self.user = user
        self.credentials = self.user.getCredentials()
        self.provider = HTTPProvider(hostSettings.getHost(), hostSettings.getPort())
        self.sessid = None
        self.authorization = "Basic " + base64.b64encode(
                self.credentials["name"] + ":" + self.credentials["password"])

    def tryGet(self):
        response = self.provider.get("/index.php")
        return response

    def tryAuth(self):
        response = self.provider.get("/index.php", headers={"Authorization"
                                                            : self.authorization})
        result = response.status == 200
        if result:
            cookie = Cookie.SimpleCookie()
            cookie.load(response.getheader("Set-Cookie"))
            self.sessid = cookie["PHPSESSID"].value
        return result

    def setWebAuthorizer(self, webUser):
        self.webAuthorizer = WebAuthorizer(self, webUser)

    def resetSessid(self):
        if self.sessid:
            self.sessid = None

    def resetAuthorization(self):
        self.resetSessid()
        self.tryAuth()