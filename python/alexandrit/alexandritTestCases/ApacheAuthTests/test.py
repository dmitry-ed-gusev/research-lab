# coding=utf-8
__author__ = 'alex'

# class TestAdminAuthTests(unittest.TestCase):
#     def setUp(self):
#         self.test = AdminAuthTest()
#         httpretty.enable()
#         self.index_php = "http://" + hostSettings.getHost() + ":" + str(hostSettings.getPort()) + "/index.php"
#
#     def testUnAuthorizedAccess(self):
#         httpretty.reset()
#         httpretty.register_uri(httpretty.GET, self.index_php, status=401)
#         self.assertTrue(self.test.testUnAuthorizedAccess())
#         httpretty.reset()
#         httpretty.register_uri(httpretty.GET, self.index_php, status=200)
#         with self.assertRaises(TestException):
#             self.assertTrue(self.test.testUnAuthorizedAccess())
#         httpretty.reset()
#     def testAllTests(self):
#         httpretty.reset()
#         def authCallback(request, uri, headers):
#             credentials = users.Admin.getCredentials()
#             try:
#                 authorization = request.headers["authorization"]
#                 credential_string = "Basic " + base64.b64encode(
#                     credentials["name"] + ":" + credentials["password"])
#                 status = 200 if credential_string == authorization else 401
#             except KeyError:
#                 status = 401
#             return status, headers, ""
#         self.sessid="testestset"
#         httpretty.register_uri(httpretty.GET, self.index_php, body=authCallback, adding_headers={
#             "Set-Cookie": "PHPSESSID="+self.sessid+"; path=/"
#         })
#         self.test.test()
#
#     def tearDown(self):
#         httpretty.disable()
#
#
# if __name__ == '__main__':
#     unittest.main()
