#!/usr/bin/env python
# coding=utf-8

"""

    Unit tests for pylog module.

    Created:  Dmitrii Gusev, 15.04.2019
    Modified:
"""

import unittest


class PylogTest(unittest.TestCase):

    def setUp(self):
        print("PylogTest.setUp()")

    def tearDown(self):
        print("PylogTest.tearDown()")

    @classmethod
    def setUpClass(cls):
        print("PylogTest.setUpClass()")

    @classmethod
    def tearDownClass(cls):
        print("PylogTest.tearDownClass()")

    def some_test(self):
        pass
