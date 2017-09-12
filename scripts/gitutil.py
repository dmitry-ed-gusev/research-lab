#!/usr/bin/env python
# coding=utf-8

"""
 Go through list of repositories and 'git pull' + 'mvn clean install' on them.
 Created: Gusev Dmitrii, 03.04.2017
 Modified: Gusev Dmitrii, 25.04.2017
"""

# todo: move processing repo in a function
# todo: move building repo in a function
# todo: add cmd line options

import subprocess as sub
from pylib import configuration as conf

# noinspection PyCompatibility
print "\nGIT repositories processing is starting..."

# some useful constants
SEPARATOR = '==============================================='

# path to configs (directory)
configs = 'configs'
# create config object and load configs
config = conf.Configuration()
config.load(configs)

# get repositories configs (paths)
base_dir = config.get('projects_dir')
# fix base projects dir (path ending)
if not (base_dir.endswith("\\") or base_dir.endswith("/")):
    base_dir += "/"
# get repos list from config
repos_list = config.get('repositories')
build_list = config.get('build_repositories')

# update all repos in list (git pull)
for repo in repos_list:
    work_dir = base_dir + repo
    print "\n{}\nProcessing repository [{}] in [{}].".format(SEPARATOR, repo, work_dir)
    try:
        # status of current repo
        p = sub.Popen(['git', 'status'], cwd=work_dir)
        p.wait()
        # update current repo
        p = sub.Popen(['git', 'pull'], cwd=work_dir)
        p.wait()
        # run gc() on current repository
        p = sub.Popen(['git', 'gc'], cwd=work_dir)
        p.wait()
    except WindowsError as we:
        print "ERROR: {}".format(we)

# build all repos in build list
for repo in build_list:
    work_dir = base_dir + repo
    print "\n{}\nBuilding repository [{}] in [{}].".format(SEPARATOR, repo, work_dir)
    try:
        # build current repo
        p = sub.Popen(['mvn.cmd', 'clean', 'install'], cwd=work_dir)
        p.wait()
    except WindowsError as we:
        print "ERROR: {}".format(we)

print "\n{}\nGIT repositories processing has finished.".format(SEPARATOR)
