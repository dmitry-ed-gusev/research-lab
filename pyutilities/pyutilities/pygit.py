#!/usr/bin/env python
# coding=utf-8

"""

    Some useful/convenient functions related to GIT.

    Created:  Dmitrii Gusev, 03.05.2019
    Modified:

"""

from subprocess import Popen
from pyutilities.pylog import init_logger

# useful constants
GIT_EXECUTABLE = 'git'


class PyGit:
    """ Class represents GIT functionality """

    def __init__(self, git_address, git_user, git_pass, http=None, https=None):
        self.log = init_logger(__name__)
        self.log.info("Initializing Maven class.")
        # init internal state
        self.__git_address = git_address
        # self.__git_user = git_user
        # self.__git_pass = git_pass
        # set up proxy if specified
        self.set_global_proxy(http, https)

    def set_global_proxy(self, http=None, https=None):  # todo: unit tests! make it decorator.
        """
        Set specified proxies (both http/https) for local git globally.
        :param http:
        :param https:
        """
        self.log.debug("git_set_global_proxy() is working. Setting proxies: http -> [{}], https -> [{}]".format(http, https))
        if http:
            self.log.info("Setting HTTP proxy: {}".format(http))
            process = Popen([GIT_EXECUTABLE, 'config', '--global', 'http.proxy', http])
            process.wait()
        if https:
            self.log.debug("Setting HTTPS proxy: {}".format(http))
            process = Popen([GIT_EXECUTABLE, 'config', '--global', 'https.proxy', https])
            process.wait()

    def __generate_repo_url(self, repo_name):
        """ Generates repository URL for clone. Internal method.
        :param repo_name:
        :return:
        """
        repo_url = self.__git_address + '/' + repo_name + '.git'
        self.log.debug('__generate_repo_url(): generated repo url [{}].'.format(repo_url))
        return repo_url

    def clean_global_proxy(self):  # todo: unit tests! make it decorator.
        """ Clear git global proxies (both http/https). """
        self.log.debug("git_clean_global_proxy() is working.")
        process = Popen([GIT_EXECUTABLE, 'config', '--global', '--unset', 'http.proxy'])
        process.wait()
        process = Popen([GIT_EXECUTABLE, 'config', '--global', '--unset', 'https.proxy'])
        process.wait()

    def clone(self, repo_path, repo_url):
        """ """
        self.log.info("Clone repo [{}].".format(repo_path))
        try:
            process = Popen([GIT_EXECUTABLE, 'clone', repo_url], cwd=repo_path)
            process.wait()
            # self.log.debug('Clone for [{}] finished.'.format(repo_url))  # <- too much output
        except AttributeError as se:
            self.log.error('Error while cloning repo [{}]! {}'.format(repo_url, se))

    def pull(self, repo_path):
        """ """
        # todo: POSSIBLE BUG!!! if remote pass has been changed -> it can't be updated (delete folder and clone!)
        self.log.info("Pull repo [{}]".format(repo_path))
        try:
            process = Popen([GIT_EXECUTABLE, 'pull'], cwd=repo_path)
            process.wait()
        except AttributeError as se:
            self.log.error('Error while updating repo [{}]! {}'.format(repo_path, se))

    def gc(self, repo_path):
        """ """
        self.log.info("Calling gc() for repo [{}]".format(repo_path))
        try:
            process = Popen([GIT_EXECUTABLE, 'gc'], cwd=repo_path)
            process.wait()
        except AttributeError as se:
            self.log.error('Error while calling gc() for [{}]! {}'.format(repo_path, se))
