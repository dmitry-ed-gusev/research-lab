#!/usr/bin/env python
# coding=utf-8

"""
    Unit tests for jira_helpers script (procedures).

    Created:  Dmitrii Gusev, 12.01.2019
    Modified: Gusev Dmitrii, 15.02.2019
"""

import unittest
from scripts.atlassian.jira_helpers import is_key_empty, get_issue_key

EMPTY_KEYS_LIST = ['', '      ', None, '-', '   -', '-  ', '  -    ', '???', '  ???', '???    ', '  ???     ']


class JiraHelpersTest(unittest.TestCase):

    def setUp(self):
        print("JiraHelpersTest.setUp()")

    def tearDown(self):
        print("JiraHelpersTest.tearDown()")

    @classmethod
    def setUpClass(cls):
        print("JiraHelpersTest.setUpClass()")

    @classmethod
    def tearDownClass(cls):
        print("JiraHelpersTest.tearDownClass()")

    def test_is_key_empty_empty_keys(self):
        for key in EMPTY_KEYS_LIST:
            self.assertTrue(is_key_empty(key))

    def test_is_key_empty_non_empty_keys(self):
        self.assertFalse(is_key_empty('KDM-111'))

    def test_get_issue_key_all_empty(self):
        for epic_key in EMPTY_KEYS_LIST:
            for story_key in EMPTY_KEYS_LIST:
                for task_key in EMPTY_KEYS_LIST:
                    self.assertIsNone(get_issue_key(epic_key, story_key, task_key))

    def test_get_issue_key_more_one_provided(self):
        keys_values = [('  epic', 'story    ', ''), ('epic', '  ', ' task  '), (' ', '  story', '    task')]
        for value in keys_values:
            with self.assertRaises(ValueError):
                get_issue_key(*value)

    def test_get_issue_key_right_key(self):
        keys_values = [('  key', '   ', ''), ('', ' key  ', '  '), (' ', '', '    key')]
        for value in keys_values:
            self.assertEqual('key', get_issue_key(*value))
