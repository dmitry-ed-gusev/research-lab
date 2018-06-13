#!/usr/bin/python
#  -*- coding: utf-8 -*-
import os

fname = "/Users/gusevdmi/Downloads/List of users Mantis.txt"
outfile = "/Users/gusevdmi/Downloads/list_emails.txt"

# -- count lines number for file
print 'file length -> ', sum(1 for line in open(fname, 'r'))
counter = 0
# do processing progress output
progress_output = False

# remove output file (if exist)
try:
    os.remove(outfile)
except OSError:
    pass
# create new empty file
open(outfile, 'a').close()

# open and iterate over
fh = open(fname)
for line in fh:

    # -- simple debug output
    if counter % 200 == 0 and progress_output:
        print "processed ->", counter

    # get email(s) from line
    addresses = [address[1:-1] for address in line.split() if address.startswith('<')]
    # print '-> ', addresses
    # write each found email into text file
    with open(outfile, "a") as myfile:
        for address in addresses:
            myfile.write(address)
            myfile.write('\n')

    # -- increment counter after processing
    counter += 1

# -- last simple debug output
if progress_output:
    print "processed ->", counter
