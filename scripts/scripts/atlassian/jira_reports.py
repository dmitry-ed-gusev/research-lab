#!/usr/bin/env python
# coding=utf-8

"""
    Python ,odule for JIRA reports methods/procedures.

    Created:  Gusev Dmitrii, 01.02.2019
    Modified: Gusev Dmitrii, 12.02.2019
"""

import prettytable
import logging
from pyutilities.utils import setup_logging

# setup logging for current script
setup_logging()
log = logging.getLogger('jira_release')
log.info('Starting JIRA Release utility...')


def get_issues_report(issues, show_label=False, title=None):
    """Generate and return text report for list of jira issues.
    :param issues: list of issues
    :param show_label: show "Labels" column in a report, True by default
    :param title:
    :return: generated report
    """
    log.debug("get_issues_report() is working.")

    # create report header
    header_list = ['#', 'Issue', 'Type', 'SP']
    if show_label:
        header_list.append('Labels')
    header_list.extend(['Components', 'Summary', 'Status', 'Assignee'])
    report = prettytable.PrettyTable(header_list)

    report.align['Summary'] = "l"  # align columns (Summary = left)
    counter = 1  # add rows to report

    for issue in issues:
        # transform/process some columns values
        assignee = ('-' if not issue.fields.assignee else issue.fields.assignee)
        # no story points for: "Sub-task", "Test Suite"
        storypoints = (issue.fields.customfield_10008 if str(issue.fields.issuetype) not in ["Sub-task", "Test Suite"] else '-')
        components = ',\n '.join([component.name for component in issue.fields.components])
        status = str(issue.fields.status)

        if show_label:  # labels - is optional column
            labels = ',\n '.join(issue.fields.labels)
            report.add_row([counter, issue.key, issue.fields.issuetype, storypoints, labels, components,
                            issue.fields.summary, status, assignee])
        else:
            report.add_row([counter, issue.key, issue.fields.issuetype, storypoints, components,
                            issue.fields.summary, status, assignee])
        counter += 1

    if title:  # if specified - add title to issues report
        return title + '\n' + report.get_string()
    else:
        return report.get_string()
