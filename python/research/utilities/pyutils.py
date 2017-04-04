#!/usr/bin/python
#  -*- coding: utf-8 -*-

"""
 Common utilities in python. Can be useful in different cases.
 Created: Gusev Dmitrii, 04.04.2017
"""

import csv
from os import walk, rename

__author__ = 'gusevd'


def count_lines(filename):
    """
    COunt lines in given file.
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
