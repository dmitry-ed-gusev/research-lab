#!/usr/bin/env python
# coding=utf-8

"""
    Unit tests for jira_utility_base module.

    Created:  Dmitrii Gusev, 30.09.2018
    Modified: Dmitrii Gusev, 04.03.2019
"""

import unittest


class GitUtilityTest(unittest.TestCase):

    def setUp(self):
        print("GitUtilityTest.setUp()")

    def tearDown(self):
        print("GitUtilityTest.tearDown()")

    @classmethod
    def setUpClass(cls):
        print("GitUtilityTest.setUpClass()")

    @classmethod
    def tearDownClass(cls):
        print("GitUtilityTest.tearDownClass()")

    def test(self):
        print("TEST!")
