#!/usr/bin/env python
# coding=utf-8

"""
 JIRA Utility object, that incapsulates some useful methods.
 Created: Gusev Dmitrii, 04.04.2017
"""

import prettytable
from jira import JIRA


# todo: throw away logic, related to team (do it more general)
# todo: unit tests for this class!!!

# noinspection PyCompatibility
class JIRAUtility(object):

    # JQL_ALL_SPRINT_ISSUES = 'project = {} AND issuetype in ({}) AND Sprint = "{}" AND assignee in ({})'
    JQL_ALL_SPRINT_ISSUES = 'project = {} AND sprint = "{}"'
    # all issues, that may be interested
    ISSUES_ALL_TYPES = 'Bug, Epic, Story, Task, Sub-task'

    def __init__(self, jira_address, user, password):
        """
        Initializer for JIRAUtility class.
        :param config_path:
        """
        print "JIRAUtility.__init__() is working. JIRA: [{}], user: [{}].".format(jira_address, user)
        # fail-fast - check JIRA address and username
        if not jira_address or not jira_address.strip():
            raise JiraException('Empty JIRA address!')
        if not user or not user.strip():
            raise JiraException('Empty username!')
        # init internal JIRA instance
        self.jira = None
        self.address = jira_address
        self.user = user
        self.password = password

    def connect(self):
        """
        Connect to JIRA server (with params specified in constructor).
        :return: None
        """
        print "JIRAUtility.connect() is working. Connecting to [{}] as user [{}].".format(self.address, self.user)
        self.jira = JIRA(self.address, basic_auth=(self.user, self.password))
        print "JIRAUtility: connected to [{}] as user [{}].".format(self.address, self.user)

    def get_project_key(self, project_name):
        """
        Returns project key from JIRA by exact project name (first occurrence).
        :param project_name: name of project for which we will get the key
        :return: project key
        """
        print "JIRAUtility.get_project_key() is working. Search project key by name: [{}].".format(project_name)
        # fail-fast check of parameters
        # todo: !!!
        for project in self.jira.projects():
            if project.name == project_name:
                print "Found key [{}] for project [{}].".format(project.key, project_name)
                return project.key
        # project/key not found
        print "Key for project [{}] not found!".format(project_name)
        return None

    def get_component_by_name(self, project_name, component_name):
        """
        Returns component object by name for project with specified name. Component object contains fields: id, name, self
        :param project_name: name of project for which we will search a component
        :param component_name: name for component search
        :return:
        """
        print "JIRAUtility.get_component() is working. Search component: [{}] for project: [{}]."\
            .format(component_name, project_name)
        # fail-fast check of parameters
        # todo: !!!
        for component in self.jira.project_components(self.get_project_key(self.config.get('jira_project'))):
            if component.name == component_name:
                print "Found component [{}] by name [{}].".format()
                return component
            #print component.id, '->', component.name, '->', component.self
        # component not found
        return None

    def get_all_sprint_issues(self, sprint_name):
        """
        Returns all sprint issues (for current project)
        :param sprint_name:
        :return:
        """
        print "JIRAUtility.get_all_sprint_issues() is working. Search issues for sprint: [{}].".format(sprint_name)
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
                print "Processed -> {}/{}".format(counter, len(issues))
        print "Updated [{}] issue(s).".format(counter)

    def add_component_to_issues_list(self, issues_list, component_name):
        return None

    def get_issue(self, issue_key):
        """
        Returns object JIRA issue by key,
        :param issue_key: issue key for search (<project key>-<number>).
        :return:
        """
        print "JIRAUtility.get_issue() is working. Get issue by key: [{}].".format(issue_key)
        return self.jira.issue(issue_key)

    @staticmethod
    def print_raw_issue(issue):
        """
        This method is intended mostly for debug purposes - print JIRA issue as a raw JSON
        :param issue: issue object for printing
        :return:
        """
        print "JIRAUtility.print_raw_issue() is working."
        print issue.raw
        #print '\n\n'
        #print issue.fields.components
        #for component in issue.fields.components:
        #    print component.id, '->', component.name, '->', component.self

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
            components = ', '.join([component.name for component in issue.fields.components])
            status = str(issue.fields.status)
            # add row to report
            report.add_row([counter, issue.key, issue.fields.issuetype, storypoints, labels, components, issue.fields.summary, status, assignee])
            counter += 1

        return report


class JiraException(Exception):
    """JIRA Exception, used if something is wrong with/in JIRA interaction."""
    pass
