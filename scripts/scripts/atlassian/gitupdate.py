#!/usr/bin/env python
# coding=utf-8

"""
    GIT utility, simplifies work with many repositories. By default - update and build repositories
    specified in config file. Has some config parameters for fine tuning.

    Created:  Gusev Dmitrii, 03.04.2017
    Modified: Gusev Dmitrii, 22.05.2019
"""

import platform
import argparse
from pyutilities.config import Configuration
from pyutilities.pylog import init_logger, setup_logging, myself
from scripts.atlassian.atlassian_exception import AtlassianException

# config keys for switching off
CONFIG_KEY_MVN_BUILD_OFF = 'no-build'
CONFIG_KEY_MVN_JAVADOC_OFF = 'no-javadoc'
CONFIG_KEY_MVN_SOURCES_OFF = 'no-sources'

# CONFIG_KEY_GIT_CLONE = 'git-clone'
# CONFIG_KEY_GIT_UPDATE_OFF = 'no-update'

# utility configuration file key
CONFIG_KEY_CFG_FILE = "config.file"
# config file keys - for proxy
CONFIG_KEY_PROXY_HTTP = 'proxy.http'
CONFIG_KEY_PROXY_HTTPS = 'proxy.https'
CONFIG_KEY_GIT_PASS = 'stash.password'

# init module logger
log = init_logger('gitupdate')


def build_repos_list(repos_dict):
    """ Build list of repositories from provided dictionary.
        :param repos_dict:
        :return:
    """
    log.info(f'{myself()}() is working.')

    if not repos_dict or not isinstance(repos_dict, dict):  # fail-fast for empty/non-dictionary
        raise AtlassianException('Provided empty or non-dictionary instance!')

    repos_keys_list = repos_dict.keys()  # get only keys list
    repos_list = []
    for key in repos_keys_list:
        repos = repos_dict[key]
        for repo_name in repos:
            repos_list.append(''.join([key, '/', repo_name]))  # construct repo name with key and name

    return repos_list


def select_projects_location(locations_dict):
    """ Select local repositories/projects location, depending on underlying OS. Internal method.
    :return: location path from config
    """
    log.info(f'{myself()}() is working.')

    if not locations_dict or not isinstance(locations_dict, dict):  # fail-fast for empty/non-dictionary
        raise AtlassianException('Provided empty or non-dictionary instance!')

    if 'windows' in platform.system().lower():
        return locations_dict['win']
    elif 'linux' in platform.system().lower():
        return locations_dict['linux']
    else:
        return locations_dict['macos']


def prepare_arg_parser():
    """ Prepare and return cmd line parser.
        :return: prepared cmd line parser
    """
    # create arguments parser
    parser = argparse.ArgumentParser(description='Git/Stash Projects Update/Build Utility.')
    # config file for loading, mandatory
    parser.add_argument('--config', dest=CONFIG_KEY_CFG_FILE, action='store', help='YAML configuration file/path')
    # proxy settings, optional
    parser.add_argument('--proxy.http', dest=CONFIG_KEY_PROXY_HTTP, action='store', help='HTTP proxy')
    parser.add_argument('--proxy.https', dest=CONFIG_KEY_PROXY_HTTPS, action='store', help='HTTPS proxy')
    # stash password, mandatory
    parser.add_argument('--pass', dest=CONFIG_KEY_GIT_PASS, action='store',
                        required=True, help='JIRA password (mandatory)')
    # switches to turn off functionality: no build, no javadoc/sources update
    parser.add_argument('--nobuild', dest=CONFIG_KEY_MVN_BUILD_OFF, action='store_true',
                        help='Switch Maven build off')
    parser.add_argument('--nojavadoc', dest=CONFIG_KEY_MVN_JAVADOC_OFF, action='store_true',
                        help='Switch downloading javadoc off')
    parser.add_argument('--nosources', dest=CONFIG_KEY_MVN_SOURCES_OFF, action='store_true',
                        help='Switch downloading sources off')
    return parser


def git_utility_start():
    log = setup_logging(logger_name='gitutil')
    log.debug('Git Utility is starting...')

    # parse cmd line arguments and create Configuration
    cmd_line_args = prepare_arg_parser().parse_args()
    config = Configuration(path_to_config=getattr(cmd_line_args, CONFIG_KEY_CFG_FILE),
                           dict_to_merge=vars(cmd_line_args), is_override_config=True, is_merge_env=False)
    log.debug("Loaded Configuration:\n\t{}".format(config.config_dict))

    import os
    print('script dir ->', os.path.dirname(os.path.abspath(__file__)))
    print('working dir -> ', os.getcwd())

    # init GitUtility class
    # git = GitUtility(config)
    #
    # # GIT - update/clone repositories, if not switched off
    # if not config.get(CONFIG_KEY_GIT_UPDATE_OFF, default=False):
    #     # update or clone repositories - depending on settings/options
    #     if config.get(CONFIG_KEY_GIT_CLONE, default=False):
    #         git.process_repositories(repo_function=REPO_FUNCTION_CLONE)  # clone by option
    #     else:
    #         git.process_repositories(repo_function=REPO_FUNCTION_UPDATE)  # update by option
    #
    # # build repositories, if not switched off
    # if not config.get(CONFIG_KEY_MVN_BUILD_OFF, default=False):
    #     git.build()
    #


if __name__ == '__main__':
    git_utility_start()
