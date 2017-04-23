#!/usr/bin/env python
# coding=utf-8

# todo: implement tests!
# todo: check error messages for tests with expected failure
# todo: add some positive scenarios unit tests

import unittest

from scripts.pylib.jiralib import JIRAUtility


class JIRAUtilityTest(unittest.TestCase):

    def setUp(self):
        print "JIRAUtilityTest tests: setUp()"
        self.jira = JIRAUtility('address', 'user', 'pass')

    @unittest.expectedFailure
    def test_init_no_params(self):
        JIRAUtility()

    @unittest.expectedFailure
    def test_init_not_all_params(self):
        JIRAUtility('', '')

    @unittest.expectedFailure
    def test_init_empty_address1(self):
        JIRAUtility('', 'user', 'pass')

    @unittest.expectedFailure
    def test_init_empty_address2(self):
        JIRAUtility('      ', 'user', 'pass')

    @unittest.expectedFailure
    def test_init_empty_user1(self):
        JIRAUtility('address', '', 'pass')

    @unittest.expectedFailure
    def test_init_empty_user2(self):
        JIRAUtility('address', '    ', 'pass')

    @unittest.expectedFailure
    def test_get_project_key_empty_key1(self):
        self.jira.get_project_key('')

    @unittest.expectedFailure
    def test_get_project_key_empty_key2(self):
        self.jira.get_project_key('    ')
