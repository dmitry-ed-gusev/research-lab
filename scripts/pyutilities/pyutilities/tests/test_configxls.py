#!/usr/bin/env python
# coding=utf-8

import os
import yaml
import unittest
import logging
import logging.config
from mock import patch
from pyutilities.config import ConfigurationXls, ConfigError

LOGGING_FILE = 'configs/test_logging.yml'
XLS_CONFIG_FILE = 'configs/xls_config.xlsx'
XLS_CONFIG_SHEET = 'config_sheet'


# todo: add more test cases!!!
class ConfigurationTest(unittest.TestCase):

    @classmethod
    def setUpClass(cls):
        cls._log = logging.getLogger(__name__)
        with open(LOGGING_FILE, 'rt') as f:
            config = yaml.safe_load(f.read())
        logging.config.dictConfig(config)

    def setUp(self):
        # init config before each test, don't merge with environment
        self.config = ConfigurationXls(XLS_CONFIG_FILE, XLS_CONFIG_SHEET)

    def tearDown(self):
        self.config = None

    def test_no_args(self):
        with self.assertRaises(TypeError):
            ConfigurationXls()

    def test_no_xls_file(self):
        with self.assertRaises(TypeError):
            ConfigurationXls(path_to_xls='some.xlsx')

    def test_no_xls_sheet(self):
        with self.assertRaises(TypeError):
            ConfigurationXls(config_sheet_name='some_sheet_name')
