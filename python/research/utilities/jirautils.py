#!/usr/bin/env python
# coding=utf-8

"""
 Some useful JIRA utilities/functions.
 Created: Gusev Dmitrii, 04.04.2017
"""

import configuration as conf
from jira import JIRA


# noinspection PyCompatibility
class JIRAUtility(object):

    # JQL query for all sprint issues
    JQL_ALL_SPRINT_ISSUES = 'project = {} AND issuetype in ({}) AND Sprint = "{}" AND assignee in ({})'
    # all issues, that may be interested
    ISSUES_ALL_TYPES = 'Bug, Epic, Story, Task, Sub-task'

    def __init__(self, config_path):
        """
        Initializer for JIRAUtility class.
        :param config_path:
        """
        # init and load internal config
        self.config = conf.Configuration()
        self.config.load(config_path)
        # init some internal fields
        self.jira_project = self.config.get('jira_project')
        self.team_members_string = ", ".join(self.config.get('team'))
        # init internal JIRA instance
        self.jira = None

    def connect(self, user, password):
        """
        Connect to JIRA server.
        :param user: JIRA user
        :param password: JIRA password
        :return: None
        """
        jira_address = self.config.get('jira_address')
        self.jira = JIRA(jira_address, basic_auth=(user, password))
        print "Connected to JIRA by address [{}].".format(jira_address)

    def get_project_key(self, project_name):
        """
        Returns project key from JIRA by exact project name (first occurence).
        :param project_name:
        :return: project key
        """
        projects = self.jira.projects()
        for project in projects:
            if project.name == project_name:
                return project.key

        return None

    def get_current_project_key(self):
        """
        Returns current project key by name from config.
        :return: project key
        """
        return self.get_project_key(self.config.get('jira_project'))

    def add_label_to_sprint_issues(self, sprint_name):
        """
        Add team label (from config) to issues from specified sprint.
        :param sprint_name: sprint for search issues (for adding label)
        :return: None
        """
        print "JIRAUtility.add_label_to_sprint_issues() is working."
        # format JQL query (add values)
        jql = JIRAUtility.JQL_ALL_SPRINT_ISSUES.format(self.jira_project, JIRAUtility.ISSUES_ALL_TYPES,
                                                       sprint_name, self.team_members_string)
        print "Generated JQL [{}].".format(jql)

        # search issues for specified sprint
        issues = self.jira.search_issues(jql, maxResults=False)
        print "Found [{}] issues for sprint [{}].".format(len(issues), sprint_name)

        # iterate over found issues list and add team label
        label = self.config.get('team_label')
        for issue in issues:
            labels = issue.fields.labels
            print 'ISSUE:', issue.key, 'LABELS:', issue.fields.labels, 'SUMMARY:', issue.fields.summary
            if label not in labels:
                issue.fields.labels.append(label)
                issue.update(fields={"labels": issue.fields.labels})

    def get_issues_for_assignee(self, assignee):
        # todo: implementation!
        print "Not implemented yet!"

