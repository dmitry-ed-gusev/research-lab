#!/usr/bin/env python
# coding=utf-8

# todo: implement tests!
# todo: check error messages for tests with expected failure
# todo: add some positive scenarios unit tests
# todo: add tests for get_issues_by_jql()

import unittest

from pylib.jira_base_utility import BaseJiraUtility, JiraException


INVALID_JIRA_PARAMS = [['address'], ['address', 'user'], ['111', 'user', 'pass']]
a1 = 'aaa'
a2 = ['ss', 'dd', 'ddd']


class BaseJIRAUtilityTest(unittest.TestCase):

    def setUp(self):
        print "BaseJIRAUtilityTest tests: setUp()"
        # self.jira = BaseJiraUtility(None, 'addrezz', 'uzzer')

    def test_InitNoneConfigParam(self):
        with self.assertRaises(JiraException):
            BaseJiraUtility(None)

    def test_InitInvalidConfigParam(self):
        with self.assertRaises(JiraException):
            BaseJiraUtility(object)

    # todo: write separate tests cases???
    def test_InitInvalidJiraParams(self):
        for jira_params in INVALID_JIRA_PARAMS:
            # print "->", a, u, p
            with self.assertRaises(JiraException):
                BaseJiraUtility(None, 'ss', 'dd', 'ddd')

    # @unittest.expectedFailure
    # def test_init_not_all_params(self):
    #     BaseJiraUtility('', '')
    #
    # @unittest.expectedFailure
    # def test_init_empty_address1(self):
    #     BaseJiraUtility('', 'user', 'pass')
    #
    # @unittest.expectedFailure
    # def test_init_empty_address2(self):
    #     BaseJiraUtility('      ', 'user', 'pass')
    #
    # @unittest.expectedFailure
    # def test_init_empty_user1(self):
    #     BaseJiraUtility('address', '', 'pass')
    #
    # @unittest.expectedFailure
    # def test_init_empty_user2(self):
    #     BaseJiraUtility('address', '    ', 'pass')
    #
    # @unittest.expectedFailure
    # def test_get_project_key_empty_key1(self):
    #     self.jira.get_project_key('')
    #
    # @unittest.expectedFailure
    # def test_get_project_key_empty_key2(self):
    #     self.jira.get_project_key('    ')
