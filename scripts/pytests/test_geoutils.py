#!/usr/bin/env python
# coding=utf-8

"""
    Unit tests for geoutils module.
    Created: Gusev Dmitrii, 04.02.2017
    Modified:
"""

import unittest
from geopython.geoutils import filter_str


class GeoUtilsTests(unittest.TestCase):

    def test_filter_str_for_empty(self):
        for string in ['', '    ', None]:
            self.assertEquals(string, filter_str(string))

    def test_filter_str_for_string(self):
        self.assertEquals('45, .555', filter_str('+45, *@.555'))
        self.assertEquals('улица  Правды. 11,', filter_str('улица + =Правды. 11,'))
        self.assertEquals('3-5-7', filter_str('3-5-7'))
        self.assertEquals('zzzz. , fgh ', filter_str('zzzz. ??, fgh *'))
