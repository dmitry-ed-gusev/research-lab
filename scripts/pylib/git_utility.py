#!/usr/bin/env python
# coding=utf-8

"""
    Utility module for interacting with GIT.
    Created: Gusev Dmitrii, 22.05.2017
    Modified: Gusev Dmitrii, 27.01.2018
"""

import logging
import os
import platform
import pylib.common_constants as myconst
from subprocess import Popen
from pylib.pyutilities import git_set_global_proxy, git_clean_global_proxy

# some useful constants
GIT_EXECUTABLE = 'git'


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
        self.mvn_exec = self.__select_mvn_executable()
        self.repos_list = self.config.get(myconst.CONFIG_KEY_REPO).keys()
        self.log.debug('Loaded repos list [{}].'.format(self.repos_list))
        # init special maven settings - calculate path
        mvn_settings = self.config.get(myconst.CONFIG_KEY_MVN_SETTINGS, default='')
        if mvn_settings:
            self.mvn_settings = os.path.abspath(mvn_settings)
        else:
            self.mvn_settings = None
        self.log.debug('Loaded special maven settings [{}].'.format(self.mvn_settings))

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

    def __select_mvn_executable(self):
        """
        Select Maven executable, depending on OS (windows-family or not). Internal method.
        :return:
        """
        if 'windows' in platform.system().lower():
            mvn_exec = 'mvn.cmd'
        else:
            mvn_exec = 'mvn'
        self.log.debug('__select_mvn_executable(): selected maven executable [{}].'.format(mvn_exec))
        return mvn_exec

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
        self.log.debug('__repo_clone(): starting clone for [{}].'.format(repo_url))
        try:
            process = Popen([GIT_EXECUTABLE, 'clone', repo_url], cwd=self.location)
            process.wait()
            self.log.debug('Clone for [{}] finished.'.format(repo_url))
        except StandardError as se:
            self.log.error('Error cloning repo [{}]! {}'.format(repo_url, se))

    def __repo_update(self, repo_name):
        """
        Pull repository and run gc() on it.
        :param repo_name:
        :return:
        """
        repo_path = self.location + '/' + repo_name
        self.log.debug('__repo_update(): updating repository [{}].'.format(repo_path))
        try:
            process = Popen([GIT_EXECUTABLE, 'pull'], cwd=repo_path)
            process.wait()
            process = Popen([GIT_EXECUTABLE, 'gc'], cwd=repo_path)
            process.wait()
        except StandardError as se:
            self.log.error('Error updating repo [{}]! {}'.format(repo_path, se))

    def __repo_build(self, repo_name):
        """
        Build specified repository, if specified - download sources/javadoc for dependencies.
        :param repo_name:
        :return:
        """
        repo_path = self.location + '/' + repo_name
        self.log.debug('__repo_build(): build repository [{}].'.format(repo_path))
        try:
            # build repository
            cmd = [self.mvn_exec, 'clean', 'install']
            if self.mvn_settings:
                cmd.extend(['-s', self.mvn_settings])
            process = Popen(cmd, cwd=repo_path)
            process.wait()
            # download javadoc packages for dependencies
            if self.config.get(myconst.CONFIG_KEY_MVN_JAVADOC, default=False):
                self.log.debug('__repo_build(): download javadoc for repo [{}] dependencies.'.format(repo_path))
                cmd = [self.mvn_exec, 'dependency:resolve', '-Dclassifier=javadoc']
                if self.mvn_settings:
                    cmd.extend(['-s', self.mvn_settings])
                process = Popen(cmd, cwd=repo_path)
                process.wait()
            # download source packages for dependencies
            if self.config.get(myconst.CONFIG_KEY_MVN_SOURCES, default=False):
                self.log.debug('__repo_build(): download sources for repo [{}] dependencies.'.format(repo_path))
                cmd = [self.mvn_exec, 'dependency:resolve', '-Dclassifier=sources']
                if self.mvn_settings:
                    cmd.extend(['-s', self.mvn_settings])
                process = Popen(cmd, cwd=repo_path)
                process.wait()
        except StandardError as se:
            self.log.error('Error building repo [{}]! {}'.format(repo_path, se))

    def clone(self):
        """
        Clone all repositories, mentioned in config file.
        Set proxy before updating and clean after it.
        """
        git_set_global_proxy(self.config.get(myconst.CONFIG_KEY_PROXY_HTTP),
                             self.config.get(myconst.CONFIG_KEY_PROXY_HTTPS))
        self.log.info('GitUtility: clone():\n\trepositories: [{}]\n\tdestination: [{}].'
                      .format(self.repos_list, self.location))
        for repository in self.repos_list:
            self.__repo_clone(self.__generate_repo_url(repository))
        git_clean_global_proxy()

    def update(self):
        """
        Update (pull) and gc() all repositories, mentioned in config file.
        Set proxy before updating and clean after it.
        """
        git_set_global_proxy(self.config.get(myconst.CONFIG_KEY_PROXY_HTTP),
                             self.config.get(myconst.CONFIG_KEY_PROXY_HTTPS))
        self.log.info('GitUtility: update():\n\trepositories: [{}]'.format(self.repos_list))
        for repository in self.repos_list:
            self.__repo_update(repository)
        git_clean_global_proxy()

    def build(self):
        """
        Build repositories with turned on build setting.
        """
        self.log.info('GitUtility: build():\n\trepositories: [{}]'.format(self.repos_list))
        for repository in self.repos_list:
            if self.config.get(myconst.CONFIG_KEY_REPO_BUILD.format(repository)):
                self.__repo_build(repository)


class GitException(Exception):
    """GIT Exception, used if something is wrong with/in GIT interaction."""
