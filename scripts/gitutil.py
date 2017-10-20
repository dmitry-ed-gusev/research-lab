#!/usr/bin/env python
# coding=utf-8

"""
 Go through list of repositories and 'git pull' + 'mvn clean install' on them.
 Created: Gusev Dmitrii, 03.04.2017
 Modified: Gusev Dmitrii, 25.04.2017
"""

import os
import sys
import subprocess as sub
import argparse
from pylib import configuration as conf

# todo: move common functions to library/utility module

# some useful constants
SEPARATOR = '==============================================='
MVN_EXECUTABLE = 'mvn.cmd'
GIT_EXECUTABLE = 'git'


def prepare_arg_parser():
    """
    Prepare and return cmd line parser.
    :return: prepared cmd line parser
    """
    # create arguments parser
    parser = argparse.ArgumentParser(description='GIT Utility.')
    # optional arguments
    parser.add_argument('--config-dir', dest='config_dir', action='store', default='configs',
                        help='Configuration directory (*.yml/*.yaml files)')
    parser.add_argument('--no-git-update', dest='git_update_off', action='store_true', help='Skip updating git repos')
    parser.add_argument('--no-mvn-build', dest='mvn_build_off', action='store_true', help='Skip Maven build')
    parser.add_argument('--javadoc', dest='javadoc', action='store_true', help='Download Maven dependencies javadoc')
    parser.add_argument('--sources', dest='sources', action='store_true', help='Download Maven dependencies sources')
    return parser


def git_update(repo_path):
    """
    Update repository from GIT server
    :param repo_path:
    :return:
    """
    print "\n{}\nUpdating repository [{}].".format(SEPARATOR, repo_path)
    try:
        # status of current repo
        p = sub.Popen([GIT_EXECUTABLE, 'status'], cwd=repo_path)
        p.wait()
        # update current repo
        p = sub.Popen([GIT_EXECUTABLE, 'pull'], cwd=repo_path)
        p.wait()
        # run gc() on current repository
        p = sub.Popen([GIT_EXECUTABLE, 'gc'], cwd=repo_path)
        p.wait()
    except WindowsError as we:
        print "ERROR: {}".format(we)


def mvn_build(repo_path, javadoc=False, sources=False):
    """
    Build repository and download javadoc/sources
    :param repo_path:
    :param javadoc:
    :param sources:
    :return:
    """
    print "\n{}\nBuilding repository [{}].".format(SEPARATOR, repo_path)
    try:
        # build current repo
        p = sub.Popen([MVN_EXECUTABLE, 'clean', 'install'], cwd=repo_path)
        p.wait()

        # download javadoc for dependencies
        if javadoc:
            # download all sources and docs related to dependencies
            p = sub.Popen([MVN_EXECUTABLE, 'dependency:resolve', '-Dclassifier=javadoc'], cwd=repo_path)
            p.wait()

        # download sources for dependencies
        if sources:
            p = sub.Popen([MVN_EXECUTABLE, 'dependency:resolve', '-Dclassifier=sources'], cwd=repo_path)
            p.wait()

    except WindowsError as we:
        print "ERROR: {}".format(we)


# starting utility
print "\nProjects processing is starting."
# prepare and parse cmd line
args = prepare_arg_parser().parse_args()

# try to load config from specified directory
if not args.config_dir or not args.config_dir.strip() \
        or not os.path.isdir(args.config_dir) or not os.path.exists(args.config_dir):
    print "Config dir [{}] is invalid!".format(args.config_dir)
    sys.exit(1)

# config dir is ok - loading
config = conf.Configuration()
config.load(args.config_dir)

# get repositories configs (paths)
base_dir = config.get('repos_dir')
# fix base projects dir (path ending)
if not (base_dir.endswith("\\") or base_dir.endswith("/")):
    base_dir += "/"
# get repos list from config
repos_list = config.get('repos')
build_list = config.get('build_repos')

# update all repos in list (git status/pull/gc), if not switched off
if not args.git_update_off:
    for repo in repos_list:
        git_update(base_dir + repo)
else:
    print "\nUpdating repositories from GIT is turned OFF."

# build all repos in build list
if not args.mvn_build_off:
    for repo in build_list:
        mvn_build(base_dir + repo, args.javadoc, args.sources)
else:
    print "\nMaven build if turned OFF."

print "\n{}\nRepositories processing has finished.".format(SEPARATOR)
