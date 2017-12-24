#!/usr/bin/env python
# coding=utf-8

"""
 Go through list of repositories and 'git pull' + 'mvn clean install' on them.
 Created: Gusev Dmitrii, 03.04.2017
 Modified: Gusev Dmitrii, 25.04.2017
"""

import os
import sys
import argparse
import logging
import pylib.common_constants as myconst
from pylib.configuration import Configuration
from pylib.pyutilities import setup_logging


def prepare_arg_parser():
    """
    Prepare and return cmd line parser.
    :return: prepared cmd line parser
    """
    # create arguments parser
    parser = argparse.ArgumentParser(description='GIT Utility.')
    # config file for loading, optional
    parser.add_argument('--config', dest=myconst.CONFIG_KEY_CFG_FILE, action='store',
                        default=myconst.CONST_GIT_CONFIG_FILE, help='YAML configuration file/path')
    # parser.add_argument('--no-git-update', dest='git_update_off', action='store_true', help='Skip updating git repos')
    # parser.add_argument('--no-mvn-build', dest='mvn_build_off', action='store_true', help='Skip Maven build')
    # parser.add_argument('--javadoc', dest='javadoc', action='store_true', help='Download Maven dependencies javadoc')
    # parser.add_argument('--sources', dest='sources', action='store_true', help='Download Maven dependencies sources')
    return parser


def git_utility_start():
    setup_logging()
    # get module-level logger
    log = logging.getLogger('gitutil')
    log.info("Starting GIT Utility...")
    # parse cmd line arguments
    cmd_line_args = prepare_arg_parser().parse_args()
    # create configuration
    config = Configuration(path_to_config=getattr(cmd_line_args, myconst.CONFIG_KEY_CFG_FILE),
                           dict_to_merge=vars(cmd_line_args), is_override_config=True, is_merge_env=False)


if __name__ == '__main__':
    git_utility_start()



# # try to load config from specified directory
# if not args.config_dir or not args.config_dir.strip() \
#         or not os.path.isdir(args.config_dir) or not os.path.exists(args.config_dir):
#     print "Config dir [{}] is invalid!".format(args.config_dir)
#     sys.exit(1)
#
# # config dir is ok - loading
# config = conf.Configuration()
# config.load(args.config_dir)
#
# print "->", config.get('stash.address')
# sys.exit(123)
#
#
# # get repositories configs (paths)
# base_dir = config.get('repos_dir')
# # fix base projects dir (path ending)
# if not (base_dir.endswith("\\") or base_dir.endswith("/")):
#     base_dir += "/"
# # get repos list from config
# repos_list = config.get('repos')
# build_list = config.get('build_repos')
#
# # update all repos in list (git status/pull/gc), if not switched off
# if not args.git_update_off:
#     for repo in repos_list:
#         git_update(base_dir + repo)
# else:
#     print "\nUpdating repositories from GIT is turned OFF."
#
# # build all repos in build list
# if not args.mvn_build_off:
#     for repo in build_list:
#         mvn_build(base_dir + repo, args.javadoc, args.sources)
# else:
#     print "\nMaven build if turned OFF."
#
# print "\n{}\nRepositories processing has finished.".format(SEPARATOR)
