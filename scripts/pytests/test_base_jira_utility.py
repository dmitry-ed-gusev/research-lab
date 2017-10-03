#!/usr/bin/env python
# coding=utf-8

# todo: implement tests!
# todo: check error messages for tests with expected failure
# todo: add some positive scenarios unit tests
# todo: add tests for get_issues_by_jql()

import unittest
from pylib.jira_base_utility import BaseJiraUtility, JiraException

# empty values tuple
EMPTY_VALUES = (None, '', '     ')


class BaseJIRAUtilityTest(unittest.TestCase):

    def setUp(self):
        print "BaseJIRAUtilityTest tests: setUp()"
        self.jira = BaseJiraUtility(None, 'jira_address', 'jira_user', 'jira_password')

    def test_InitNoneConfigParam(self):
        with self.assertRaises(JiraException):
            BaseJiraUtility(None)

    def test_InitInvalidConfigParam(self):
        with self.assertRaises(JiraException):
            BaseJiraUtility(object)

    def test_InitInvalidJiraParams1(self):
        with self.assertRaises(JiraException):
            BaseJiraUtility(None, 'address')

    def test_InitInvalidJiraParams2(self):
        with self.assertRaises(JiraException):
            BaseJiraUtility(None, 'address', 'user')

    def test_InitInvalidJiraParams3(self):
        for empty_value in EMPTY_VALUES:
            with self.assertRaises(JiraException):
                BaseJiraUtility(None, empty_value, 'user', 'password')

    def test_InitInvalidJiraParams4(self):
        for empty_value in EMPTY_VALUES:
            with self.assertRaises(JiraException):
                BaseJiraUtility(None, 'address', empty_value, 'password')


    def test_InitWithStringPath(self):
        print "implement it!"

    def test_InitWithConfigurationObject(self):
        print "implement it!"

    def test_InitWithJiraParameters(self):
        print "implement it!"

    def test_GetEmptyProjectKey(self):
        for empty_value in EMPTY_VALUES:
            with self.assertRaises(JiraException):
                self.jira.get_project_key(empty_value)
