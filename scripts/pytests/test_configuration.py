#!/usr/bin/env python
# coding=utf-8
# Copyright © 2017 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
# All rights reserved.

import os

from pylib.configuration import Configuration, ConfigError
from config_aware_test_case import ConfigAwareTestCase


class ConfigurationTest(ConfigAwareTestCase):

    def setUp(self):
        self.config = Configuration()

    def test_LoadNoPathFail(self):
        self.config.load("config/test.yml")
        with self.assertRaises(Exception):
            self.config.load()

    def test_LoadInvalidPathFail(self):
        with self.assertRaises(Exception):
            self.config.load("config_non_existing")

    def test_MergeConfigFiles(self):
        self.config.load(self.config_dir)
        # Property from env-related files
        self.assertEqual(self.config.get("logging.file_log_level"), "DEBUG")
        # Property from common
        self.assertEqual(self.config.get("yarn_queue"), "mantis")

    def test_MergeEnvVariables(self):
        os.environ["yarn_queue"] = "test"
        self.config.load(self.config_dir)
        self.assertEqual(self.config.get("yarn_queue"), "test")

    def test_GetNotExistingProp(self):
        with self.assertRaises(ConfigError):
            self.config.get("non_existing")

    def test_GetTopProperty(self):
        self.config.merge_dict({'f':'h'})
        self.assertEqual(self.config.get('f'), 'h')

    def test_SetTopProperty(self):
        self.config.set('m', 'n')
        self.assertEqual(self.config.get('m'), 'n')

    def test_GetDeepProperty(self):
        self.config.merge_dict({'x':{'y':{'z':'100'}}})
        self.assertEqual(self.config.get('x.y.z'), '100')

    def test_SetDeepProperty(self):
        self.config.set('a.b.c', 'e')
        self.assertEqual(self.config.get('a.b.c'), 'e')

    def test_ReplaceDeepProperty(self):
        self.config.merge_dict({'a':{'b':{'c':'d'}}})
        self.config.set('a.b.c', 'e')
        self.assertEqual(self.config.get('a.b.c'), 'e')

    def tearDown(self):
        self.config = None
