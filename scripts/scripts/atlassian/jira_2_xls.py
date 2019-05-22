#!/usr/bin/env python
# coding=utf-8

"""
    Script for creating jira release tasks.

    Created:  Gusev Dmitrii, XX.06.2018
    Modified: Gusev Dmitrii, 19.02.2019
"""

import sys
import xlrd  # reading excel library
import xlwt  # writing excel library
import xlwings as xw  # read/write open excel files library
import logging
import string
import inspect
import argparse
import pylib.consts as consts
import pylib.jira_utility as jira
from datetime import datetime
from pylib.jira_utility import JiraUtility
from pylib.jira_issue import JiraIssue, JIRA_LINK_IMPLEMENTS
from pylib.jira_helpers import get_issue_key
from pyutilities.utils import setup_logging
from pyutilities.config import Configuration, ConfigurationXls

# todo: jira statuses link -> https://issues.merck.com/rest/api/2/project/KDM/statuses
# todo: jira fixVersion -> https://stackoverflow.com/questions/29634019/jira-python-how-do-you-update-the-fixversions-field

# handy utility for getting name of executing function from inside the function
myself = lambda: inspect.stack()[1][3]

# script default encoding
ENCODING = 'UTF8'
# keys for config dictionary
KEY_XLS_CONFIG = 'xls.config'
KEY_XLS_TASKS_SHEET = 'xls.tasks_sheet'
KEY_XLS_CONFIG_SHEET = 'xls.config_sheet'
# xls parameters
XLS_TASKS_SHEET_DEFAULT = 'tasks'
XLS_CONFIG_SHEET_DEFAULT = 'config'

# layout of xls file with JIRA tasks
XLS_COLUMN_EPIC = 0
XLS_COLUMN_STORY = 1
XLS_COLUMN_TASK = 2
XLS_COLUMN_SUMMARY = 3
XLS_COLUMN_ASSIGNEE = 5
XLS_COLUMN_DUEDATE = 6
XLS_COLUMN_STATE = 7
XLS_COLUMN_DESCRIPTION = 8

# xls layout map
XLS_LAYOUT = {XLS_COLUMN_EPIC, ""}

# log message template for debugging
LOG_IS_WORKING = '{}() is working.'

# setup logging for current script
# todo: add parameters to setup_logging() and let it return initialized logger (preserve current behaviour!)
setup_logging()
log = logging.getLogger('jira_release')
log.info('Starting JIRA Release utility...')

# dictionary: number -> letter
letters = dict(enumerate(string.ascii_uppercase, 0))
# print letters[XLS_COLUMN_STATE]
# sys.exit(777)

# issues keys/defaults
# KEY_PROJECT = 'project_key'
# KEY_ISSUE_TYPE = 'issue_type'
# KEY_ISSUE_SUMMARY = 'issue_summary'
# KEY_ISSUE_DESC = 'issue_desc'
# KEY_ISSUE_ASSIGNEE = 'issue_assignee'
# KEY_ISSUE_PRIORITY = 'issue_priority'
# KEY_ISSUE_FIX_VERSIONS = 'issue_fix_versions'
# KEY_ISSUE_EPIC = 'issue_epic_link'
# KEY_ISSUE_STORY = 'issue_story_link'
# configuration defaults
# xls_file = '../release_tasks/release_tasks_kdm_20.xlsx'
# xls_file_config_sheet = 'config'


def init_arg_parser():
    """ Initialize cmd line parser, parse arguments and return initialized cmd line parser.
        Mandatory params: jira password, xls file name, xls config sheet name (default provided), xls tasks sheet
        name (default provided). Optional parameter: jira user name.
        name in excel, config sheet name in excel.
        :return: prepared cmd line parser
    """
    log.debug(LOG_IS_WORKING.format(myself()))
    # create arguments parser
    parser = argparse.ArgumentParser(description='JIRA Release supporting utility.')
    # optional cmd line parameter: jira user
    parser.add_argument('-u', '--user', dest=jira.CONFIG_KEY_JIRA_USER, action='store', help='JIRA user')
    # mandatory cmd line parameters: jira password, xls config file name
    parser.add_argument('-p', '--pass', dest=jira.CONFIG_KEY_JIRA_PASS, action='store',
                        required=True, help='JIRA password')
    parser.add_argument('--xls_file', dest=KEY_XLS_CONFIG, action='store', required=True,
                        help='Excel file with list of JIRA issues')
    # mandatory cmd line parameters (with defaults provided): tasks and config sheets of xls config
    parser.add_argument('--xls_tasks_sheet', dest=KEY_XLS_TASKS_SHEET, action='store', default=XLS_TASKS_SHEET_DEFAULT,
                        help='Sheet name for tasks in Excel file with list of JIRA issues')
    parser.add_argument('--xls_config_sheet', dest=KEY_XLS_CONFIG_SHEET, action='store', default=XLS_CONFIG_SHEET_DEFAULT,
                        help='Sheet name for configuration (name=value) in Excel file with list of JIRA issues')
    # returning resulting object - cmd line parser
    return parser


def init_config():
    """ Initialize ConfigurationXls class instance from all sources (merge): cmd line, config file, xls file
    :return:
    """
    log.debug(LOG_IS_WORKING.format(myself()))
    # parse cmd line and get dict and parameters
    cmd_line_dict = vars(init_arg_parser().parse_args())
    path_to_xls = cmd_line_dict[KEY_XLS_CONFIG]
    config_sheet = cmd_line_dict[KEY_XLS_CONFIG_SHEET]
    # init configuration instance
    config_xls = ConfigurationXls(path_to_xls, config_sheet, dict_to_merge=cmd_line_dict,
                                  path_to_yaml=consts.DEFAULT_JIRA_CONFIG, is_override_config=True, is_merge_env=False)
    log.info("Loaded XLS  Configuration:\n\t{}".format(config_xls.config_dict))
    # return initialized configuration
    return config_xls


def update_tasks_from_jira(xls_config_file, xls_tasks_sheet, jira_obj, save_excel=False):
    """ Reads XLS config, check each task and write state back to excel. Check: existence of task, summary value.
    :return:
    """
    log.debug(LOG_IS_WORKING.format(myself()))
    log.info("Updating tasks from JIRA. XLS config [{}], tasks sheet [{}].".format(xls_config_file, xls_tasks_sheet))
    # loading (reading) xls workbook/sheet
    excel_book = xlrd.open_workbook(xls_config_file, encoding_override=ENCODING)
    excel_sheet = excel_book.sheet_by_name(xls_tasks_sheet)
    log.debug("Loaded xls config. Found [{}] row(s). Processing".format(excel_sheet.nrows))
    # open xls file with xlwings (for writing to open excel file)
    xlbook = xw.Book(xls_config_file)
    xltasks = xlbook.sheets[xls_tasks_sheet]

    # read all tasks keys
    for rownumber in range(excel_sheet.nrows):
        if rownumber == 0:  # skip first row (index = 0) - it's just a header
            continue
        # get key of currently processing task
        key = get_issue_key(excel_sheet.cell_value(rownumber, XLS_COLUMN_EPIC),
                            excel_sheet.cell_value(rownumber, XLS_COLUMN_STORY),
                            excel_sheet.cell_value(rownumber, XLS_COLUMN_TASK))
        if key:  # find issue by key and write it's status to excel file
            issue = jira_obj.issue(key)
            log.info("Found issue: [{}], status [{}].".format(key, issue.fields.status))

            # update state of issue
            state_cell = letters[XLS_COLUMN_STATE] + str(rownumber + 1)
            xltasks.range(state_cell).value = str(issue.fields.status)

            if issue.fields.duedate:  # calculate due date value for issue
                due_date = datetime.strptime(str(issue.fields.duedate), "%Y-%m-%d").strftime("%d/%m/%Y")
            else:
                due_date = None
            # update due date for issue
            duedate_cell = letters[XLS_COLUMN_DUEDATE] + str(rownumber + 1)
            xltasks.range(duedate_cell).value = due_date

            # update assignee for issue
            assignee_cell = letters[XLS_COLUMN_ASSIGNEE] + str(rownumber + 1)
            xltasks.range(assignee_cell).value = issue.fields.assignee.name

            # print 'found issue ->', issue.raw, ' status:', issue.fields
            # print '->', jira_obj.transitions(issue)
            # print 'cell # -> ', (letters[XLS_COLUMN_STATE] + str(rownumber + 1))
            # print issue.fields.assignee.name

        if save_excel:  # save excel after updating status of issues
            xlbook.save()


def update_tasks_to_jira(xls_config_file, xls_tasks_sheet, jira_obj):
    log.debug(LOG_IS_WORKING.format(myself()))
    log.info("Updating tasks to JIRA from XLS config [{}], tasks sheet [{}].".format(xls_config_file, xls_tasks_sheet))
    log.warn("Not implemented yet!")


def jira_release_start():
    """ Main procedure of jira release script. """

    log.debug(LOG_IS_WORKING.format(myself()))

    config = init_config()           # init cmd line and script config
    jira_util = JiraUtility(config)  # init jira instance
    jira_util.connect()              # connect to Jira and do jira tasks check
    jira_obj = jira_util.jira        # get jira instance
    log.info("Connected to Jira server.")

    # execute method for checking tasks state/status
    update_tasks_from_jira(config.get(KEY_XLS_CONFIG), config.get(KEY_XLS_TASKS_SHEET), jira_obj)

    sys.exit(333)

    issue_key = 'KDM-844'
    project_key = 'KDM'
    issue_type = 'Task'
    summary = 'Test task created by script'
    description = 'Test task description created by script'
    assignee = 'gusevdmi'
    priority = 'Major'
    # fix_versions = ['KDM 2.0', 'KDM 3.0']  # <- use versions for issue creating/update
    epic_link = 'KDM-741'  # <- KDM Release 2.0 Epic
    story_link = 'KDM-742'  # <- INIT KDM Release 2.0 Story
    # IMPLEMENTS_TYPE = "implements / must come before"
    story_points_field = 'customfield_10008'
    story_points = 10
    components = ''
    due_date = ''
    # linked_issues = ''
    # link_type = ''
    # status = 'In Progress'

    # dictionary for new issue
    # todo: move to jira issue class
    issue_dict = {
        'project':     {'key': project_key},
        'summary':     summary,
        'description': description,
        'issuetype':   {'name': issue_type},
        'assignee':    {'name': assignee},
        'priority':    {'name': priority},
        # 'status':      {'statusCategory': {'name': status}},
        'fixVersions': [{'name': 'KDM 2.0'}, {'name': 'KDM 3.0'}],
        story_points_field: story_points,
        'duedate':     '2018-11-02'
    }

    # tasks = read_xls_config()
    # create jira issue
    # issue = jira_obj.create_issue(fields=issue_dict)
    # get an issue by key
    # issue = jira_obj.issue(issue_key)
    print('->', issue.raw)
    # update issue
    issue_dict['summary'] = 'new summary from script 2'
    issue.update(fields=issue_dict)
    issue = jira_obj.issue(issue_key)
    print('->', issue.raw)

    # change status (transition issue)
    # jira_obj.transition_issue(issue, 'To Do')
    # put issue to epic
    jira_obj.add_issues_to_epic(epic_link, [issue_key])
    # link issue to story
    jira_obj.create_issue_link(JIRA_LINK_IMPLEMENTS, issue_key, story_link, comment=None)


if __name__ == '__main__':
    jira_release_start()
