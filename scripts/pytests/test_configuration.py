#!/usr/bin/env python
# coding=utf-8

import os
import mock

from pylib.configuration import Configuration, ConfigError
from config_aware_test_case import ConfigAwareTestCase

# todo: add more test cases


class ConfigurationTest(ConfigAwareTestCase):

    INVALID_PATHS = ['', "", '   ', "    ", "non-exist path"]

    def setUp(self):
        self.config = Configuration(is_merge_env=False)

    def test_LoadInvalidPathFail(self):
        for invalid_path in ConfigurationTest.INVALID_PATHS:
            with self.assertRaises(ConfigError):
                self.config.load(invalid_path)

    def test_MergeConfigFiles(self):
        self.config.load(self.config_dir)
        # Property from env-related files (config/test.yml)
        self.assertEqual(self.config.get("logging.file_log_level"), "DEBUG")
        # Property from common (config/common.yml)
        self.assertEqual(self.config.get("yarn_queue"), "mantis")

    def test_MergeEnvVariables(self):
        os.environ["yarn_queue"] = "test"
        self.config.merge_env()
        self.assertEqual(self.config.get("yarn_queue"), "test")

    def test_GetNotExistingProp(self):
        with self.assertRaises(ConfigError):
            self.config.get("non_existing")

    def test_GetTopProperty(self):
        self.config.merge_dict({'f': 'h'})
        self.assertEqual(self.config.get('f'), 'h')

    def test_SetTopProperty(self):
        self.config.set('m', 'n')
        self.assertEqual(self.config.get('m'), 'n')

    def test_GetDeepProperty(self):
        self.config.merge_dict({'x': {'y': {'z': '100'}}})
        self.assertEqual(self.config.get('x.y.z'), '100')

    def test_SetDeepProperty(self):
        self.config.set('a.b.c', 'e')
        self.assertEqual(self.config.get('a.b.c'), 'e')

    def test_ReplaceDeepProperty(self):
        self.config.merge_dict({'a': {'b': {'c': 'd'}}})
        self.config.set('a.b.c', 'e')
        self.assertEqual(self.config.get('a.b.c'), 'e')

    # test with mocks (patches) for some functions
    @mock.patch('pylib.configuration.parse_yaml')
    @mock.patch('pylib.configuration.os.path')
    def test_LoadFromSingleYamlFile(self, mock_path, mock_parse_yaml):

        # set returned results for mocks
        mock_path.exists.return_value = True
        mock_path.isfile.return_value = True
        mock_path.isdir.return_value = False
        mock_parse_yaml.return_value = {"key": "value"}

        # check that key doesn't exist in config
        with self.assertRaises(ConfigError):
            self.config.get("key")

        # load config from mocked file
        self.config.load("/config/myyaml123.yml", False)

        # check that now key exists in config
        self.assertEqual(self.config.get("key"), "value")

    # todo: implement test with mocks!
    def test_LoadFromManyYarnFiles(self):
        print "!!!"

    def tearDown(self):
        self.config = None
