#!/usr/bin/env python
# coding=utf-8

"""
 Utility to work with JIRA - put labels, generate reports, etc.
 See list of options.
 Created: Gusev Dmitrii, 04.04.2017
"""

import argparse
import codecs
from pylib.jiralib import JIRAUtility

# utility options
OPTION_CLOSED = 'printClosed'
OPTION_SPRINT_ISSUES = 'sprintIssues'
OPTIONS = (OPTION_CLOSED, OPTION_SPRINT_ISSUES)
# todo: cmd line arg to select whole/current team
# ADA team (all members\current members)
ALL_MEMBERS_TEAM = ('gogolev', 'hapii', 'lipkovic', 'kaplia', 'zhukv', 'andreevi', 'barzilov', 'kudriash', 'iushin', 'gorkoven', 'gusevdm')
TEAM = ('kaplia', 'zhukv', 'andreevi', 'barzilov', 'kudriash', 'iushin', 'gorkoven', 'gusevdm')


def prepare_arg_parser():
    """
    Prepare and return cmd line parser.
    :return: prepared cmd line parser
    """
    # create arguments parser
    parser = argparse.ArgumentParser(description='JIRA Utility.')
    # add arguments to parser (mandatory/optional)
    parser.add_argument('-a', '--address', dest='jira_address', action='store', required=True, help='JIRA address')
    parser.add_argument('-u', '--user', dest='user', action='store', required=True, help='JIRA user')
    parser.add_argument('-p', '--pass', dest='password', action='store', required=True, help='JIRA password')
    # options/actions (mandatory too)
    parser.add_argument('-o', '--option', dest='option', action='store', required=True, choices=OPTIONS, help='Type of option')
    # optional arguments
    parser.add_argument('--project', dest='project', action='store', help='Project name')
    parser.add_argument('--sprint', dest='sprint', action='store', help='Sprint name')
    parser.add_argument('--file', dest='out_file', action='store', default=None, help='Output file name')
    parser.add_argument('--daysBack', dest='days_back', action='store', default=0, help='Days back for closed issues report')
    parser.add_argument('--simpleReport', dest='simple_report', action='store_true', help='Generate simple or detailed (default) report')
    # todo: add config dir/file parameter
    return parser


def print_closed_report(days_back=0, out_file=None, simple_report=False, print_to_console=True):
    # todo: add pydoc
    # todo: add checks

    report = 'ADA Team closed issues report'
    if days_back > 0:
        report += ' (for last {} day(s))'.format(days_back)
    else:
        report += ' (for the whole time)'
    report += '\n\n'

    for user in TEAM:
        # search issues
        issues = jira.get_all_closed_issues_for_user(user, days_back)
        # add them to report
        report += 'Issues report for user [{}]. Closed count [{}].\n'.format(user, len(issues))
        if len(issues) > 0 and not simple_report:
            report += jira.get_issues_report(issues).get_string()
            report += '\n\n'

    # print to console
    if print_to_console:
        print '\n', report

    # out to file (with overwriting)
    if out_file and out_file.strip():
        # with open(out_file, 'w') as out:
        with codecs.open(out_file, 'w', 'utf-8') as out:
            out.write(report)


def print_all_sprint_issues():
    # todo: implementation/pydoc/checks/tests
    # issues = jira.get_all_sprint_issues(sprint_name)
    # print "Found [{}] issues.".format(len(issues))
    # generate report and print it
    # print JIRAUtility.get_issues_report(issues)
    print 'Not implemented yet...'


# ===========================================
print 'JIRA Utility starting...'
# parse cmd line parameters
parser = prepare_arg_parser()
args = parser.parse_args()

# create JIRA object and connect to JIRA
jira = JIRAUtility(args.jira_address, args.user, args.password)
jira.connect()

# process selected option/action
if args.option == OPTION_CLOSED:
    print_closed_report(args.days_back, args.out_file, args.simple_report)
elif args.option == OPTION_SPRINT_ISSUES:
    print 'Not implemented yet...'


# add team label to all current sprint issues and print report
# jira.add_label_to_sprint_issues(sprint_name)
# print JIRAUtility.get_issues_report(jira.get_all_sprint_issues(sprint_name))
# issue = jira.get_issue('BMA-1216')
# component = jira.get_component('mantis-capability')
# print 'component ->', component
# components = issue.fields.components
# print 'components ->', components
# if component in components:
#     print 'ok'
# else:
#     print 'not present'
# jira.print_raw_issue(issue)
# component = jira.get_component('mantis-capability')
# print 'Found: ', component
# print issue.raw
# for component in issue.fields.components:
#    print component.id, '->', component.name, '->', component.self
