#!/usr/bin/env python
# coding=utf-8

"""
    Utility module for interacting with GIT.

    Created: Gusev Dmitrii, 22.05.2017
    Modified: Gusev Dmitrii, 14.01.2018
"""

import logging
import platform
import pylib.common_constants as myconst
from subprocess import Popen

# some useful constants
# SEPARATOR = '==============================================='
MVN_EXECUTABLE = 'mvn.cmd'
GIT_EXECUTABLE = 'git'
# configure module logger
# log = logging.getLogger(__name__)
# log.addHandler(logging.NullHandler())


class GitUtility(object):
    """
    Class for interaction with GIT through cmd line utility 'git'.
    """

    @property
    def config(self):
        return self.__config

    def __init__(self, config):
        # init logger
        self.log = logging.getLogger(__name__)
        self.log.addHandler(logging.NullHandler())
        self.log.debug("Initializing GIT Utility class.\nConfig [{}].".format(config))

        # init config
        if not config:
            raise GitException('Empty config for GIT Utility!')
        self.__config = config

        # init internal state of class
        self.location = self.__select_location()
        self.repos_list = self.config.get(myconst.CONFIG_KEY_REPO).keys()
        self.log.debug('Loaded repos list [{}].'.format(self.repos_list))

    def __select_location(self):
        """
        Select local repositories location, depending on underlying OS. Internal method.
        :return:
        """
        if 'windows' in platform.system().lower():
            location = self.config.get(myconst.CONFIG_KEY_REP_LOCATION_WIN)
        elif 'linux' in platform.system().lower():
            location = self.config.get(myconst.CONFIG_KEY_REP_LOCATION_LINUX)
        else:
            location = self.config.get(myconst.CONFIG_KEY_REP_LOCATION_MACOS)
        # debug out and return value
        self.log.debug('__select_location(): selected location [{}].'.format(location))
        return location

    def __generate_repo_url(self, repo_name):
        """
        Generates repository URL for clone. Internal method.
        :param repo_name:
        :return:
        """
        repo_url = self.config.get(myconst.CONFIG_KEY_STASH_ADDRESS)\
            .format(self.config.get(myconst.CONFIG_KEY_STASH_PASS)) + \
            self.config.get(myconst.CONFIG_KEY_REPO_KEY.format(repo_name)) + '/' + repo_name + '.git'
        self.log.debug('__generate_repo_url(): generated repo url [{}].'.format(repo_url))
        return repo_url

    def __repo_clone(self, repo_url):
        """
        Clones repo by specified url to local location. Internal method.
        :param repo_url:
        """
        try:
            self.log.debug('__repo_clone(): starting clone for [{}].'.format(repo_url))
            process = Popen([GIT_EXECUTABLE, 'clone', repo_url], cwd=self.location)
            process.wait()
            self.log.debug('Clone for [{}] finished.'.format(repo_url))
        except StandardError as we:
            self.log.error('Error cloning repo [{}]! {}'.format(repo_url, we))

    def clone(self):
        """ Clone all repositories, mentioned in config file. """
        self.log.info('GitUtility: clone():\n\trepositories: [{}]\n\tdestination: [{}].'
                      .format(self.repos_list, self.location))
        for repository in self.repos_list:
            self.__repo_clone(self.__generate_repo_url(repository))


def git_repo_update(repo_path):
    """
    Pull repository from GIT server, after pull - do gc on repository.
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


class GitException(Exception):
    """GIT Exception, used if something is wrong with/in GIT interaction."""
