__author__ = 'alex'

import unittest
from DataBase import DataBase
TEST_REAL_BASE = True
class MyTestCase(unittest.TestCase):
    @unittest.skipIf(not TEST_REAL_BASE, "Real database connection skipped because TEST_REAL_BASE is false")
    def testRealBDConnection(self):
        db = DataBase()


if __name__ == '__main__':
    unittest.main()
