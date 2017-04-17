#!/usr/bin/env python
# coding=utf-8

"""
 Some useful JIRA utilities/functions.
 Created: Gusev Dmitrii, 04.04.2017
"""

import configuration as conf
import prettytable
from jira import JIRA


# noinspection PyCompatibility
class JIRAUtility(object):

    # JQL_ALL_SPRINT_ISSUES = 'project = {} AND issuetype in ({}) AND Sprint = "{}" AND assignee in ({})'
    JQL_ALL_SPRINT_ISSUES = 'project = {} AND sprint = "{}"'
    # all issues, that may be interested
    ISSUES_ALL_TYPES = 'Bug, Epic, Story, Task, Sub-task'

    def __init__(self, config_path):
        """
        Initializer for JIRAUtility class.
        :param config_path:
        """
        print "JIRAUtility.__init__() is working."
        # init and load internal config
        self.config = conf.Configuration()
        self.config.load(config_path)
        # init some internal fields
        # self.jira_project = self.config.get('jira_project')
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
        print "JIRAUtility.connect() is working."
        jira_address = self.config.get('jira_address')
        self.jira = JIRA(jira_address, basic_auth=(user, password))
        print "JIRAUtility: connected to JIRA by address [{}] with username [{}].".format(jira_address, user)

    # def get_project_key(self, project_name):
    #     """
    #     Returns project key from JIRA by exact project name (first occurence).
    #     :param project_name:
    #     :return: project key
    #     """
    #     projects = self.jira.projects()
    #     for project in projects:
    #         if project.name == project_name:
    #             return project.key
    #
    #     return None

    def get_all_sprint_issues(self, sprint_name):
        """
        Returns all sprint issues (for current project)
        :param sprint_name:
        :return:
        """
        print "JIRAUtility.get_all_sprint_issues() is working."
        # generate jql
        jql = JIRAUtility.JQL_ALL_SPRINT_ISSUES.format(self.config.get('jira_project'), sprint_name)
        print "Generated JQL [{}].".format(jql)
        # search for issues and return them
        issues = []
        batch_size = 50
        total_processed = 0
        while batch_size == 50:
            # get issues part (in a size of batch, default = 50)
            issues_batch = self.jira.search_issues(jql_str=jql, maxResults=False, startAt=total_processed)
            # update current batch size
            batch_size = len(issues_batch)
            # update total processed count
            total_processed += batch_size
            # add all found issues to resulting list
            for issue in issues_batch:
                issues.append(issue)
        # return result
        return issues

    def add_label_to_sprint_issues(self, sprint_name):
        """
        Add team label (from config) to issues from specified sprint.
        :param sprint_name: sprint for search issues (for adding label)
        :return: None
        """
        print "JIRAUtility.add_label_to_sprint_issues() is working."
        issues = self.get_all_sprint_issues(sprint_name)
        print "Found [{}] issues for sprint [{}].".format(len(issues), sprint_name)
        # iterate over found issues and add team label to each
        label = self.config.get('team_label')
        counter = 0
        for issue in issues:
            labels = issue.fields.labels
            if label not in labels:
                issue.fields.labels.append(label)
                issue.update(fields={"labels": issue.fields.labels})
            counter += 1
            if counter % 5 == 0:
                print "-> updated {}".format(counter)
        print "Updated [{}] issue(s).".format(counter)

    def get_issue(self):
        """

        :return:
        """
        print "JIRAUtility.get_issue() is working."
        #authenticated_jira = JIRA(options={'server': self.jira_server}, basic_auth=(self.jira_username, self.jira_password))
        issue = self.jira.issue('ZEN-37')
        #print issue.fields()
        print issue.raw

    @staticmethod
    def get_issues_report(issues):
        """
        Generate and return report for issues
        :param issues: list of issues
        :return: generated report
        """
        print "JIRAUtility.get_issues_report() is working."
        # create report header
        report = prettytable.PrettyTable(['#', 'Issue', 'Type', 'SP', 'Labels', 'Components', 'Summary', 'Status', 'Assignee'])
        report.align['Summary'] = "l"
        # add rows to report
        counter = 1
        for issue in issues:
            # fix some columns values
            assignee = ('-' if not issue.fields.assignee else issue.fields.assignee)
            storypoints = (issue.fields.customfield_10008 if str(issue.fields.issuetype) != "Sub-task" else '-')
            labels = ', '.join(issue.fields.labels)
            components = ', '.join(issue.fields.components)
            status = str(issue.fields.status)
            # add row to report
            report.add_row([counter, issue.key, issue.fields.issuetype, storypoints, labels, components, issue.fields.summary, status, assignee])
            counter += 1

        return report
