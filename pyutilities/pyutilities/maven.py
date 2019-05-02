#!/usr/bin/env python
# coding=utf-8

"""

    Some useful/convenient functions related to Maven build tool.

    Created:  Dmitrii Gusev, 02.05.2019
    Modified:
"""

import platform
from subprocess import Popen
from pyutilities.pylog import init_logger

# init logger
LOG = init_logger("maven")


def select_mvn_executable():
    """ Select Maven executable, depending on OS (windows-family or not). Internal method.
    :return:
    """
    if 'windows' in platform.system().lower():
        mvn_exec = 'mvn.cmd'
    else:
        mvn_exec = 'mvn'
    LOG.info('MAVEN executable selected: [{}].'.format(mvn_exec))
    return mvn_exec


# init Maven executable for current platform
__MVN_EXEC = select_mvn_executable()


def mvn_build_repo(repo_path, mvn_settings=None):
    LOG.info('Building repository [{}].'.format(repo_path))

    global __MVN_EXEC

    try:
        cmd = [__MVN_EXEC, 'clean', 'install']
        if mvn_settings is not None:
            cmd.extend(['-s', mvn_settings])
        process = Popen(cmd, cwd=repo_path)
        process.wait()
    except AttributeError as se:
        LOG.error('Error building repo [{}]! {}'.format(repo_path, se))


def mvn_javadoc_repo():
    pass


def mvn_sources_repo():
    pass


def __repo_build(self, repo_name):
    """ Build specified repository, if specified - download sources/javadoc for dependencies.
    :param repo_name:
    :return:
    """
    repo_path = self.location + '/' + repo_name
    self.log.info('Building repository [{}].'.format(repo_path))
    try:
        # build repository
        cmd = [self.mvn_exec, 'clean', 'install']
        if self.mvn_settings:
            cmd.extend(['-s', self.mvn_settings])
        process = Popen(cmd, cwd=repo_path)
        process.wait()
        # download javadoc packages for dependencies
        if self.config.get(CONFIG_KEY_MVN_JAVADOC, default=False):
            self.log.info('Downloading javadoc for repository [{}].'.format(repo_path))
            cmd = [self.mvn_exec, 'dependency:resolve', '-Dclassifier=javadoc']
            if self.mvn_settings:
                cmd.extend(['-s', self.mvn_settings])
            process = Popen(cmd, cwd=repo_path)
            process.wait()
        # download source packages for dependencies
        if self.config.get(CONFIG_KEY_MVN_SOURCES, default=False):
            self.log.info('Downloading sources for repository [{}].'.format(repo_path))
            cmd = [self.mvn_exec, 'dependency:resolve', '-Dclassifier=sources']
            if self.mvn_settings:
                cmd.extend(['-s', self.mvn_settings])
            process = Popen(cmd, cwd=repo_path)
            process.wait()
    except AttributeError as se:
        self.log.error('Error building repo [{}]! {}'.format(repo_path, se))
