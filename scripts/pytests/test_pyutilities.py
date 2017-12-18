#!/usr/bin/env python
# coding=utf-8

"""
    Unit tests for
"""
# todo: implement tests!

import yaml
import unittest
import logging
import logging.config
from mock import patch, mock_open
from pylib.pyutilities import parse_yaml


class ConfigurationTest(unittest.TestCase):

    @classmethod
    def setUpClass(cls):
        cls._log = logging.getLogger(__name__)
        with open('configs/logging.yml', 'rt') as f:
            config = yaml.safe_load(f.read())
        logging.config.dictConfig(config)

    @classmethod
    def tearDownClass(cls):
        pass

    def test_parse_yaml(self):
        with patch('pylib.pyutilities.open', mock_open(read_data='name: value'), create=True):
            result = parse_yaml('foo_ok.file')
        self.assertEquals('value', result['name'])

    def test_parse_yaml_ioerror(self):
        with self.assertRaises(IOError):
            with patch('pylib.pyutilities.open', mock_open(read_data='name:\tvalue'), create=True):
                parse_yaml('foo_ioerror.file')

    def test_parse_yaml_empty_path(self):
        with self.assertRaises(IOError):
            with patch('pylib.pyutilities.open', mock_open(read_data='n: v'), create=True):
                parse_yaml('')


"""
>>> with patch('__main__.open', mock_open(read_data='bibble'), create=True) as m:
...     with open('foo') as h:
...         result = h.read()
...
>>> m.assert_called_once_with('foo')
>>> assert result == 'bibble'

@patch.object(Configuration, 'get')
@patch.object(JIRA, '__init__')
def test_ConnectNotConnectedYet(self, mock_jira, mock_config):
    mock_jira.return_value = None
    mock_config.return_value = 'some value'
    self.jira.connect()
    self.assertTrue(mock_jira.called)\
"""
