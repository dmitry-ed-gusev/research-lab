#!/usr/bin/env python
# coding=utf-8

"""
    Initializing configuration for JIRA Utility.
    Load config from default file [configs/jira.yml], any parameter from config file
    may be overwritten by cmd line argument. You may change config file, but can't avoid it
    loading.
"""

import argparse
from configuration import Configuration, ConfigError
import jira_constants as jconst

# options (one-by-one)
OPTION_CLOSED = 'printClosed'
OPTION_SPRINT_ISSUES = 'sprintIssues'
OPTION_ADD_COMPONENT_TO_SPRINT_ISSUES = 'addComponent'
OPTION_ADD_LABEL_TO_SPRINT_ISSUES = 'addLabel'
OPTION_CURRENT_TEAM_STATUS = 'teamStatus'
OPTION_DEBUG = 'debug'
OPTION_CONFIG = ''
# options list - all together
OPTIONS = (OPTION_CLOSED, OPTION_SPRINT_ISSUES, OPTION_ADD_COMPONENT_TO_SPRINT_ISSUES,
           OPTION_ADD_LABEL_TO_SPRINT_ISSUES, OPTION_CURRENT_TEAM_STATUS, OPTION_DEBUG)


def prepare_arg_parser():
    """
    Prepare and return cmd line parser.
    :return: prepared cmd line parser
    """
    # create arguments parser
    parser = argparse.ArgumentParser(description='JIRA Utility.')

    # mandatory cmd line parameters: jira password, option
    parser.add_argument('-p', '--pass', dest='jira_password', action='store', required=True, help='JIRA password')
    # possible options (actions)
    # todo: option should be required. switched off for development/debug
    parser.add_argument('-o', '--option', dest='option', action='store', required=False, choices=OPTIONS, help='Type of option/action')

    # config file for loading, optional
    parser.add_argument('--config', dest='config', action='store',
                        default='configs/jira.yml', help='Utility YAML configuration file/path')
    # jira address and user, optional
    parser.add_argument('-a', '--address', dest='jira.address', action='store', help='JIRA address')
    parser.add_argument('-u', '--user', dest='jira_user', action='store', help='JIRA user')
    # other optional arguments (self explanatory)
    parser.add_argument('--project', dest='project', action='store', help='Project name')
    parser.add_argument('--sprint', dest='sprint', action='store', help='Sprint name')

    # todo: remove component and label -> moved to config
    # parser.add_argument('--component', dest='component', action='store', help='Jira component name')
    # parser.add_argument('--label', dest='label', action='store', help='Jira label name')

    parser.add_argument('--file', dest='out_file', action='store', default=None, help='Output file name for report')
    parser.add_argument('--daysBack', dest='days_back', action='store', default=0, help='Days back for closed issues report')
    parser.add_argument('--simpleReport', dest='simple_report', action='store_true', help='Generate simple report (default - detailed)')
    parser.add_argument('--showLabel', dest='show_label', action='store_true', help='Show "Label" column in a report')
    # team: ada - current Mantis team, ada-all - all members of Ada team (with left people), bmtef - Lynx team
    # in BMTEF project, nova - Nova team, new team for Mantis project)
    parser.add_argument('--team', dest='team', action='store',
                        choices=('ada', 'ada_all', 'nova', 'nova_all', 'bmtef', 'bmtef_all'),
                        help='Team for report generating')
    # todo: add config dir/file parameter
    return parser


def init_jira_utility_config():
    print "init_jira_utility_config() is working."
    # prepare cmd line parser and parse cmd line

    zzz = argparse.Namespace()
    args = prepare_arg_parser().parse_args(namespace=zzz)

    # load configuration from specified or default config, don't merge with environment
    config = Configuration(args.config, is_merge_env=False)
    # set password config entry
    config.set(jconst.CONFIG_KEY_PASS, args.jira_password)

    # add all cmd line arguments (they override confgi file parameters)
    myargs = vars(zzz)
    print myargs

    # return created config
    return config
