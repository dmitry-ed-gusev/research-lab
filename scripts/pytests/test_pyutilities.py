#!/usr/bin/env python
# coding=utf-8

"""
    Unit tests for pyutilities module. Covers most of methods in a module.
    Created: Gusev Dmitrii, 2017
    Modified: Gusev Dmitrii, 04.02.2018
"""

import yaml
import unittest
import logging
import logging.config
from mock import patch, mock_open
from pyutilities import parse_yaml, list_files, _list_files


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

    def test_parse_yaml_empty_paths(self):
        for path in ['', '   ']:
            with self.assertRaises(IOError):
                with patch('pylib.pyutilities.open', mock_open(read_data='n: v'), create=True):
                    parse_yaml(path)

    def test_list_files_invalid_paths(self):
        for path in ['', '    ', 'not-existing-path', '__init__.py']:  # the last one - existing python file
            with self.assertRaises(IOError):
                list_files(path)

    @patch('pylib.pyutilities.walk')
    def test_internal_list_files(self, mock_walk):
        mock_walk.return_value = [('/path', ['dir1'], ['file1'])]

        files = []
        _list_files('zzz', files, True)
        self.assertEquals(1, len(files))
        self.assertEquals('/path/file1', files[0])
