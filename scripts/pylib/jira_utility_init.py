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
import jira_constants as jconst


def prepare_arg_parser():
    """
    Prepare arguments and return cmd line parser.
    :return: prepared cmd line parser
    """
    # create arguments parser
    parser = argparse.ArgumentParser(description='JIRA Utility.')

    # config file for loading, optional
    parser.add_argument('--config', dest=jconst.CONFIG_KEY_CFG_FILE, action='store',
                        default=jconst.CONST_CONFIG_FILE, help='YAML configuration file/path')
    # jira address and user, optional
    parser.add_argument('-a', '--address', dest=jconst.CONFIG_KEY_ADDRESS, action='store', help='JIRA address')
    parser.add_argument('-u', '--user', dest=jconst.CONFIG_KEY_USER, action='store', help='JIRA user')
    # mandatory cmd line parameter(s): jira password, option to execute
    parser.add_argument('-p', '--pass', dest=jconst.CONFIG_KEY_PASS, action='store', required=True, help='JIRA password')
    # possible options (actions) to be done by this script
    parser.add_argument('--option', dest=jconst.CONFIG_KEY_OPTION, action='store', required=True,
                        choices=JIRA_OPTIONS, help='Type of option/action')
    # sprint name, optional
    parser.add_argument('--sprint', dest=jconst.CONFIG_KEY_SPRINT, action='store', help='JIRA Sprint name')
    # team name, optional
    parser.add_argument('--team', dest=jconst.CONFIG_KEY_TEAM_NAME, action='store', choices=jconst.CONST_TEAMS_LIST,
                        help='Team for report generating')
    # some optional settings for detailed configuration: report output file, days back for 'Closed issues' report,
    # create simple report (flag), show label column in a report (flag)
    parser.add_argument('--file', dest=jconst.CONFIG_KEY_OUTPUT_FILE, action='store', default=None,
                        help='Output file name for report')
    parser.add_argument('--daysBack', dest=jconst.CONFIG_KEY_DAYS_BACK, action='store', default=0,
                        help='Days back for closed issues report')
    parser.add_argument('--simpleReport', dest=jconst.CONFIG_KEY_USE_SIMPLE_REPORT, action='store_true',
                        help='Generate simple report (by default - detailed)')
    parser.add_argument('--showLabel', dest=jconst.CONFIG_KEY_SHOW_LABEL_COLUMN, action='store_true',
                        help='Show "Label" column in a report')

    # return prepared parser
    return parser


def init_jira_utility_config():
    print "init_jira_utility_config() is working."
    # get argparse namespace (will be filled with vars after parsing)
    argparse_namespace = argparse.Namespace()
    # prepare cmd line parser and parse cmd line (put all in specified namespace)
    args = prepare_arg_parser().parse_args(namespace=argparse_namespace)
    # load configuration from specified or default config, don't merge with environment
    config = Configuration(getattr(args, jconst.CONFIG_KEY_CFG_FILE), is_merge_env=False)

    # add cmd line arguments to config (overwrite existing, if set value)
    for key, value in vars(argparse_namespace).items():
        if value:
            config.set(key, value)

    # just a debug output
    print "Configuration: %s" % config.config_dict
    # return created config
    return config
