#!/usr/bin/env python
# coding=utf-8

"""
    Initializing configuration for JIRA Utility.
    Load config from default file [configs/jira.yml], any parameter from config file
    may be overwritten by cmd line argument. You may change config file, but can't avoid it
    loading.
"""

import argparse
from configuration import Configuration
from jira_utility_extended import JiraUtilityExtended, JIRA_OPTIONS
import common_constants as myconst
import _jira_constants as jconst




# todo: make method common - init config object with overriding by cmd line parameters
def init_jira_utility_config():
    print "init_jira_utility_config() is working."
    # get argparse namespace (will be filled with vars after parsing)
    # argparse_namespace = argparse.Namespace()
    # prepare cmd line parser and parse cmd line (put all in specified namespace)
    # args = prepare_arg_parser().parse_args(namespace=argparse_namespace)
    args = prepare_arg_parser().parse_args()
    # load configuration from specified or default config, don't merge with environment
    config = Configuration(getattr(args, myconst.CONFIG_KEY_CFG_FILE), is_merge_env=False)

    #print vars(argparse_namespace)
    print vars(args)
    print type(args)

    # add cmd line arguments to config (overwrite existing, if set value)
    #for key, value in vars(argparse_namespace).items():
    #    if value:
    #        config.set(key, value)

    # just a debug output
    print "Configuration: %s" % config.config_dict
    # return created config
    return config


init_jira_utility_config()
