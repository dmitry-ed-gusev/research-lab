#!/usr/bin/env python
# coding=utf-8

import os
import unittest

from scripts.pylib.configuration import Configuration


class ConfigurationTest(unittest.TestCase):

    def setUp(self):
        self.config = Configuration()

    @unittest.expectedFailure
    def test_LoadNoPathFail(self):
        self.config.load()

    @unittest.expectedFailure
    def test_LoadInvalidPathFail(self):
        self.config.load('lib/python/config_nonexisting')

    @unittest.SkipTest
    def test_MergeConfigFiles(self):
        self.config.load('lib/python/config')
        # Property from env-related files
        self.assertEqual(self.config.get("logging.file_log_level"), "DEBUG")
        # Property from common
        self.assertEqual(self.config.get("yarn_queue"), "mantis")

    @unittest.SkipTest
    def test_MergeEnvVariables(self):
        os.environ["yarn_queue"] = "test"
        self.config.load('lib/python/config')
        self.assertEqual(self.config.get("yarn_queue"), "test")

    @unittest.expectedFailure
    def test_GetNotExistingProp(self):
        self.config.get("nonexisting")

    def tearDown(self):
        self.config = None
