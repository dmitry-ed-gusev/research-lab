#!/usr/bin/env python
# coding=utf-8

"""
    Just a test...
    Created: Gusev Dmitrii, 13.09.2018
    Modified:
"""

from pykeepass import PyKeePass

# keepass db path
keepass = '/Users/gusevdmi/projects/infapfm/mantis-misc/ops/passwords.kdbx'
# load database
kp = PyKeePass(keepass, password='PROFINITmantis77')

# find any group by its name
group = kp.find_groups(name='HDFS', first=True)
# get the entries in a group
#print '->', group.entries

for entry in group.entries:
    print entry
    print entry.username
    print entry.title

# find any entry by its title
entry = kp.find_entries(title='DEV', first=True)
# retrieve the associated password
#print entry.password
#print entry.notes

# update an entry
#>>> entry.notes = 'primary facebook account'

# create a new group
#>>> group = kp.add_group(kp.root_group, 'email')

# create a new entry
#>>> kp.add_entry(group, 'gmail', 'myusername', 'myPassw0rdXX')
#Entry: "email/gmail (myusername)"

# save database
#>>> kp.save()