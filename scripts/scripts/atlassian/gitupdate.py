#!/usr/bin/env python
# coding=utf-8

"""
    GIT utility, simplifies work with many repositories. By default - update and build repositories
    specified in config file. Has some config parameters for fine tuning.

    Created:  Gusev Dmitrii, 03.04.2017
    Modified: Gusev Dmitrii, 13.02.2019
"""

import argparse
import logging
import pylib.consts as consts
import pylib.git_utility as gitu
from pyutilities.config import Configuration
from pylib.git_utility import GitUtility, REPO_FUNCTION_CLONE, REPO_FUNCTION_UPDATE
from pyutilities.utils import setup_logging

DEFAULT_GIT_CONFIG = 'configs/git.yml'
CONFIG_KEY_MVN_BUILD_OFF = 'no-build'
CONFIG_KEY_GIT_CLONE = 'git-clone'
CONFIG_KEY_GIT_UPDATE_OFF = 'no-update'


def prepare_arg_parser():
    """
    Prepare and return cmd line parser.
    :return: prepared cmd line parser
    """
    # create arguments parser
    parser = argparse.ArgumentParser(description='Git/Stash Utility.')
    # config file for loading, optional
    parser.add_argument('--config', dest=consts.CONFIG_KEY_CFG_FILE, action='store',
                        default=DEFAULT_GIT_CONFIG, help='YAML configuration file/path')
    # proxy settings, optional
    parser.add_argument('--proxy.http', dest=consts.CONFIG_KEY_PROXY_HTTP, action='store', help='HTTP proxy')
    parser.add_argument('--proxy.https', dest=consts.CONFIG_KEY_PROXY_HTTPS, action='store', help='HTTPS proxy')
    # stash password, mandatory
    parser.add_argument('--pass', dest=gitu.CONFIG_KEY_GIT_PASS, action='store',
                        required=True, help='JIRA password (mandatory)')
    # additional parameters
    # todo: review necessarity of --clone option
    parser.add_argument('--clone', dest=CONFIG_KEY_GIT_CLONE, action='store_true',
                        help='Clone repositories instead of update them')
    parser.add_argument('--nobuild', dest=CONFIG_KEY_MVN_BUILD_OFF, action='store_true',
                        help='Switch Maven build off (options --javadoc/--sources will take no effect in this case)')
    parser.add_argument('--noupdate', dest=CONFIG_KEY_GIT_UPDATE_OFF, action='store_true',
                        help='Switch GIT to skip repositories update')
    parser.add_argument('--javadoc', dest=gitu.CONFIG_KEY_MVN_JAVADOC, action='store_true',
                        help='Download javadoc packages for Maven dependencies')
    parser.add_argument('--sources', dest=gitu.CONFIG_KEY_MVN_SOURCES, action='store_true',
                        help='Download sources for Maven dependencies')
    return parser


def git_utility_start():
    setup_logging()
    # get module-level logger
    log = logging.getLogger('gitutil')
    log.info("Starting GIT Utility...")
    # parse cmd line arguments
    cmd_line_args = prepare_arg_parser().parse_args()
    # create configuration
    config = Configuration(path_to_config=getattr(cmd_line_args, consts.CONFIG_KEY_CFG_FILE),
                           dict_to_merge=vars(cmd_line_args), is_override_config=True, is_merge_env=False)
    log.debug("Loaded Configuration:\n\t{}".format(config.config_dict))

    # init GitUtility class
    git = GitUtility(config)

    # GIT - update/clone repositories, if not switched off
    if not config.get(CONFIG_KEY_GIT_UPDATE_OFF, default=False):
        # update or clone repositories - depending on settings/options
        if config.get(CONFIG_KEY_GIT_CLONE, default=False):
            git.process_repositories(repo_function=REPO_FUNCTION_CLONE)  # clone by option
        else:
            git.process_repositories(repo_function=REPO_FUNCTION_UPDATE)  # update by option

    # build repositories, if not switched off
    if not config.get(CONFIG_KEY_MVN_BUILD_OFF, default=False):
        git.build()


if __name__ == '__main__':
    git_utility_start()
