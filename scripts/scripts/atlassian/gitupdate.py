#!/usr/bin/env python3
# coding=utf-8

"""
    GIT utility, simplifies work with many repositories. By default - update and build repositories
    specified in config file. Has some config parameters for fine tuning.

    Created:  Gusev Dmitrii, 03.04.2017
    Modified: Gusev Dmitrii, 30.05.2019

"""

import os
import errno
import platform
import argparse
import pyutilities.strings as string
import scripts.credentials as creds
from pyutilities.config import Configuration
from pyutilities.pylog import init_logger, setup_logging, myself
from pyutilities.pygit import PyGit
from pyutilities.pymaven import PyMaven
from scripts.atlassian.atlassian_exception import AtlassianException

# dry run key - no real actions will be done
CONFIG_KEY_DRY_RUN = 'dry-run'

# config keys for maven - switching off build/javadoc/sources, special settings
# todo: implement these settings!
CONFIG_KEY_MVN_BUILD_OFF = 'no-build'
CONFIG_KEY_MVN_JAVADOC_OFF = 'no-javadoc'
CONFIG_KEY_MVN_SOURCES_OFF = 'no-sources'
CONFIG_KEY_MVN_SETTINGS = 'mvn_settings'

# config key - utility configuration file
CONFIG_KEY_CFG_FILE = "config.file"
# config keys - for proxy
CONFIG_KEY_PROXY_HTTP = 'proxy.http'
CONFIG_KEY_PROXY_HTTPS = 'proxy.https'

# config keys - git server parameters
CONFIG_KEY_GIT_ADDRESS = "stash.address"
CONFIG_KEY_GIT_USER = 'stash.user'
CONFIG_KEY_GIT_PASS = 'stash.password'
CONFIG_KEY_GIT_CREDENTIALS = 'stash.creds'

# config file key - repositories list, local location
CONFIG_KEY_REPOS = 'repositories'
CONFIG_KEY_REPOS_BUILD = 'repositories.{}.build'
CONFIG_KEY_LOCATION = 'location'


# init module logger
log = init_logger('gitupdate')


def get_repos_list(repos_dict: dict):
    """ Build list of repositories from provided dictionary.
        :param repos_dict:
        :return:
    """
    log.debug(f'{myself()}() is working.')

    if not repos_dict or not isinstance(repos_dict, dict):  # fail-fast for empty/non-dictionary
        raise AtlassianException('Provided empty or non-dictionary instance!')

    repos_keys_list = repos_dict.keys()  # get only keys list
    repos_list = []
    for key in repos_keys_list:
        repos = repos_dict[key]
        for repo_name in repos:
            repos_list.append(''.join([key, '/', repo_name]))  # construct repo name with key and name
    # return generated list
    return repos_list


def get_projects_location(locations_dict: dict):
    """ Select local repositories/projects location, depending on underlying OS. Internal method.
    :return: location path from config
    """
    log.debug(f'{myself()}() is working.')

    if not locations_dict or not isinstance(locations_dict, dict):  # fail-fast for empty/non-dictionary
        raise AtlassianException('Provided empty or non-dictionary instance!')

    if 'windows' in platform.system().lower():
        return locations_dict['win']
    elif 'linux' in platform.system().lower():
        return locations_dict['linux']
    else:
        return locations_dict['macos']


def get_prepared_git_url(base_git_url: str, username: str, password: str):
    """ Prepage GIT url by adding username and password to it.
        :return:
    """
    log.debug(f'{myself()}() is working.')

    if string.is_str_empty(base_git_url) or string.is_str_empty(username) or string.is_str_empty(password):
        raise AtlassianException(f"Provided empty git url [{base_git_url}] or user [{username}] or pass!")

    # merge username and password
    user_pass = username + ':' + password + '@'
    # get http:// or https:// - url prefix
    url_prefix_index = base_git_url.find('//')
    url_prefix = base_git_url[:url_prefix_index + 2]
    # get url postfix
    url_postfix = base_git_url[url_prefix_index + 2:]
    # construct final url
    return url_prefix + user_pass + url_postfix


def get_project_folder(repo_name: str):
    """ Get sub-folder for a project (if project/repo name contains symbol '/'. """
    log.debug(f'{myself()}() is working.')

    if string.is_str_empty(repo_name):  # fail-fast
        raise AtlassianException(f"Provided empty repo name!")
    # process name and return part of it - till '/' symbol
    slash_index = repo_name.find('/')
    if slash_index >= 0:
        return repo_name[:slash_index]
    # return empty string in case no '/' symbol found
    return ''


def get_initialized_config(arg_parser: argparse.ArgumentParser):
    """ Return initialized (by cmd line, cfg file, creds file) Configuration instance.  """
    log.debug(f'{myself()}() is working.')

    # parse cmd line arguments and create Configuration
    cmd_line_args = arg_parser.parse_args()
    cmd_line_dict = vars(cmd_line_args)

    # get credentials from [credentials.py] module (if specified)
    creds_type = cmd_line_dict[CONFIG_KEY_GIT_CREDENTIALS]
    creds_dict = {}
    if creds_type == 'msd':
        creds_dict = {'stash.user': creds.credentials_msd_git[0], 'stash.password': creds.credentials_msd_git[1]}
    elif creds_type == 'own':
        creds_dict = {'stash.user': creds.credentials_my_git[0], 'stash.password': creds.credentials_my_git[1]}

    # init configuration class instance
    config = Configuration(path_to_config=getattr(cmd_line_args, CONFIG_KEY_CFG_FILE), is_override_config=True,
                           is_merge_env=False, dict_to_merge=[vars(cmd_line_args), creds_dict])
    log.info(f"Loaded Configuration:\n\t{config.config_dict}")
    return config


def get_git_params(config: Configuration):
    """ Return tuple (x, y, z) with git url, user, pass. """
    log.debug(f'{myself()}() is working.')

    # get git general parameters - url/user/pass
    git_address = config.get(CONFIG_KEY_GIT_ADDRESS)
    git_user = config.get(CONFIG_KEY_GIT_USER)
    git_pass = config.get(CONFIG_KEY_GIT_PASS)

    # check user/password and do fast-fail
    if string.is_str_empty(git_user) or string.is_str_empty(git_pass):
        raise AtlassianException('Provided invalid credentials (empty username or password)!')

    return git_address, git_user, git_pass


def prepare_arg_parser():
    """ Prepare and return cmd line parser.
        :return: prepared cmd line parser
    """
    log.debug(f'{myself()}() is working.')

    # create arguments parser
    parser = argparse.ArgumentParser(description='Git/Stash Projects Update/Build Utility.')

    # config file for loading, mandatory
    parser.add_argument('--config', dest=CONFIG_KEY_CFG_FILE, action='store',
                        required=True, help='YAML configuration file/path')

    # stash user/password, mandatory
    parser.add_argument('--user', dest=CONFIG_KEY_GIT_USER, action='store',
                        help='GIT user (mandatory)')
    parser.add_argument('--pass', dest=CONFIG_KEY_GIT_PASS, action='store',
                        help='GIT password (mandatory)')
    parser.add_argument('--creds', dest=CONFIG_KEY_GIT_CREDENTIALS, action='store',
                        choices=['msd', 'own'], help='GIT credentials from file (values -> msd, own)')

    # proxy settings, optional
    parser.add_argument('--proxy.http', dest=CONFIG_KEY_PROXY_HTTP, action='store', help='HTTP proxy')
    parser.add_argument('--proxy.https', dest=CONFIG_KEY_PROXY_HTTPS, action='store', help='HTTPS proxy')

    # switches to turn off functionality: no build, no javadoc/sources update (optional)
    parser.add_argument('--nobuild', dest=CONFIG_KEY_MVN_BUILD_OFF, action='store_true',
                        help='Switch Maven build off')
    parser.add_argument('--nojavadoc', dest=CONFIG_KEY_MVN_JAVADOC_OFF, action='store_true',
                        help='Switch downloading javadoc off')
    parser.add_argument('--nosources', dest=CONFIG_KEY_MVN_SOURCES_OFF, action='store_true',
                        help='Switch downloading sources off')

    # option for dry run - no real actions will be done
    parser.add_argument('--dry', dest=CONFIG_KEY_DRY_RUN, action='store_true',
                        help='Turn on DRY RUN mode - no action will be done')

    return parser


def git_process_repositories(repos_list: list, projects_location: str, git: PyGit, dry_run: bool = False):
    log.debug(f'{myself()}() is working. DRY RUN MODE [{dry_run}]')

    for repo in repos_list:  # processing repositories - clone/update

        if dry_run:
            log.warning('Dry run mode is ON!')

        # decide - will we clone (no directory exists) or update/pull (directory exists) repository
        repository_location = projects_location + '/' + get_project_folder(repo)  # repository user/type folder
        repository_location_full = projects_location + '/' + repo  # full repository path

        if os.path.exists(repository_location_full):  # pull (update) repository
            log.info(f"Pull repository [{repo}], repo full location [{repository_location_full}].")
            # pull/update repository
            if not dry_run:
                git.pull(repo, repository_location_full)
        else:  # clone repository
            log.info(f"Clone repository [{repo}], repo location [{repository_location}].")
            # check target project directory (locally) and create it, if necessary
            if not os.path.exists(repository_location):
                log.info("Repo path [{}] doesn't exist. Trying to create it...".format(repository_location))
                try:
                    if not dry_run:
                        os.makedirs(repository_location)
                except OSError as exc:  # guard against race condition
                    if exc.errno != errno.EEXIST:
                        raise
            # clone repository
            if not dry_run:
                git.clone(repo, repository_location)  # clone repository

        # execute gc() for each repository
        if not dry_run:
            git.gc(repo, repository_location_full)


def maven_process_repositories():
    log.debug(f'{myself()}() is working.')

    # execute maven tasks for repository
    #build_repo = config.get[CONFIG_KEY_REPOS_BUILD.format(repo.replace('/', '.'))]


def git_utility_start():
    log = setup_logging(logger_name='gitutil')

    # some tech output (just for debug/troubleshuting purposes)
    script = os.path.abspath(__file__)
    log.info(f'Git Utility is starting.\n\tScript name: [{script}]\n\tScript dir: [{os.path.dirname(script)}]'
             f'\n\tWork dir: [{os.getcwd()}]')

    # parse cmd line and init Configuration
    config = get_initialized_config(prepare_arg_parser())

    # get git parameters
    (address, user, password) = get_git_params(config)
    # init PyGit class
    git = PyGit(git_url=get_prepared_git_url(address, user, password))

    # get maven external settings
    mvn_settings = None
    if config.contains_key(CONFIG_KEY_MVN_SETTINGS):
        mvn_settings = config.get(CONFIG_KEY_MVN_SETTINGS)
    # init PyMaven class
    maven = PyMaven(mvn_settings=mvn_settings)

    # get list of repositories from config
    repos_list = get_repos_list(config.get(CONFIG_KEY_REPOS))
    log.info(f"Loaded repos list: {repos_list}")
    # get location for projects from config
    projects_location = get_projects_location(config.get(CONFIG_KEY_LOCATION))
    log.info(f"Loaded projects location: {projects_location}")

    # get dry run value
    if config.contains_key(CONFIG_KEY_DRY_RUN) and config.get(CONFIG_KEY_DRY_RUN):
        dry_run = True
    else:
        dry_run = False

    # process repositories clone/pull
    git_process_repositories(repos_list, projects_location, git, dry_run)


if __name__ == '__main__':
    git_utility_start()
