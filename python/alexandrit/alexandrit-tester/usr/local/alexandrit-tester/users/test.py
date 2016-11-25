__author__ = 'alex'

import unittest
import users
import types

class AdminTestCase(unittest.TestCase):
    def setUp(self):
        self.name = users.Admin.getName()
        self.password = users.Admin.getPassword()
    def testAdminName(self):
        self.assertEqual(self.name, "alexandrit")
    def testAdminPasswd(self):
        self.assertEqual(self.password, "12345678")
    def testGetCredentials(self):
        crd = users.Admin.getCredentials()
        self.assertIsInstance(crd, types.DictType)
        self.assertEqual(crd["password"], self.password)
        self.assertEqual(crd["name"], self.name)


if __name__ == '__main__':
    unittest.main()
