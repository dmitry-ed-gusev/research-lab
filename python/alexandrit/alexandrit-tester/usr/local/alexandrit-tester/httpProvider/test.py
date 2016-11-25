__author__ = 'alex'

import unittest
import httplib

import httpProvider
import httpretty

class MyTestCase(unittest.TestCase):
    def setUp(self):
        httpretty.enable()
        self.host = "httpretty"
        self.port = 80
        self.provider = httpProvider.HTTPProvider(self.host, self.port)

    def tearDown(self):
        httpretty.disable()



    def testSimpleGetResponse(self):
        httpretty.register_uri(httpretty.GET, "http://httpretty/")
        response = self.provider.get("/")
        self.assertEqual(response.status, 200)


if __name__ == '__main__':
    unittest.main()
