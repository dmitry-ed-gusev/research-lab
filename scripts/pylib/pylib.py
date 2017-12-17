#!/usr/bin/python
#  -*- coding: utf-8 -*-

"""
 Common utilities in python. Can be useful in different cases.
 Created: Gusev Dmitrii, 04.04.2017
"""

import csv
import yaml
from os import walk, rename


def count_lines(filename):
    """
    Count lines in given file.
    :return: count of lines
    """
    counter = 0
    # open file, received as first cmd line argument, mode - read+Unicode
    with open(filename, mode='rU') as file:
        # skip initial space - don't work without it
        reader = csv.reader(file, delimiter=b',', skipinitialspace=True, quoting=csv.QUOTE_MINIMAL, quotechar=b'"', lineterminator="\n")
        # counting rows in a cycle
        for row in reader:
            # just a debug output
            # print row
            counter += 1
    # debug - print count to console
    print "Lines count: {}".format(counter)
    return counter


def to_upper_case(path):
    """
    Rename all files in a given directory to upper case
    :return: nothing
    """
    print("to_upper_case() is working.")
    # -- go trough path recursively and search all files
    # for (dirpath, dirnames, filenames) in walk(unicode(path)): <- python 2
    for (dirpath, dirnames, filenames) in walk(path):
        # -- iterate over found files in concrete directory and rename them (to upper case)
        prefix = dirpath + '\\'
        for filename in filenames:
            before = prefix + filename
            after = prefix + filename.upper()
            rename(before, after)
            print('to upper case:', before, '->', after)


def to_title(path):
    """
    Rename all files in a given directory to title case (all
    words with uppercase first letter)
    :return: nothing
    """
    print ("to_title() is working.")
    # -- go trough path recursively and search all files
    # for (dirpath, dirnames, filenames) in walk(unicode(path)): <- python 2
    for (dirpath, dirnames, filenames) in walk(path):
        # -- iterate over found files in concrete directory and rename them (to upper case)
        prefix = dirpath + '\\'
        for filename in filenames:
            before = prefix + filename
            after = prefix + filename.title()
            rename(before, after)
            print('to title case:' + before + ' -> ' + after)


def parse_yaml(file_path):
    """
        Parses single YAML file and return it contents
        :param file_path: path to YAML file to load settings from
        :rtype: mantis.lib.configuration.Configuration
    """
    with open(file_path, 'r') as cfg_file:
        try:
            cfg_file_content = cfg_file.read()
            if "\t" in cfg_file_content:
                raise ConfigError("Config file %s contains 'tab' character" % file_path)
            config = yaml.load(cfg_file_content)
        except yaml.YAMLError as err:
            print "Failed to parse config file %s. Error: %s" % (file_path, err)
            raise ConfigError("Failed to parse config file %s. Error: %s" % (file_path, err))
        return config


class ConfigError(Exception):
    """Invalid configuration error"""


class JiraException(Exception):
    """JIRA Exception, used if something is wrong with/in JIRA interaction."""
