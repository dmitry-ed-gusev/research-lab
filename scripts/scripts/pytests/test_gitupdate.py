#!/usr/bin/env python
# coding=utf-8

"""
    Unit tests for gitupdate module.

    Created:  Dmitrii Gusev, 30.09.2018
    Modified: Dmitrii Gusev, 25.05.2019
"""

import unittest
from mock import patch
from scripts.pytests.scripts_test_helper import get_test_logger
from scripts.atlassian.gitupdate import build_repos_list, select_projects_location
from scripts.atlassian.atlassian_exception import AtlassianException

# empty dictionaries list
empty_dict_list = [None, {}, '']
locations = {'win': 'win_location', 'linux': 'linux_location', 'macos': 'macos_location'}


class GitUtilityTest(unittest.TestCase):

    def setUp(self):
        self.log.debug('setUp() is working.')

    def tearDown(self):
        self.log.debug('tearDown() is working.')

    @classmethod
    def setUpClass(cls):
        cls.log = get_test_logger(__name__)
        cls.log.debug('setUpClass() is working.')

    @classmethod
    def tearDownClass(cls):
        cls.log.debug('tearDownClass() is working.')

    def test_build_repos_list_empty_dict(self):
        for dictionary in empty_dict_list:
            with self.assertRaises(AtlassianException):
                build_repos_list(dictionary)

    def test_build_repos_list(self):
        # sample data
        repos_dict = {'repo_type1': {'repo1': None, 'repo2': None, 'repo3': None}, 'repo_type2': {'repo4': {'build': True}}}
        # expected result
        expected = ['repo_type1/repo1', 'repo_type1/repo2', 'repo_type1/repo3', 'repo_type2/repo4']
        # actual testing
        self.assertListEqual(expected, build_repos_list(repos_dict))

    # def test_select_projects_location_empty_dict(self):
    #     for dictionary in empty_dict_list:
    #         with self.assertRaises(AtlassianException):
    #             select_projects_location(dictionary)

    @patch('scripts.atlassian.gitupdate.platform')
    def test_select_projects_location_win(self, mock_platform):
        mock_platform.system.return_value = 'windows'
        self.assertEqual('win_location', select_projects_location(locations))

    @patch('scripts.atlassian.gitupdate.platform')
    def test_select_projects_location_linux(self, mock_platform):
        mock_platform.system.return_value = 'linux'
        self.assertEqual('linux_location', select_projects_location(locations))

    @patch('scripts.atlassian.gitupdate.platform')
    def test_select_projects_location_macos(self, mock_platform):
        mock_platform.system.return_value = 'macos'
        self.assertEqual('macos_location', select_projects_location(locations))
