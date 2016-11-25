__author__ = 'alex'
from urllib import urlencode
class WebAuthorizer(object):
    def __init__(self, authorizer, webUser):
        self.webUser = webUser
        self.authorizer = authorizer
        self.headers = {
            "Authorization": authorizer.authorization,
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
        }
        self.credentialString = urlencode({'login': webUser.getName(), 'pass': webUser.getPassword(), 'flag': '1'})

    def login(self):
        self.headers["Cookie"] = "PHPSESSID=" + self.authorizer.sessid
        self.authorizer.provider.post("/php/loginform.php", self.credentialString, self.headers)

    def logout(self):
        self.headers["Cookie"] = "PHPSESSID=" + self.authorizer.sessid
        self.authorizer.provider.post("/php/exit.php", self.credentialString, self.headers)

    def get(self, path):
        self.headers["Cookie"] = "PHPSESSID=" + self.authorizer.sessid
        return self.authorizer.provider.get(path, self.headers)

    def post(self, path, params):
        self.headers["Cookie"] = "PHPSESSID=" + self.authorizer.sessid
        return self.authorizer.provider.post(path, urlencode(params), self.headers)
