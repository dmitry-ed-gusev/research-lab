#!/usr/bin/env python
# coding=utf-8
# encoding=utf8

"""

"""

import sys
from os import walk

print "python encoding -> {}".format(sys.getdefaultencoding())  # <- python
print "OS encoding -> {}".format(sys.getfilesystemencoding())  # <- OS

# path = "D:/Cloud/YandexDisk/ФОТОГРАФИИ"
path = "D:/Cloud/YandexDisk/"

# print "File Manager. Processing [{}].".format(path)
print path

# go trough path recursively and search all files
counter = 0
for (dirpath, dirnames, filenames) in walk(path):
    prefix = dirpath + '\\'
    #print "DIR -> [{}]".format(dirpath)
    print dirpath
    counter += len(filenames)
    # process all found files in a directory
    # for filename in filenames:
    #    print "FILE -> [{}]".format(prefix + filename)

print "Found [{}] file(s).".format(counter)
