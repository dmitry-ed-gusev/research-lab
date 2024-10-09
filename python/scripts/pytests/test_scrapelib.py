"""

"""

import unittest


# todo: implement mocks for urllib and create tests
class WebScrapeLibTest(unittest.TestCase):

    EMPTY_LIST = [None, '', '    ']

    def setUp(self):
        print("WebScrapeLibTest.setUp()")

    def tearDown(self):
        print("WebScrapeLibTest.tearDown()")

    @classmethod
    def setUpClass(cls):
        print("WebScrapeLibTest.setUpClass()")

    @classmethod
    def tearDownClass(cls):
        print("WebScrapeLibTest.tearDownClass()")


if __name__ == '__main__':
    unittest.main()
