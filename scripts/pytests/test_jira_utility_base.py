#!/usr/bin/env python
# coding=utf-8

# todo: implement tests!
# todo: add some positive scenarios unit tests
# todo: add tests for get_issues_by_jql()

import unittest
from mock import patch
from jira import JIRA
from pyutilities.config import Configuration
from pylib.jira_utility_base import JiraUtilityBase, JiraException

# empty values tuple
EMPTY_VALUES = (None, '', '     ')
# invalid values tuple
INVALID_VALUES = EMPTY_VALUES + (object,)


class BaseJIRAUtilityTest(unittest.TestCase):

    def setUp(self):
        print "BaseJIRAUtilityTest tests: setUp()"
        self.config = Configuration()
        self.jira = JiraUtilityBase(self.config)

    # init - negative cases
    def test_InitInvalidConfigParam(self):
        for invalid_value in INVALID_VALUES:
            with self.assertRaises(JiraException):
                JiraUtilityBase(invalid_value)

    def test_ExecuteEmptyJql(self):
        for empty_value in EMPTY_VALUES:
            with self.assertRaises(JiraException):
                self.jira.execute_jql(empty_value)

    @patch.object(JiraUtilityBase, 'connect')
    def test_GetEmptyProjectKey(self, mock_connect):
        for empty_value in EMPTY_VALUES:
            with self.assertRaises(JiraException):
                self.jira.get_project_key(empty_value)

    def test_GetEmptyComponentName(self):
        for empty_value in EMPTY_VALUES:
            with self.assertRaises(JiraException):
                self.jira.get_component_by_name(empty_value, 'component')

        for empty_value in EMPTY_VALUES:
            with self.assertRaises(JiraException):
                self.jira.get_component_by_name('project', empty_value)

    def test_GetIssuesForEmptySprintName(self):
        for empty_value in EMPTY_VALUES:
            with self.assertRaises(JiraException):
                self.jira.get_all_sprint_issues(empty_value)

    @patch.object(Configuration, 'get')
    @patch.object(JIRA, '__init__')
    def test_ConnectNotConnectedYet(self, mock_jira, mock_config):
        mock_jira.return_value = None
        mock_config.return_value = 'some value'
        self.jira.connect()
        self.assertTrue(mock_jira.called)

    @patch.object(Configuration, 'get')
    @patch.object(JIRA, '__init__')
    @patch.object(JiraUtilityBase, 'jira')
    def test_ConnectAlreadyConnected(self, mock_internal_jira, mock_jira, mock_config):
        mock_jira.return_value = None
        mock_config.return_value = 'some value'
        self.jira.connect()
        self.assertFalse(mock_jira.called)

    # patch connect() method and .jira internal property of BaseJiraUtility class
    @patch.object(JiraUtilityBase, 'connect')
    @patch.object(JiraUtilityBase, 'jira')
    def test_IsConnectCalledExecuteJql(self, mock_jira, mock_connect):
        self.jira.execute_jql('test JQL query')
        self.assertTrue(mock_connect.called)

    @patch.object(JiraUtilityBase, 'connect')
    @patch.object(JiraUtilityBase, 'jira')
    def test_IsConnectCalledGetProjectKey(self, mock_jira, mock_connect):
        self.jira.get_project_key('test project name')
        self.assertTrue(mock_connect.called)

    @patch.object(JiraUtilityBase, 'connect')
    @patch.object(JiraUtilityBase, 'jira')
    def test_IsConnectCalledGetComponentByName(self, mock_jira, mock_connect):
        self.jira.get_component_by_name('test project name', 'test component name')
        self.assertTrue(mock_connect.called)
