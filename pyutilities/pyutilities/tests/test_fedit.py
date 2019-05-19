#!/usr/bin/env python
# coding=utf-8

"""
    Unit tests for fedit utility.

    Created:  Gusev Dmitrii, 26.09.2018
    Modified: Dmitrii Gusev, 04.03.2019
"""

import yaml
import logging
import logging.config
import unittest
from pyutilities.tests.pyutils_test_constants import TEST_LOGGING_CONFIG


# todo: implement unit tests for fedit utility
class FEditTest(unittest.TestCase):

    def setUp(self):
        print("FEditTest.setUp()")

    def tearDown(self):
        print("FEditTest.tearDown()")

    @classmethod
    def setUpClass(cls):
        cls._log = logging.getLogger(__name__)
        with open(TEST_LOGGING_CONFIG, 'rt') as f:
            config = yaml.safe_load(f.read())
        logging.config.dictConfig(config)
        print("FEditTest.setUpClass()")

    @classmethod
    def tearDownClass(cls):
        print("FEditTest.tearDownClass()")

    def test(self):
        pass
