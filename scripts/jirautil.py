#!/usr/bin/env python
# coding=utf-8

"""
    Utility to work with JIRA - put labels, generate reports, etc.
    Contains just a starter for utility.

    Created: Gusev Dmitrii, 04.04.2017
    Modified: Gusev Dmitrii, 24.12.2017
"""

import argparse
import logging
from pylib.jira_utility_extended import JiraUtilityExtended, JIRA_OPTIONS
from pyutilities import setup_logging
import pylib.common_constants as myconst
from pyutilities.pyutilities.configuration import Configuration


def prepare_arg_parser():
    """
    Prepare arguments and return cmd line parser.
    :return: prepared cmd line parser
    """
    # create arguments parser
    parser = argparse.ArgumentParser(description='JIRA Utility.')

    # config file for loading, optional
    parser.add_argument('--config', dest=myconst.CONFIG_KEY_CFG_FILE, action='store',
                        default=myconst.CONST_JIRA_CONFIG_FILE, help='YAML configuration file/path')
    # jira address and user, proxy server (http/https) optional
    parser.add_argument('-a', '--address', dest=myconst.CONFIG_KEY_JIRA_ADDRESS, action='store', help='JIRA address')
    parser.add_argument('-u', '--user', dest=myconst.CONFIG_KEY_JIRA_USER, action='store', help='JIRA user')
    parser.add_argument('--proxy.http', dest=myconst.CONFIG_KEY_PROXY_HTTP, action='store', help='HTTP proxy')
    parser.add_argument('--proxy.https', dest=myconst.CONFIG_KEY_PROXY_HTTPS, action='store', help='HTTPS proxy')
    # mandatory cmd line parameter(s): jira password, option to execute
    parser.add_argument('-p', '--pass', dest=myconst.CONFIG_KEY_JIRA_PASS, action='store', required=True, help='JIRA password')
    # possible options (actions) to be done by this script
    parser.add_argument('--option', dest=myconst.CONFIG_KEY_OPTION, action='store', required=True,
                        choices=JIRA_OPTIONS, help='Type of option/action')
    # sprint name, optional
    parser.add_argument('--sprint', dest=myconst.CONFIG_KEY_SPRINT, action='store', help='JIRA Sprint name')
    # team name, optional
    parser.add_argument('--team', dest=myconst.CONFIG_KEY_TEAM_NAME, action='store', choices=myconst.CONST_TEAMS_LIST,
                        help='Team for report generating')
    # some optional settings for detailed configuration: report output file, days back for 'Closed issues' report,
    # create simple report (flag), show label column in a report (flag)
    parser.add_argument('--file', dest=myconst.CONFIG_KEY_OUTPUT_FILE, action='store', default=None,
                        help='Output file name for report')
    parser.add_argument('--daysBack', dest=myconst.CONFIG_KEY_DAYS_BACK, action='store', default=0,
                        help='Days back for closed issues report')
    parser.add_argument('--simpleReport', dest=myconst.CONFIG_KEY_USE_SIMPLE_REPORT, action='store_true',
                        help='Generate simple report (by default - detailed)')
    parser.add_argument('--showLabel', dest=myconst.CONFIG_KEY_SHOW_LABEL_COLUMN, action='store_true',
                        help='Show "Label" column in a report')

    # return prepared parser
    return parser


def jira_utility_start():
    setup_logging()
    # get module-level logger
    log = logging.getLogger(myconst.LOGGER_NAME_JIRAUTIL)
    log.info("Starting JIRA Utility...")
    # parse cmd line arguments
    cmd_line_args = prepare_arg_parser().parse_args()
    # create configuration
    config = Configuration(path_to_config=getattr(cmd_line_args, myconst.CONFIG_KEY_CFG_FILE),
                           dict_to_merge=vars(cmd_line_args), is_override_config=True, is_merge_env=False)
    log.debug("Loaded Configuration:\n\t{}".format(config.config_dict))

    # init jira instance
    jira = JiraUtilityExtended(config)
    jira.execute_option(config.get(myconst.CONFIG_KEY_OPTION))


if __name__ == '__main__':
    jira_utility_start()
