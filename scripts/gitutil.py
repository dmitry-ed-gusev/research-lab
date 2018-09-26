#!/usr/bin/env python
# coding=utf-8

"""
    GIT utility, simplifies work with many repositories. By default - update and build repositories
    specified in config file. Has some config parameters for fine tuning.
    Created: Gusev Dmitrii, 03.04.2017
    Modified: Gusev Dmitrii, 20.02.2018
"""

import argparse
import logging
import pylib.common_constants as myconst
from pyutilities.config import Configuration
from pylib.git_utility import GitUtility, REPO_FUNCTION_CLONE, REPO_FUNCTION_UPDATE
from pyutilities.utils import setup_logging


def prepare_arg_parser():
    """
    Prepare and return cmd line parser.
    :return: prepared cmd line parser
    """
    # create arguments parser
    parser = argparse.ArgumentParser(description='Git/Stash Utility.')
    # config file for loading, optional
    parser.add_argument('--config', dest=myconst.CONFIG_KEY_CFG_FILE, action='store',
                        default=myconst.CONST_GIT_CONFIG_FILE, help='YAML configuration file/path')
    # proxy settings, optional
    parser.add_argument('--proxy.http', dest=myconst.CONFIG_KEY_PROXY_HTTP, action='store', help='HTTP proxy')
    parser.add_argument('--proxy.https', dest=myconst.CONFIG_KEY_PROXY_HTTPS, action='store', help='HTTPS proxy')
    # todo: add --noproxy key - ignore proxy settings in config file
    # todo: add --nosettings key - ignore special settings in config file
    # stash password, mandatory
    parser.add_argument('--pass', dest=myconst.CONFIG_KEY_STASH_PASS, action='store',
                        required=True, help='JIRA password (mandatory)')
    # additional parameters
    parser.add_argument('--clone', dest=myconst.CONFIG_KEY_GIT_CLONE, action='store_true',
                        help='Clone repositories instead of update them')
    parser.add_argument('--nobuild', dest=myconst.CONFIG_KEY_MVN_BUILD_OFF, action='store_true',
                        help='Switch Maven build off (options --javadoc/--sources will take no effect in this case)')
    parser.add_argument('--javadoc', dest=myconst.CONFIG_KEY_MVN_JAVADOC, action='store_true',
                        help='Download javadoc packages for Maven dependencies')
    parser.add_argument('--sources', dest=myconst.CONFIG_KEY_MVN_SOURCES, action='store_true',
                        help='Download sources for Maven dependencies')
    return parser


def git_utility_start():
    setup_logging()
    # get module-level logger
    log = logging.getLogger(myconst.LOGGER_NAME_GITUTIL)
    log.info("Starting GIT Utility...")
    # parse cmd line arguments
    cmd_line_args = prepare_arg_parser().parse_args()
    # create configuration
    config = Configuration(path_to_config=getattr(cmd_line_args, myconst.CONFIG_KEY_CFG_FILE),
                           dict_to_merge=vars(cmd_line_args), is_override_config=True, is_merge_env=False)
    log.debug("Loaded Configuration:\n\t{}".format(config.config_dict))

    # init GitUtility class
    git = GitUtility(config)

    # update or clone repositories - depending on settings/options
    if config.get(myconst.CONFIG_KEY_GIT_CLONE, default=False):
        git.process_repositories(repo_function=REPO_FUNCTION_CLONE)  # clone by option
    else:
        git.process_repositories(repo_function=REPO_FUNCTION_UPDATE)  # update by option
    # build repositories, if not switched off
    if not config.get(myconst.CONFIG_KEY_MVN_BUILD_OFF, default=False):
        git.build()


if __name__ == '__main__':
    git_utility_start()
