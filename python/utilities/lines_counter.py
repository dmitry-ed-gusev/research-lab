#!/usr/bin/python
#  -*- coding: utf-8 -*-

import csv, sys

counter = 0
# open file, received as first cmd line argument, mode - read+Unicode
with open(sys.argv[1], mode='rU') as file:
    # skip initial space - don't work without it
    reader = csv.reader(file, delimiter=b',', skipinitialspace=True, quoting=csv.QUOTE_MINIMAL, quotechar=b'"', lineterminator="\n")
    # counting rows in a cycle
    for row in reader:
        # just a debug output
        #print row
        counter = counter + 1

# print count to console
print counter