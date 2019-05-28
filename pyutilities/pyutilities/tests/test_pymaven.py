#!/usr/bin/env python
# coding=utf-8

"""

    Unit tests for pygit module/PyMaven class.

    Created:  Dmitrii Gusev, 28.05.2019
    Modified:

"""

import unittest
from pyutilities.tests.pyutils_test_helper import get_test_logger
from pyutilities.pymaven import PyMaven


class PyMavenTest(unittest.TestCase):

    def setUp(self):
        self.log.debug('setUp() is working.')
        self.pymaven = PyMaven()

    def tearDown(self):
        self.log.debug('tearDown() is working.')

    @classmethod
    def setUpClass(cls):
        cls.log = get_test_logger(__name__)
        cls.log.debug('setUpClass() is working.')

    @classmethod
    def tearDownClass(cls):
        cls.log.debug('tearDownClass() is working.')

    def test(self):
        pass
