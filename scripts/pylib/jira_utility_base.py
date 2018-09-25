#!/usr/bin/env python
# coding=utf-8

"""
    Basic utility module for interacting with JIRA. Contains JIRAUtilityBase class, internal
    exception. Maybe some useful methods will be added.

    Created: Gusev Dmitrii, 04.04.2017
    Modified: Gusev Dmitrii, 27.12.2017
"""

import codecs
import prettytable
import logging
import common_constants as myconst
from jira import JIRA
from pyutilities.pyutilities.configuration import Configuration


class JiraUtilityBase(object):
    """ Class JIRAUtilityBase. Intended for interaction with JIRA and performing some useful actions. """

    # internal read-only property, could be accessed outside, but cannot be assigned
    @property
    def jira(self):
        return self.__jira

    @property
    def config(self):
        return self.__config

    def __init__(self, config):
        """
        System method: initializer for JIRAUtilityBase class.
        :param config: Configuration object or string path to config file/directory
        """
        # init logger
        self.log = logging.getLogger(__name__)
        self.log.addHandler(logging.NullHandler())

        self.log.debug("Initializing JIRA Base Utility class.\nConfig [{}].".format(config))
        # if specified config file/object - use it, ignore other params.
        if config:
            if isinstance(config, str):
                if not config.strip():
                    raise JiraException("Empty configuration object/path provided!")
                self.log.debug("Provided config is string path. Loading.")
                self.__config = Configuration(config)
            elif isinstance(config, Configuration):
                self.log.debug("Provided config is Configuration() object.")
                self.__config = config
            else:
                raise JiraException("Unknown configuration object type! Not a string path/object!")
        else:
            raise JiraException("Empty configuration object/path provided!")
        # init internal JIRA instance state
        self.__jira = None

    def connect(self):
        """
        Internal system method: connect to JIRA instance (with params from config - initialized in constructor).
        Also init internal field [jira].
        """
        self.log.debug("connect() is working.")
        # check - if we aren't connected -> connect, otherwise - skip (just inform)
        if not self.jira:
            self.log.debug("JIRA: doesn't connected yet. Connecting.")

            # collect connection parameters
            jira_address = self.config.get(myconst.CONFIG_KEY_JIRA_ADDRESS, '')
            jira_rest_path = self.config.get(myconst.CONFIG_KEY_JIRA_REST_PATH, '')
            jira_api_ver = self.config.get(myconst.CONFIG_KEY_JIRA_API_VERSION, '')
            user = self.config.get(myconst.CONFIG_KEY_JIRA_USER, '')
            password = self.config.get(myconst.CONFIG_KEY_JIRA_PASS, '')
            http_proxy = self.config.get(myconst.CONFIG_KEY_PROXY_HTTP, '')
            https_proxy = self.config.get(myconst.CONFIG_KEY_PROXY_HTTPS, '')

            # create connection options
            options = {
                'server': jira_address,
                'rest_path': jira_rest_path,
                'rest_api_version': jira_api_ver,
                'verify': False
            }

            # add proxies (if set)
            proxies = {}
            if http_proxy:
                proxies['http'] = http_proxy
            if https_proxy:
                proxies['https'] = https_proxy

            self.log.info("JIRA: connection to jira server:"
                          "\n\tuser -> {}\n\toptions -> {}\n\tproxies -> {}"
                          .format(user, options, proxies))

            if proxies:  # connect through proxy
                # just a workaround to get rid of warnings :)
                import urllib3
                urllib3.disable_warnings()
                # connecting (and get instance of JIRA object)
                self.__jira = JIRA(options=options, basic_auth=(user, password), proxies=proxies)
            else:
                self.__jira = JIRA(options=options, basic_auth=(user, password))

            self.log.info("JIRA: successfully connected.")
        else:
            self.log.info("JIRA: already connected.")

    def execute_jql(self, jql):
        """
        Utility method for executing JQL in JIRA and get resulting issues list.
        :param jql: JQL query to be executed in a JIRA
        :return: found issues list
        """
        self.log.debug("execute_jql() is working. JQL [{}].".format(jql))
        if not jql or not jql.strip():  # fast check
            raise JiraException('Provided JQL is empty!')
        # connect to JIRA
        self.connect()
        # search for issues by provided jql and return them
        issues = []
        batch_size = myconst.CONST_JIRA_ISSUES_BATCH_SIZE
        total_processed = 0
        while batch_size == myconst.CONST_JIRA_ISSUES_BATCH_SIZE:
            # get issues part (in a size of batch, default = 50 - see JIRAUtilityBase.ISSUES_BATCH_SIZE)
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
        self.log.debug("get_project_key() is working. Search project key by name: [{}].".format(project_name))
        self.connect()
        if not project_name or not project_name.strip():  # fail-fast - provided project name
            raise JiraException('Empty project name for project key search!')
        # search project key
        for project in self.jira.projects():
            if project.name == project_name:
                self.log.debug("Found key [{}] for project [{}].".format(project.key, project_name))
                return project.key
        # project/key not found
        self.log.debug("Key for project [{}] not found!".format(project_name))
        return None

    def get_component_by_name(self, project_name, component_name):
        """
        Returns component object by specified name for specified project.
        Component object contains fields: id, name, self
        :param project_name: name of project for which we will search a component
        :param component_name: name for component search
        :return: found component or None
        """
        self.log.debug("get_component() is working. Search component: [{}] for project: [{}]."
                       .format(component_name, project_name))
        # fail-fast - check of parameters
        if not project_name or not project_name.strip() or not component_name or not component_name.strip():
            raise JiraException('Empty project name [{}] or component name [{}]!'.format(project_name, component_name))
        # search over project components
        for component in self.jira.project_components(self.get_project_key(project_name)):
            if component.name == component_name:
                self.log.debug("Found component by name [{}].".format(component_name))
                return component
        # component not found
        self.log.debug("Component by name [{}] not found!".format(component_name))
        return None

    def get_all_sprint_issues(self, sprint_name):
        """
        Returns list of all sprint issues, found by provided sprint name
        :param sprint_name: name of sprint for issues search
        :return: list of sprint issues
        """
        self.log.debug("get_all_sprint_issues() is working. Search issues for sprint: [{}].".format(sprint_name))
        if not sprint_name or not sprint_name.strip():  # fail-fast
            raise JiraException("Empty sprint name!")
        # generate jql
        jql = 'sprint = "{}"'.format(sprint_name)
        self.log.debug("Generated JQL [{}].".format(jql))
        return self.execute_jql(jql)  # search for issues and return them

    def add_component_to_issues(self, issues, project_name, component_name):
        """
        Add specified component (from specified project) to issues list.
        :param issues: list of issues for adding component
        :param project_name: project for component
        :param component_name: component name
        :return:
        """
        self.log.debug("add_component_to_issues() is working.")

        if not project_name or not project_name.strip():  # fail-fast
            raise JiraException('Project name is empty!')
        if not component_name or not component_name.strip():  # fail-fast
            raise JiraException('Component name is empty!')

        # get component (if return None - no such component!)
        new_component = self.get_component_by_name(project_name, component_name)

        if new_component:
            counter = 0

            for issue in issues:  # iterate over issues and try to add component
                existing_components = []
                is_comp_found = False
                for comp in issue.fields.components:
                    existing_components.append({"name": comp.name})
                    if comp.name == component_name:  # component already exists
                        is_comp_found = True
                        break  # component found, don't need to iterate further

                if not is_comp_found:  # if we haven't found component, add it and update issue
                    existing_components.append({"name": new_component.name})
                    issue.update(fields={"components": existing_components})  # <- very long operation!

                counter += 1
                if counter % myconst.CONST_PROGRESS_STEP_COUNTER == 0:  # report progress
                    self.log.debug("Processed -> {}/{}".format(counter, len(issues)))
            self.log.debug("Summary: updated [{}] issue(s).".format(counter))
        else:
            self.log.debug("Component [{}] not found!".format(component_name))

    def get_issue(self, issue_key):
        """
        Returns object JIRA issue by key,
        :param issue_key: issue key for search (<project key>-<number>).
        :return:
        """
        self.log.debug("get_issue() is working. Get issue by key: [{}].".format(issue_key))
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
        self.log.debug("get_all_issues_for_user() is working. Search issues for user: [{}].".format(user))

        # fast checks
        if not user or not user.strip:
            raise JiraException('User name is empty!')
        if last_days_count < 0:
            raise JiraException('Invalid (negative) value for days back counter!')

        # generate jql (depends on in params)
        jql = 'assignee = {} AND status changed to (Closed, Done)'.format(user)
        if last_days_count > 0:
            jql += ' after -{}d'.format(last_days_count)
        self.log.debug("Generated JQL [{}].".format(jql))
        # execute jql and return result
        return self.execute_jql(jql)

    def get_current_status_for_user(self, user):
        self.log.debug("get_current_status_for_user() is working. User: [{}].".format(user))
        # fast checks
        if not user or not user.strip:
            raise JiraException('User name is empty!')
        # generate jql
        jql = 'assignee = {} AND status = "In Progress"'.format(user)
        self.log.debug("Generated JQL [{}]".format(jql))
        # execute jql and return result
        return self.execute_jql(jql)

    def write_report_to_file(self, report, out_file=None):
        """
        Write report to specified file. If file isn't specified (by default), using internal config value.
        If internal config doesn't exist too - do nothing.
        :return:
        """
        self.log.debug("write_report_toFile() is working. Output file [{}].".format(out_file))
        # select report output file (if specified)
        if out_file and out_file.strip():
            report_file = out_file
        else:
            report_file = self.config.get(myconst.CONFIG_KEY_OUTPUT_FILE, '')
        # out report to file (if no file - no output!)
        if report_file:
            self.log.debug("Output report to file [{}].".format(report_file))
            with codecs.open(report_file, 'w', myconst.CONST_COMMON_ENCODING) as out:
                out.write(report)
        else:
            self.log.warn("Can't output to file with empty name!")

    def add_label_to_issues(self, issues, label_name):
        """
        Add specified label to specified issues list.
        :param issues: issues for add label to
        :param label_name: label to add to each issue
        """
        self.log.debug("add_label_to_issues() is working. Adding label [{}].".format(label_name))
        if not label_name or not label_name.strip():  # fail-fast
            raise JiraException('Label is empty!')
        # iterate over issues and add team label to each
        counter = 0
        for issue in issues:
            if label_name not in issue.fields.labels:  # add label
                issue.fields.labels.append(label_name)
                issue.update(fields={"labels": issue.fields.labels})
            counter += 1
            if counter % myconst.CONST_PROGRESS_STEP_COUNTER == 0:  # report progress
                self.log.debug("Processed -> {}/{}".format(counter, len(issues)))
        self.log.debug("Summary: updated [{}] issue(s).".format(counter))

    def print_raw_issue(self, issue):
        """
        This method is intended mostly for debug purposes - print JIRA issue as a raw JSON
        :param issue: issue object for printing
        """
        self.log.debug("print_raw_issue() is working.")
        print "Raw issue:\n\t", issue.raw

    def get_issues_report(self, issues, show_label=False):
        """
        Generate and return report for issues
        :param issues: list of issues
        :param show_label: show "Labels" column in a report, True by default
        :return: generated report
        """
        self.log.debug("get_issues_report() is working.")

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
                report.add_row([counter, issue.key, issue.fields.issuetype, storypoints, labels, components,
                                issue.fields.summary, status, assignee])
            else:
                report.add_row([counter, issue.key, issue.fields.issuetype, storypoints, components,
                                issue.fields.summary, status, assignee])

            counter += 1
        # return generated report
        return report


class JiraException(Exception):
    """JIRA Exception, used if something is wrong with/in JIRA interaction."""
