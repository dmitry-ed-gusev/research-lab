__author__ = 'alex'

import unittest
from alexandritTestCases.WebAuthTests.WebAuthTest import WebAuthTest
from users import WebAdmin, FakeWebAdmin
import httpretty
from httpProvider import HTTPProvider
import hostSettings
import Cookie
from urlparse import parse_qs
from DataBase import DataBase
from DataBase import test


class Authorizer(object):
    def __init__(self):
        self.authorization = "qwerty"
        self.sessid = "test"
        self.provider = HTTPProvider(hostSettings.getHost(), hostSettings.getPort())


class MyTestCase(unittest.TestCase):
    def setUp(self):
        httpretty.enable()
        self.authorizer = Authorizer()
        self.login_php = "http://" + hostSettings.getHost() + ":" + str(hostSettings.getPort()) + "/php/loginform.php"
        cursor = mock_cursor()
        self.mock_psycopg2 = mock_psycopg2(cursor)
        DataBase.dbEngine = self.mock_psycopg2

    def test_something(self):
        def authCallback(request, uri, headers):
            status = 200
            body = ""
            cookie = Cookie.SimpleCookie()
            cookie.load(request.headers["cookie"])
            post_parsed = parse_qs(request.body)
            login = post_parsed['login'][0]
            password = post_parsed['pass'][0]
            sessid = cookie["PHPSESSID"].value

            if request.headers["authorization"] == self.authorizer.authorization \
                    and sessid == self.authorizer.sessid \
                    and login == WebAdmin.getName() \
                    and password == WebAdmin.getPassword():
                body += "SUCCESS!!!"
                self.mock_psycopg2.cursor = mock_cursor(sessid)
            return status, headers, body

        httpretty.register_uri(httpretty.POST, self.login_php, body=authCallback)
        test = WebAuthTest(self.authorizer, webUser=WebAdmin)
        self.assertTrue(test.tryAuth())
        self.mock_psycopg2.cursor = mock_cursor()
        test = WebAuthTest(self.authorizer, webUser=FakeWebAdmin)
        self.assertFalse(test.tryAuth())

    @unittest.skipIf(not test.TEST_REAL_BASE, "Real database authorization test skipped because TEST_REAL_BASE is false")
    def testOnRealBase(self):
        import psycopg2
        from authorization.HTTPAuthorizationFactory import HTTPAuthorizationFactory
        httpretty.disable()
        DataBase.dbEngine = psycopg2
        authorizer = HTTPAuthorizationFactory.getAuthorizationAdminObject()
        self.assertTrue(authorizer.tryAuth())
        test = WebAuthTest(authorizer, webUser=WebAdmin)
        self.assertTrue(test.tryAuth())
        self.assertTrue(test.checkUserSession())
        test.cleanAuthInfo()
        self.assertFalse(test.checkUserSession())

    def tearDown(self):
        httpretty.disable()


class mock_connection:
    def __init__(self, c):
        self.c = c

    def cursor(self):
        return self.c

    def commit(self):
        pass

    def close(self):
        pass


class mock_psycopg2:
    def __init__(self, cursor):
        self.cursor = cursor

    def connect(self, *args, **kwargs):
        return mock_connection(self.cursor)


class mock_cursor:
    def __init__(self,testSessid=None):
        self.testSessid = testSessid
        self.aueryStrings = {
            "SELECT 1 AS result FROM web_users WHERE login=%s AND session_id=%s": {
                "params": [
                    ((WebAdmin.getName(), self.testSessid), [1,2,3])
                ]
            }
        }
        self.result = []
    def execute(self, queryString, queryParams=()):
        try:
            params = self.aueryStrings[queryString]["params"]
            for par, result in params:
                if par == queryParams:
                    self.result = result
        except KeyError:
            self.result = []

    def fetchall(self):
        return self.result

    def close(self):
        pass


if __name__ == '__main__':
    unittest.main()
