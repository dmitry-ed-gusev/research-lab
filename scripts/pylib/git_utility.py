#!/usr/bin/env python
# coding=utf-8

"""
    Utility module for interacting with GIT.

    Created: Gusev Dmitrii, 22.05.2017
    Modified: Gusev Dmitrii, 24.12.2017
"""

from subprocess import Popen
from subprocess import check_output
#import subprocess as sub

# some useful constants
SEPARATOR = '==============================================='
MVN_EXECUTABLE = 'mvn.cmd'
GIT_EXECUTABLE = 'git'


def git_restore_proxy():
    pass


def select_local_location(config):
    """

    :param config:
    :return:
    """
    pass


def git_repo_clone():
    """

    :return:
    """
    pass


def git_repo_update(repo_path):
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


def git_repo_build(repo_path, javadoc=False, sources=False):
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

