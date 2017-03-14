#!/usr/bin/python
#  -*- coding: utf-8 -*-

from os import walk, rename

__author__ = 'gusevd'


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


# -- execute, if runned sigle or imported by other module
if __name__ == '__main__':

    print('[myutils] main is working.')

    # to_upper_case('c:\\temp')
    to_title("c:\\temp")
