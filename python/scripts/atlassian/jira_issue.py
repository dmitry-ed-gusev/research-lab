#!/usr/bin/env python
# coding=utf-8

"""
    Jira issue class. Represents domain object - one issue.

    Some technical notes:
        * project key -> symbolic key from jira, should exist in Jira
        * summary     -> simple text (issue title)
        * description -> large (possibly) text for issue
        * issuetype   -> Epic/Story/Task/Sub-task
        * assignee    -> to whom issue is assigned to (user should exist in Jira)
        * priority    -> Trivial/Minor/Major/Critical/Blocker
        * fixVersions -> list of name:value versions for issue (versions should exist in Jira)
        * customfield_10008 -> story points value for issue (integer value)
        * duedate           -> due date for issue, format: yyyy-MM-dd

    Created:  Dmitrii Gusev, 19.11.2018
    Modified: Dmitrii Gusev, 15.04.2019
"""

import logging

# useful constants
JIRA_LINK_IMPLEMENTS = 'Implementation'


class JiraIssue(object):
    """ This class represents one Jira issue. """

    @property
    def issue_dict(self):
        return self.__issue_dict

    def __init__(self):
        self.log = logging.getLogger(__name__)
        self.log.addHandler(logging.NullHandler())
        self.log.debug("Created Jira issue instance.")
        # internal state
        self.__issue_dict = {
            'project':           {'key': None},     # project key
            'summary':           None,              # issue summary
            'description':       None,              # issue description
            'issuetype':         {'name': '-'},     # issue type (Task, Sub-task, etc...)
            'assignee':          {'name': '-'},     # assignee user
            'priority':          {'name': '-'},     # issue priority
            'fixVersions':       [{'name': '-'}],   # list of {name : value} pairs
            'customfield_10008': 0,                 # story points, integer value
            'duedate':           None               # date format: yyyy-MM-dd
        }

    def project(self, project_key):
        self.__issue_dict['project'] = project_key
        return self

    def summary(self, summary):
        self.__issue_dict['summary'] = summary
        return self

    def desc(self, description):
        self.__issue_dict['description'] = description
        return self

    def issuetype(self, issuetype):
        self.__issue_dict['issuetype'] = {'name': issuetype}
        return self

    def assignee(self, assignee):
        self.__issue_dict['assignee'] = {'name': assignee}
        return self

    def priority(self, priority):
        self.__issue_dict['priority'] = {'name': priority}
        return self

    def fixversions(self, fix_versions):
        self.__issue_dict['fixVersions'] = [{'name': version} for version in fix_versions]
        return self

    def storypoints(self, storypoints):
        self.__issue_dict['customfield_10008'] = storypoints
        return self

    def duedate(self, duedate):
        self.__issue_dict['duedate'] = duedate
        return self

    def __str__(self):
        return self.issue_dict
