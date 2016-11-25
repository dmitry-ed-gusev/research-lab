__author__ = 'alex'

import unittest
import authorization.testsMocks as mocks
from authorization.HTTPAuthorizationFactory import HTTPAuthorizationFactory
from authorization.Authorizer import Authorizer
import httpretty
import hostSettings
import base64
import users
class TestAuth(unittest.TestCase):
    def setUp(self):
        self.user = mocks.user
        httpretty.enable()
        self.obj = HTTPAuthorizationFactory.getAuthorizationObject(self.user)
        self.index_php = "http://" + hostSettings.getHost() + ":" + str(hostSettings.getPort()) + "/index.php"
        self.sessid = "oslfnto9so20etiutf14viocs5"

    def testGetAdminUser(self):
        obj = HTTPAuthorizationFactory.getAuthorizationAdminObject()
        self.assertEqual(obj.credentials, users.Admin.getCredentials())

    def testGetAuthorizationObject(self):
        self.assertIsInstance(self.obj, Authorizer)
        self.assertEqual(self.obj.user, self.user)

    def testTryGet(self):
        httpretty.reset()
        httpretty.register_uri(httpretty.GET, self.index_php, status=401)
        self.assertEqual(self.obj.tryGet().status, 401)

    def testTryAuth(self):
        httpretty.reset()
        def authCallback(request, uri, headers):
            credentials = self.user.getCredentials()
            authorization = request.headers["authorization"]
            credential_string = "Basic " + base64.b64encode(
                credentials["name"] + ":" + credentials["password"])
            status = 200 if credential_string == authorization else 401
            return status, headers, ""

        httpretty.register_uri(httpretty.GET, self.index_php, body=authCallback, adding_headers={
            "Set-Cookie": "PHPSESSID="+self.sessid+"; path=/"
        })
        self.assertTrue(self.obj.tryAuth())
        self.assertEqual(self.obj.sessid, self.sessid)
        self.assertFalse(HTTPAuthorizationFactory.getAuthorizationFakeAdminObject().tryAuth())




    def tearDown(self):
        httpretty.disable()


if __name__ == '__main__':
    unittest.main()
