#!/usr/bin/env python
# coding=utf-8

"""
    Unit tests for gitupdate module.

    Created:  Dmitrii Gusev, 30.09.2018
    Modified: Dmitrii Gusev, 27.05.2019
"""

import unittest
from mock import patch
from scripts.pytests.scripts_test_helper import get_test_logger
from scripts.atlassian.gitupdate import get_repos_list, get_projects_location, get_prepared_git_url, \
    get_project_folder
from scripts.atlassian.atlassian_exception import AtlassianException

# empty dictionaries list
empty_values_list = [None, {}, '', '   ']
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
        for dictionary in empty_values_list:
            with self.assertRaises(AtlassianException):
                get_repos_list(dictionary)

    def test_build_repos_list(self):
        # sample data
        repos_dict = {'repo_type1': {'repo1': None, 'repo2': None, 'repo3': None}, 'repo_type2': {'repo4': {'build': True}}}
        # expected result
        expected = ['repo_type1/repo1', 'repo_type1/repo2', 'repo_type1/repo3', 'repo_type2/repo4']
        # actual testing
        self.assertListEqual(expected, get_repos_list(repos_dict))

    def test_select_projects_location_empty_dict(self):
        for dictionary in empty_values_list:
            with self.assertRaises(AtlassianException):
                get_projects_location(dictionary)

    @patch('scripts.atlassian.gitupdate.platform')
    def test_select_projects_location_win(self, mock_platform):
        mock_platform.system.return_value = 'windows'
        self.assertEqual('win_location', get_projects_location(locations))

    @patch('scripts.atlassian.gitupdate.platform')
    def test_select_projects_location_linux(self, mock_platform):
        mock_platform.system.return_value = 'linux'
        self.assertEqual('linux_location', get_projects_location(locations))

    @patch('scripts.atlassian.gitupdate.platform')
    def test_select_projects_location_macos(self, mock_platform):
        mock_platform.system.return_value = 'macos'
        self.assertEqual('macos_location', get_projects_location(locations))

    def test_get_prepared_git_url_empty_values(self):
        for value in empty_values_list:
            with self.assertRaises(AtlassianException):
                get_prepared_git_url(base_git_url=value, username='user', password='password')
            with self.assertRaises(AtlassianException):
                get_prepared_git_url(base_git_url='git_url', username=value, password='password')
            with self.assertRaises(AtlassianException):
                get_prepared_git_url(base_git_url='git_url', username='user', password=value)

    def test_get_prepared_git_url(self):
        # sample data
        git_url1 = 'https://stash.server.com/scm'
        git_url2 = 'http://stash.server.com/scm'
        git_user = 'username'
        git_pass = 'password'
        # expected result
        expected1 = 'https://username:password@stash.server.com/scm'
        expected2 = 'http://username:password@stash.server.com/scm'
        # tests itself
        self.assertEqual(expected1, get_prepared_git_url(git_url1, git_user, git_pass))
        self.assertEqual(expected2, get_prepared_git_url(git_url2, git_user, git_pass))

    def test_get_project_folder_empty_repo(self):
        for value in empty_values_list:
            with self.assertRaises(AtlassianException):
                get_project_folder(value)

    def test_get_project_folder(self):
        self.assertEqual('bdp', get_project_folder('bdp/project1'))

    def test_get_project_folder_return_empty(self):
        self.assertEqual('', get_project_folder('project2'))
