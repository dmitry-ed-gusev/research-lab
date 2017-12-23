#!/usr/bin/python
#  -*- coding: utf-8 -*-

"""
 Common utilities in python. Can be useful in different cases.
 Created: Gusev Dmitrii, 04.04.2017
 Modified: Gusev Dmitrii, 17.12.2017
"""

# todo: implement proper logging!

import os
import sys
import csv
import yaml
import argparse
import logging
import logging.config
import _common_constants as const
from os import walk, rename

# configure logger on module level. it isn't a good practice, but it's convenient.
# don't forget to set disable_existing_loggers=False, otherwise logger won't get its config!
log = logging.getLogger(__name__)
# to avoid errors like 'no handlers' for libraries it's necessary to add NullHandler.
log.addHandler(logging.NullHandler())


def setup_logging(default_path='configs/logging.yml', default_level=logging.INFO, env_key='LOG_CFG'):
    """
        Setup logging configuration - load it from YAML file.
        :param default_path path to logging config YAML file
        :param default_level default logging level - INFO
        :param env_key environment variable key to override settings from cmd line,
               like LOG_CFG=my_logging_config.yml
    """
    path = default_path
    value = os.getenv(env_key, None)
    if value:
        path = value
    if os.path.exists(path):
        with open(path, 'rt') as f:
            config = yaml.safe_load(f.read())
        logging.config.dictConfig(config)
    else:
        logging.basicConfig(level=default_level)


def count_lines(filename):
    """
    Count lines in given file.
    :return: count of lines
    """
    counter = 0
    # open file, received as first cmd line argument, mode - read+Unicode
    with open(filename, mode='rU') as file:
        # skip initial space - don't work without it
        reader = csv.reader(file, delimiter=b',', skipinitialspace=True, quoting=csv.QUOTE_MINIMAL, quotechar=b'"',
                            lineterminator="\n")
        # counting rows in a cycle
        for row in reader:
            # just a debug output
            # print row
            counter += 1
    # debug - print count to console
    print "Lines count: {}".format(counter)
    return counter


def _list_files(path, files_buffer, out_to_console=False):
    """
    Internal function for listing (recursively) all files in specified directory.
    Don't use it directly, use list_files()
    :param path: path to iterate through
    :param files_buffer: buffer list for collection files
    :param out_to_console: out to console processing file
    """
    # print "STDOUT encoding ->", sys.stdout.encoding  # <- just a debug output
    for (dirpath, dirnames, filenames) in walk(unicode(path)):
        for filename in filenames:
            abs_path = dirpath + '/' + filename
            if out_to_console:  # debug output
                if sys.stdout.encoding is not None:  # sometimes encoding may be null!
                    print abs_path.encode(sys.stdout.encoding, errors='replace')
                else:
                    print abs_path
            files_buffer.append(abs_path)


def list_files(path, out_to_console=False):
    """
    List all files in a specified path and return list of found files.
    :param path: path to directory
    :param out_to_console: do or don't output to system console
    :return: list of files
    """
    log.debug("list_files() is working. Path [{}].".format(path))
    if not path or not path.strip():  # fail-fast #1
        raise IOError("Can't list files in empty path!")
    if not os.path.exists(path) or not os.path.isdir(path):  # fail-fast #2
        raise IOError("Path [{}] doesn't exist or not a directory!".format(path))
    files = []
    _list_files(path, files, out_to_console)
    return files


def parse_yaml(file_path):
    """
    Parses single YAML file and return its contents as object (dictionary).
    :param file_path: path to YAML file to load settings from
    :return python object with YAML file contents
    """
    log.debug("parse_yaml() is working. Parsing YAML file [{}].".format(file_path))
    if not file_path or not file_path.strip():
        raise IOError("Empty path to YAML file!")
    with open(file_path, 'r') as cfg_file:
        cfg_file_content = cfg_file.read()
        if "\t" in cfg_file_content:  # no tabs allowed in file content
            raise IOError("Config file [{}] contains 'tab' character!".format(file_path))
        return yaml.load(cfg_file_content)


"""
def init_config(parser, is_merge_env = False):
    print "init_config() is working."
    # get argparse namespace (will be filled with vars after parsing)
    argparse_namespace = argparse.Namespace()
    # prepare cmd line parser and parse cmd line (put all in specified namespace)
    args = parser.parse_args(namespace=argparse_namespace)
    # load configuration from specified or default config, don't merge with environment (default)
    config = Configuration(getattr(args, const.CONFIG_KEY_CFG_FILE), is_merge_env=is_merge_env)
    # add cmd line arguments to config (overwrite existing, if value is present)
    for key, value in vars(argparse_namespace).items():
        if value:
            config.set(key, value)

    # just a debug output
    print "Configuration: %s" % config.config_dict
    # return created config
    return config
"""


if __name__ == '__main__':
    print "pyutilities: Don't try to execute library as standalone app!"
    # list_files('/media/vinnypuhh/MyData/Cloud/YandexDisk/DOCS AND BOOKS')
    # list = list_files('/media/vinnypuhh/MyData', True)
    # print len(list)
