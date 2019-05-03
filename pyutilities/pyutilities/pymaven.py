#!/usr/bin/env python
# coding=utf-8

"""

    Some useful/convenient functions related to Maven build tool.
    Functions are incapsulated in PyMaven class.

    Created:  Dmitrii Gusev, 02.05.2019
    Modified: Dmitrii Gusev, 03.05.2019

"""

import platform
from subprocess import Popen
from pyutilities.pylog import init_logger


class PyMaven:
    """ Class represents maven functionality. """

    def __init__(self):
        self.log = init_logger(__name__)
        self.log.info("Initializing Maven class.")
        self.__mvn_exec = self.__select_mvn_executable()

    def __select_mvn_executable(self):
        """ Select Maven executable, depending on OS (windows-family or not). Internal method.
        :return:
        """
        if 'windows' in platform.system().lower():
            mvn_exec = 'mvn.cmd'
        else:
            mvn_exec = 'mvn'
        self.log.info('MAVEN executable selected: [{}].'.format(mvn_exec))
        return mvn_exec

    def mvn_build_repo(self, repo_path, mvn_settings=None):
        self.log.info('Building repo [{}].'.format(repo_path))
        try:
            cmd = [self.__mvn_exec, 'clean', 'install']
            if mvn_settings is not None:
                cmd.extend(['-s', mvn_settings])
            process = Popen(cmd, cwd=repo_path)
            process.wait()
        except AttributeError as se:
            self.log.error('Error building repo [{}]! {}'.format(repo_path, se))

    def mvn_javadoc_repo(self, repo_path, mvn_settings=None):
        self.log.info('Downloading javadoc for repo [{}].'.format(repo_path))
        try:
            cmd = [self.__mvn_exec, 'dependency:resolve', '-Dclassifier=javadoc']
            if mvn_settings is not None:
                cmd.extend(['-s', mvn_settings])
                process = Popen(cmd, cwd=repo_path)
                process.wait()
        except AttributeError as se:
            self.log.error('Error downloading javadoc for repo [{}]! {}'.format(repo_path, se))

    def mvn_sources_repo(self, repo_path, mvn_settings=None):
        self.log.info('Downloading sources for repo [{}].'.format(repo_path))
        try:
            cmd = [self.__mvn_exec, 'dependency:resolve', '-Dclassifier=sources']
            if mvn_settings:
                cmd.extend(['-s', mvn_settings])
                process = Popen(cmd, cwd=repo_path)
                process.wait()
        except AttributeError as se:
            self.log.error('Error downloading sources for repo [{}]! {}'.format(repo_path, se))
