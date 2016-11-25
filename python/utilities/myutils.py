#!/usr/bin/python
#  -*- coding: utf-8 -*-

from os import walk, rename

__author__ = 'gusevd'


def to_upper_case(path):
    """
    Rename all files in a directory to upper case
    :return: nothing
    """

    # -- go trough path recursively and search all files
    for (dirpath, dirnames, filenames) in walk(unicode(path)):

        # -- iterate over found files in concrete directory and rename them (to upper case)
        prefix = dirpath + '\\'
        for filename in filenames:
            before = prefix + filename
            after  = prefix + filename.upper()
            rename(before, after)
            print 'to upper case:', before, '->', after

# -- execute, if runned sigle or imported by other module
if __name__ == '__main__':
    print '[myutils] working.'
    to_upper_case('c:\\temp')
