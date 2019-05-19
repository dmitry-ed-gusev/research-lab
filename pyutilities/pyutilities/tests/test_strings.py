#!/usr/bin/env python
# coding=utf-8

"""

    Unit tests for strings module.

    Created:  Dmitrii Gusev, 15.04.2019
    Modified: Dmitrii Gusev, 26.04.2019

"""

import unittest
import pyutilities.strings as pystr
from pyutilities.tests.pyutils_test_constants import TEST_LOGGING_CONFIG

# common constants for testing
EMPTY_STRINGS = ['', '     ', None, "", "  "]
NON_EMPTY_STRINGS = {'str1': '   str1',
                     'str2': 'str2    ',
                     'str3': '   str3     ',
                     'str4': 'str4'}


class StringsTest(unittest.TestCase):

    def setUp(self):
        print("StringsTest.setUp()")

    def tearDown(self):
        print("StringsTest.tearDown()")

    @classmethod
    def setUpClass(cls):
        print("StringsTest.setUpClass()")

    @classmethod
    def tearDownClass(cls):
        print("StringsTest.tearDownClass()")

    def test_is_str_empty_with_empty_strings(self):
        for s in EMPTY_STRINGS:
            self.assertTrue(pystr.is_str_empty(s), "Must be True!")

    def test_is_str_empty_with_non_empty_strings(self):
        for k, v in NON_EMPTY_STRINGS.items():
            self.assertFalse(pystr.is_str_empty(k), "Must be False!")
            self.assertFalse(pystr.is_str_empty(v), "Must be False!")

    def test_trim_to_none_with_empty_strings(self):
        for s in EMPTY_STRINGS:
            self.assertIsNone(pystr.trim_to_none(s), "Must be NoNe!")

    def test_trim_to_none_with_non_empty_strings(self):
        for k, v in NON_EMPTY_STRINGS.items():
            self.assertEqual(k, pystr.trim_to_none(v), "Must be equals!")

    def test_trim_to_empty_with_empty_strings(self):
        for s in EMPTY_STRINGS:
            self.assertEqual('', pystr.trim_to_empty(s), "Must be an empty string!")
            self.assertEqual("", pystr.trim_to_empty(s), "Must be an empty string!")

    def test_trim_to_empty_with_non_empty_strings(self):
        for k, v in NON_EMPTY_STRINGS.items():
            self.assertEqual(k, pystr.trim_to_empty(v), "Must be equals!")
