#!/usr/bin/env python
# coding=utf-8

"""
    Basic utility module for interacting with JIRA. Contains BaseJIRAUtility class, internal
    exception. Maybe some useful methods will be added.

    Created: Gusev Dmitrii, 04.04.2017
    Modified: Gusev Dmitrii, 01.10.2017
"""

from jira import JIRA
from pylib import JiraException
from configuration import Configuration
import prettytable
import jira_constants as jconst


class BaseJiraUtility(object):
    """ Class BaseJIRAUtility. Intended for interaction with JIRA and performing some useful actions. """

    def __init__(self, config, *jira_params):
        """
        System method: initializer for BaseJIRAUtility class.
        :param jira_address: address of JIRA instance
        :param user: user for JIRA
        :param password: pass for JIRA user
        """
        print "BaseJIRAUtility.__init__() is working. Config [%s], jira params [%s]" % (config, jira_params)
        # if specified config file/object - use it, ignore other params.
        if config:
            if isinstance(config, str):
                print "Provided config is string path. Loading."
                self.config = Configuration(config)
            elif isinstance(config, Configuration):
                print "Provided config is Configuration() object."
                self.config = config
            else:
                raise JiraException("Unknown configuration object type! Not a string path/object!")
        # if not specified config - use *jira_params tuple
        elif jira_params and len(jira_params) == 3:
            print "Using list of jira parameters."
            self.config = Configuration()
            # set parameters
            jira_address = jira_params[0]
            if not jira_address or not jira_address.strip():  # check JIRA address
                raise JiraException('Empty JIRA address!')
            self.config.set(jconst.CONFIG_KEY_ADDRESS, jira_address)
            jira_user = jira_params[1]
            if not jira_user or not jira_user.strip():  # check JIRA user
                raise JiraException('Empty username!')
            self.config.set(jconst.CONFIG_KEY_USER, jira_user)
            self.config.set(jconst.CONFIG_KEY_PASS, jira_params[2])
        else:
            raise JiraException("No configuration parameters provided!")
        # init internal JIRA instance state
        self.jira = None

    def connect(self):
        """
        Internal system method: connect to JIRA instance (with params specified in constructor).
        Init internal field [jira].
        """
        address = self.config.get(jconst.CONFIG_KEY_ADDRESS)
        user = self.config.get(jconst.CONFIG_KEY_USER)
        # check - if we aren't connected -> connect, otherwise - skip (just inform)
        if not self.jira:
            password = self.config.get(jconst.CONFIG_KEY_PASS)
            print "BaseJIRAUtility.connect() is working. Connecting to [{}] as user [{}].".format(address, user)
            self.jira = JIRA(address, basic_auth=(user, password))
            print "BaseJIRAUtility: connected to [{}] as user [{}].".format(address, user)
        else:
            print "BaseJIRAUtility: already connected to [{}] as user [{}].".format(address, user)

    def execute_jql(self, jql):
        """
        Utility method for executing JQL in JIRA and get result issues list.
        :param jql: JQL query to be executed in a JIRA
        :return: found issues list
        """
        if not jql or not jql.strip():  # fast check
            raise JiraException('Provided JQL is empty!')
        # search for issues by provided jql and return them
        issues = []
        batch_size = jconst.CONST_JIRA_ISSUES_BATCH_SIZE
        total_processed = 0
        while batch_size == jconst.CONST_JIRA_ISSUES_BATCH_SIZE:
            # get issues part (in a size of batch, default = 50 - see BaseJIRAUtility.ISSUES_BATCH_SIZE)
            issues_batch = self.jira.search_issues(jql_str=jql, maxResults=False, startAt=total_processed)
            batch_size = len(issues_batch)  # update current batch size
            total_processed += batch_size  # update total processed count
            for issue in issues_batch:  # add all found issues to resulting list
                issues.append(issue)
        # return result
        return issues

    def get_project_key(self, project_name):
        """
        Returns project key from JIRA by provided project name (return first occurrence).
        :param project_name: name of project for which we will get the key
        :return: project key or None
        """
        print "BaseJIRAUtility.get_project_key() is working. Search project key by name: [{}].".format(project_name)
        if not project_name or not project_name.strip():  # fail-fast - provided project name
            raise JiraException('Empty project name for project key search!')
        # search project key
        for project in self.jira.projects():
            if project.name == project_name:
                print "Found key [{}] for project [{}].".format(project.key, project_name)
                return project.key
        # project/key not found
        print "Key for project [{}] not found!".format(project_name)
        return None

    def get_component_by_name(self, project_name, component_name):
        """
        Returns component object by specified name for specified project.
        Component object contains fields: id, name, self
        :param project_name: name of project for which we will search a component
        :param component_name: name for component search
        :return: found component or None
        """
        print "BaseJIRAUtility.get_component() is working. Search component: [{}] for project: [{}]."\
            .format(component_name, project_name)
        # fail-fast - check of parameters
        if not project_name or not project_name.strip() or not component_name or not component_name.strip():
            raise JiraException('Empty project name [{}] or component name [{}]!'.format(project_name, component_name))
        # search over project components
        for component in self.jira.project_components(self.get_project_key(project_name)):
            if component.name == component_name:
                print "Found component by name [{}].".format(component_name)
                return component
        # component not found
        print "Component by name [{}] not found!".format(component_name)
        return None

    def get_all_sprint_issues(self, sprint_name):
        """
        Returns list of all sprint issues, found by provided sprint name
        :param sprint_name: name of sprint for issues search
        :return: list of sprint issues
        """
        print "BaseJIRAUtility.get_all_sprint_issues() is working. Search issues for sprint: [{}].".format(sprint_name)
        if not sprint_name or not sprint_name.strip():  # fail-fast
            raise JiraException("Empty sprint name!")
        # generate jql
        jql = 'sprint = "{}"'.format(sprint_name)
        print "Generated JQL [{}].".format(jql)
        return self.execute_jql(jql)  # search for issues and return them

    def add_component_to_issues(self, issues, project_name, component_name):
        """
        Add specified component (from specified project) to issues list.
        :param issues: list of issues for adding component
        :param project_name: project for component
        :param component_name: component name
        :return:
        """
        print "BaseJIRAUtility.add_component_to_issues() is working."

        if not project_name or not project_name.strip():  # fail-fast
            raise JiraException('Project name is empty!')
        if not component_name or not component_name.strip():  # fail-fast
            raise JiraException('Component name is empty!')

        # get component (if return None - no such component!)
        new_component = self.get_component_by_name(project_name, component_name)

        if new_component:
            counter = 0
            for issue in issues:
                existing_components = []
                for comp in issue.fields.components:
                    existing_components.append({"name": comp.name})
                existing_components.append({"name": new_component.name})
                issue.update(fields={"components": existing_components})

                counter += 1
                if counter % jconst.CONST_PROGRESS_STEP_COUNTER == 0:  # report progress
                    print "Processed -> {}/{}".format(counter, len(issues))
            print "Summary: updated [{}] issue(s).".format(counter)
        else:
            print "Component [{}] not found!".format(component_name)

    def get_issue(self, issue_key):
        """
        Returns object JIRA issue by key,
        :param issue_key: issue key for search (<project key>-<number>).
        :return:
        """
        print "BaseJIRAUtility.get_issue() is working. Get issue by key: [{}].".format(issue_key)
        return self.jira.issue(issue_key)

    def get_all_closed_issues_for_user(self, user, last_days_count=0):
        """
        Return all closed issues (in statuses 'Done' and 'Closed') for specified user for specified
        days count back in time, starting from today.
        :param user: user for issues search
        :param last_days_count: count of days back in time for issues search, if = 0, will search
        closed issues for all time, if < 0, raise exception
        :return: found issues list
        """
        print "BaseJIRAUtility.get_all_issues_for_user() is working. Search issues for user: [{}].".format(user)

        # fast checks
        if not user or not user.strip:
            raise JiraException('User name is empty!')
        if last_days_count < 0:
            raise JiraException('Invalid (negative) value for days back counter!')

        # generate jql (depends on in params)
        jql = 'assignee = {} AND status changed to (Closed, Done)'.format(user)
        if last_days_count > 0:
            jql += ' after -{}d'.format(last_days_count)
        print "Generated JQL [{}].".format(jql)
        # execute jql and return result
        return self.execute_jql(jql)

    def get_current_status_for_user(self, user):
        # todo: implementation/pydoc
        print "BaseJIRAUtility.get_current_status_for_user() is working. Search 'In Progress' issues for user: [{}]."\
            .format(user)
        # fast checks
        if not user or not user.strip:
            raise JiraException('User name is empty!')
        # generate jql
        jql = 'assignee = {} AND status = "In Progress"'.format(user)
        print "Generated JQL [{}]".format(jql)
        # execute jql and return result
        return self.execute_jql(jql)

    @staticmethod
    def add_label_to_issues(issues, label_name):
        """
        Add specified label to specified issues list.
        :param issues: issues for add label to
        :param label_name: label to add to each issue
        """
        print "BaseJIRAUtility.add_label_to_issues() is working. Adding label [{}].".format(label_name)

        if not label_name or not label_name.strip():  # fail-fast
            raise JiraException('Label is empty!')

        # iterate over issues and add team label to each
        counter = 0
        for issue in issues:
            if label_name not in issue.fields.labels:  # add label
                issue.fields.labels.append(label_name)
                issue.update(fields={"labels": issue.fields.labels})
            counter += 1
            if counter % jconst.CONST_PROGRESS_STEP_COUNTER == 0:  # report progress
                print "Processed -> {}/{}".format(counter, len(issues))
        print "Summary: updated [{}] issue(s).".format(counter)

    @staticmethod
    def print_raw_issue(issue):
        """
        This method is intended mostly for debug purposes - print JIRA issue as a raw JSON
        :param issue: issue object for printing
        """
        print "BaseJIRAUtility.print_raw_issue() is working."
        print issue.raw

    @staticmethod
    def get_issues_report(issues, show_label=False):
        """
        Generate and return report for issues
        :param issues: list of issues
        :param show_label: show "Labels" column in a report, True by default
        :return: generated report
        """
        print "BaseJIRAUtility.get_issues_report() is working."

        # create report header
        header_list = ['#', 'Issue', 'Type', 'SP']
        if show_label:
            header_list.append('Labels')
        header_list.extend(['Components', 'Summary', 'Status', 'Assignee'])
        report = prettytable.PrettyTable(header_list)
        # align columns (Summary = left)
        report.align['Summary'] = "l"
        # add rows to report
        counter = 1

        for issue in issues:
            # transform/process some columns values
            assignee = ('-' if not issue.fields.assignee else issue.fields.assignee)
            # no story points for: "Sub-task", "Test Suite"
            storypoints = (issue.fields.customfield_10008 if str(issue.fields.issuetype) not in ["Sub-task", "Test Suite"] else '-')
            components = ',\n '.join([component.name for component in issue.fields.components])
            status = str(issue.fields.status)

            # labels - is optional column
            if show_label:
                labels = ',\n '.join(issue.fields.labels)
                report.add_row([counter, issue.key, issue.fields.issuetype, storypoints, labels, components, issue.fields.summary, status, assignee])
            else:
                report.add_row([counter, issue.key, issue.fields.issuetype, storypoints, components, issue.fields.summary, status, assignee])

            counter += 1
        # return generated report
        return report
