#!/usr/bin/env python
# coding=utf-8

"""

    Some useful/convenient functions related to GIT.

    13.05.2019 Module is in DRAFT state.

    Created:  Dmitrii Gusev, 03.05.2019
    Modified: Dmitrii Gusev, 25.05.2019

"""

from subprocess import Popen
from pyutilities.pylog import init_logger, setup_logging, myself

# useful constants
GIT_EXECUTABLE = 'git'
# REPO_FUNCTION_CLONE = 'clone'
# REPO_FUNCTION_UPDATE = 'update'
# config keys for repositories hierarchy
# CONFIG_KEY_REPO = 'repositories'
# CONFIG_KEY_REPO_KEY = 'repositories.{}'
# CONFIG_KEY_REPO_BUILD = 'repositories.{}.build'
# config keys for GIT/Stash
# CONFIG_KEY_GIT_ADDRESS = "stash.address"
# CONFIG_KEY_GIT_PASS = "stash.password"
# options - download javadoc/sources
# CONFIG_KEY_MVN_JAVADOC = 'javadoc'
# CONFIG_KEY_MVN_SOURCES = 'sources'
# CONFIG_KEY_MVN_SETTINGS = 'mvn_settings'

# module logger
log = init_logger('pygit')


class PyGit:
    """ Class represents GIT functionality """

    def __init__(self, git_address, git_user, git_pass, http=None, https=None):
        self.log = init_logger(__name__)
        self.log.info("Initializing PyGit class.")
        # init internal state
        self.__git_address = git_address
        self.__git_user = git_user
        self.__git_pass = git_pass
        # set up proxy if specified
        self.set_global_proxy(http, https)

        # init special maven settings - calculate path
        mvn_settings = self.config.get(CONFIG_KEY_MVN_SETTINGS, default='')
        if mvn_settings:
            self.mvn_settings = os.path.abspath(mvn_settings)
        else:
            self.mvn_settings = None
        self.log.info('Loaded special maven settings [{}].'.format(self.mvn_settings))

    def set_global_proxy(self, http=None, https=None):  # todo: unit tests! make it decorator.
        """ Set specified proxies (http/https) for local git instance as a global variables. """
        self.log.debug(f"{myself()}() is working. Setting proxies: http -> [{http}], https -> [{https}]")
        if http:
            self.log.info(f"Setting HTTP proxy: {http}")
            process = Popen([GIT_EXECUTABLE, 'config', '--global', 'http.proxy', http])
            process.wait()
        if https:
            self.log.info(f"Setting HTTPS proxy: {https}")
            process = Popen([GIT_EXECUTABLE, 'config', '--global', 'https.proxy', https])
            process.wait()

    def clean_global_proxy(self):  # todo: unit tests! make it decorator.
        """ Clear git global proxies (both http/https). """
        self.log.debug(f"{myself()}() is working.")
        process = Popen([GIT_EXECUTABLE, 'config', '--global', '--unset', 'http.proxy'])
        process.wait()
        process = Popen([GIT_EXECUTABLE, 'config', '--global', '--unset', 'https.proxy'])
        process.wait()

    def __generate_repo_url(self, repo_name):
        """ Generates repository URL for clone. Internal method.
        :param repo_name:
        :return:
        """
        repo_url = self.__git_address + '/' + repo_name + '.git'
        self.log.debug('__generate_repo_url(): generated repo url [{}].'.format(repo_url))
        return repo_url

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

    # todo: move to maven class???
    def build(self):
        """ Build repositories with turned on build setting. """
        # self.log.info('GitUtility: build():\n\trepositories: [{}]'.format(self.repos_list))
        self.log.debug('Starting build repositories...')

        for repository in self.repos_list:
            repo_key = CONFIG_KEY_REPO_BUILD.format(repository.replace('/', '.'))
            if self.config.contains_key(repo_key) and self.config.get(repo_key):
                self.__repo_build(repository)

    def __repo_clone(self, repo_name):
        """
        Clones repo by specified url to local location. Internal method.
        :param repo_name:
        """
        self.log.info('Cloning repository [{}].'.format(repo_name))
        # generating repo url and path
        repo_url = self.__generate_repo_url(repo_name)
        repo_path = self.location + '/' + repo_name[:repo_name.find('/')]
        self.log.debug('__repo_clone(): generated repo path [{}].'.format(repo_path))
        # check target dir and create it, if necessary
        if not os.path.exists(repo_path):
            self.log.info("Repo path [{}] doesn't exist. Trying to create it...".format(repo_path))
            try:
                os.makedirs(repo_path)
            except OSError as exc:  # guard against race condition
                if exc.errno != errno.EEXIST:
                    raise
        # cloning specified repository
        try:
            process = Popen([GIT_EXECUTABLE, 'clone', repo_url], cwd=repo_path)
            process.wait()
            # self.log.debug('Clone for [{}] finished.'.format(repo_url))  # <- too much output
        except AttributeError as se:
            self.log.error('Error cloning repo [{}]! {}'.format(repo_url, se))

    def __repo_update(self, repo_name):
        """
        Pull repository and run gc() on it.
        :param repo_name:
        :return:
        """
        # todo: BUG!!! if remote pass has been changed -> it can't be updated (delete folder and clone!)
        repo_path = self.location + '/' + repo_name
        self.log.info('Updating repository [{}].'.format(repo_path))
        try:
            process = Popen([GIT_EXECUTABLE, 'pull'], cwd=repo_path)
            process.wait()
            process = Popen([GIT_EXECUTABLE, 'gc'], cwd=repo_path)
            process.wait()
        except AttributeError as se:
            self.log.error('Error updating repo [{}]! {}'.format(repo_path, se))

    def __repo_build(self, repo_name):
        """
        Build specified repository, if specified - download sources/javadoc for dependencies.
        :param repo_name:
        :return:
        """
        repo_path = self.location + '/' + repo_name
        self.log.info('Building repository [{}].'.format(repo_path))
        # todo: build code was here...

    def __is_repo_exists(self, repo_name):
        repo_path = self.location + '/' + repo_name
        check_result = os.path.exists(repo_path)
        # too much output
        # self.log.debug('__is_repo_exists(): checking existence of [{}], result [{}].'.format(repo_name, check_result))
        return check_result

    def process_repositories(self, repo_function=REPO_FUNCTION_CLONE):
        self.log.debug('process_repositories() is working.')
        # self.log.info('GitUtility: processing repositories with [{}]:\n[{}]'
        #              .format(repo_function, self.repos_list))  # <- too much output
        # get proxies from config
        http_proxy = self.config.get(consts.CONFIG_KEY_PROXY_HTTP, '')
        https_proxy = self.config.get(consts.CONFIG_KEY_PROXY_HTTPS, '')
        # set proxies (http/https)
        git_set_global_proxy(http_proxy, https_proxy)
        # perform processing
        for repository in self.repos_list:
            # if option is 'clone' or repository doesn't exist - clone it
            if REPO_FUNCTION_CLONE == repo_function or not self.__is_repo_exists(repository):
                self.__repo_clone(repository)
            # option 'update'
            elif REPO_FUNCTION_UPDATE == repo_function:
                self.__repo_update(repository)
            # unknown option
            else:
                raise AttributeError('Invalid option for repository processing [{}]!'.format(repo_function))
        # clean git proxies, if any was set
        if http_proxy or https_proxy:
            git_clean_global_proxy()

class GitException(Exception):
    """GIT Exception, used if something is wrong with/in GIT interaction."""


if __name__ == '__main__':
    pass
    # log = setup_logging(logger_name='pygit')
    # build_repos_list(None)
