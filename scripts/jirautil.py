#!/usr/bin/env python
# coding=utf-8

"""
 Utility to work with JIRA - put labels, generate reports, etc.
 See list of options.
 Created: Gusev Dmitrii, 04.04.2017
"""

# todo: move team members lists to config file
# todo: implement current team status (in progress tasks)

import argparse
import codecs
from pylib.jiralib import JiraUtility, JiraException

# utility options (one-by-one)
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
# teams members (all members\current members)
#ADA_TEAM = ('kaplia', 'zhukv', 'andreevi', 'barzilov', 'kudriash', 'iushin', 'gorkoven', 'gusevdm')
#ADA_TEAM_ALL = ('gogolev', 'hapii', 'lipkovic', 'kaplia', 'zhukv', 'andreevi', 'barzilov', 'kudriash', 'iushin', 'gorkoven', 'gusevdm')
#NOVA_TEAM = ('kobiakov', 'listvin', 'sokolose', 'garadagl', 'kuchin')
#BMTEF_TEAM = ('iablokov', 'sitnikod', 'trukhano', 'gorodilo')


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
    parser.add_argument('-o', '--option', dest='option', action='store', required=True, choices=OPTIONS, help='Type of option/action')
    # optional arguments
    parser.add_argument('--project', dest='project', action='store', help='Project name')
    parser.add_argument('--sprint', dest='sprint', action='store', help='Sprint name')
    parser.add_argument('--component', dest='component', action='store', help='Jira component name')
    parser.add_argument('--label', dest='label', action='store', help='Jira label name')
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


def jira_connect():
    """ Utility method for "lazy" JIRA connecting (after checks in functions). """
    global jira
    if not jira:  # if not connected yet
        # create JIRA object and connect to JIRA
        jira = JiraUtility(args.jira_address, args.user, args.password)
        jira.connect()


def team_select():
    # todo: implement team selection/pydoc/tests
    if args.team == 'ada':
        return ADA_TEAM
    elif args.team == 'ada-all':
        return ADA_TEAM_ALL
    elif args.team == 'bmtef':
        return BMTEF_TEAM
    elif args.team == 'nova':
        return NOVA_TEAM
    else:
        raise JiraException('Team not specified! Use --team <team> parameter.')


def print_closed_issues_report(days_back=0, out_file=None, simple_report=False, print_to_console=True):
    """
    Generate and print report "Closed issues by every team member."
    :param days_back: period of time (back in time) for which we need this report (days)
    :param out_file: file to print report to (if needed)
    :param simple_report: if true, only issues counts will be printed (default false)
    :param print_to_console: if true, print report to console (default true)
    """
    # preparing parameters
    team = team_select()  # select team, fail if not specified
    jira_connect()  # jira 'lazy' connect
    # generate report header
    report = 'ADA Team closed issues report'
    if days_back > 0:
        report += ' (for last {} day(s))'.format(days_back)
    else:
        report += ' (for the whole time)'
    report += '\n\n'

    #  generate report for each user of specified team
    for user in team:
        # search issues
        issues = jira.get_all_closed_issues_for_user(user, days_back)
        # add them to report
        report += 'Issues report for user [{}]. Closed count [{}].\n'.format(user, len(issues))
        if len(issues) > 0 and not simple_report:
            report += jira.get_issues_report(issues).get_string()
            report += '\n\n'

    # print report to console
    if print_to_console:
        print '\n', report

    # out report to file (with overwriting)
    if out_file and out_file.strip():
        # with open(out_file, 'w') as out:
        with codecs.open(out_file, 'w', 'utf-8') as out:
            out.write(report)


def print_current_status_report(out_file=None, print_to_console=True):
    # todo: pydoc
    # preparing parameters
    team = team_select()  # select team, fail if not specified
    jira_connect()  # jira 'lazy' connect
    # report header and body
    report = 'Current "In Progress" status'
    for user in team:
        issues = jira.get_current_status_for_user(user)
        report += 'Issues "In Progress" for user [{}], count [{}].\n'.format(user, len(issues))
        if len(issues) > 0:
            report += jira.get_issues_report(issues).get_string()
            report += '\n\n'
    # print report to console
    if print_to_console:
        print '\n', report
    # out report to file
    if out_file and out_file.strip():
        with codecs.open(out_file, 'w', 'utf-8') as out:
            out.write(report)


def print_sprint_issues_report(sprint_name, out_file=None, print_to_console=True):
    """
    Generate and print report "All named sprint issues."
    :param sprint_name: sprint for which we generate current report
    :param out_file: file to print report to (if needed)
    :param print_to_console: if true, print report to console (default true)
    """

    if not sprint_name or not sprint_name.strip():  # fast-fail
        raise JiraException('Empty sprint name!')

    jira_connect()  # jira 'lazy' connect

    # generate report header
    report = 'Issues report for {}.\n'.format(sprint_name)
    report += jira.get_issues_report(jira.get_all_sprint_issues(sprint_name)).get_string()

    # print report to console
    if print_to_console:
        print '\n', report

    # out report to file (with overwriting)
    if out_file and out_file.strip():
        # with open(out_file, 'w') as out:
        with codecs.open(out_file, 'w', 'utf-8') as out:
            out.write(report)


def add_component_to_sprint_issues(sprint_name, project_name, component_name):
    # todo: checks/pydoc
    jira_connect()  # jira 'lazy' connect
    # get all issues for mentioned sprint
    issues = jira.get_all_sprint_issues(sprint_name)
    # add component to all found issues
    jira.add_component_to_issues(issues, project_name, component_name)


def add_label_to_sprint_issues(sprint_name, label_name):
    # todo: checks/pydoc
    jira_connect()  # jira 'lazy' connect
    # get all issues for mentioned sprint
    issues = jira.get_all_sprint_issues(sprint_name)
    # add component to all found issues
    jira.add_label_to_issues(issues, label_name)


# ===========================================
print 'JIRA Utility starting...'

# prepare and parse cmd line
parser = prepare_arg_parser()
args = parser.parse_args()
# define jira instance
jira = None

# process selected option/action
if args.option == OPTION_CLOSED:  # print closed issues report
    print_closed_issues_report(args.days_back, args.out_file, args.simple_report)

elif args.option == OPTION_SPRINT_ISSUES:  # print all sprint issues report
    print_sprint_issues_report(args.sprint, args.out_file)

elif args.option == OPTION_ADD_COMPONENT_TO_SPRINT_ISSUES:  # add component to sprint issues
    add_component_to_sprint_issues(args.sprint, args.project, args.component)
    print_sprint_issues_report(args.sprint, args.out_file)

elif args.option == OPTION_ADD_LABEL_TO_SPRINT_ISSUES:  # add label to sprint issues
    add_label_to_sprint_issues(args.sprint, args.label)
    print_sprint_issues_report(args.sprint, args.out_file)

elif args.option == OPTION_CURRENT_TEAM_STATUS:  # print all "In Progress" issues for specified team
    print_current_status_report(args.out_file)

elif args.option == OPTION_DEBUG:  # debug option - for debug features, etc.
    # print jira.get_project_key('Mantis')
    print jira.get_component_by_name('Mantis', 'mantis-capability')

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
