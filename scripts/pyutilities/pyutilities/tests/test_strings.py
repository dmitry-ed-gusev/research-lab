#!/usr/bin/env python
# coding=utf-8

"""

    Unit tests for strings module.

    Created:  Dmitrii Gusev, 15.04.2019
    Modified: Dmitrii Gusev, 18.04.2019

"""

import unittest
import pyutilities.strings as pystr

# common constants for testing
EMPTY_STRINGS = ['', '     ', None, "", "  "]
NON_EMPTY_STRINGS = []  # todo: maybe use a dict? 'aaa': '   aaa' etc...


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

    def test_is_str_empty(self):
        for s in EMPTY_STRINGS:
            self.assertTrue(pystr.is_str_empty(s))

    def test_trim_to_none_with_empty_strings(self):
        for s in EMPTY_STRINGS:
            self.assertIsNone(pystr.trim_to_none(s), "Must be NoNe!")

    def test_trim_to_none_with_non_empty_strings(self):
        pass

    def test_trim_to_empty_with_empty_strings(self):
        for s in EMPTY_STRINGS:
            self.assertEquals('', pystr.trim_to_empty(s), "Must be equals ('')!")
            self.assertEquals("", pystr.trim_to_empty(s), "Must be equals (\"\")!")

    def test_trim_to_empty_with_non_empty_strings(self):
        pass
