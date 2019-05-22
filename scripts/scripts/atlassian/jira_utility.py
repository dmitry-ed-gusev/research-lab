#!/usr/bin/env python
# coding=utf-8

"""
    Utility module for interacting with JIRA. Contains JIRAUtility class, internal
    exception. Maybe some useful methods will be added.

    Created:  Gusev Dmitrii, 04.04.2017
    Modified: Gusev Dmitrii, 12.02.2019
"""

import logging
import pylib.consts as consts
from jira import JIRA
from pyutilities.utils import setup_logging
from pyutilities.config import Configuration, ConfigurationXls

# config keys and some useful constants for JIRA
CONFIG_KEY_JIRA_ADDRESS = "jira.address"
CONFIG_KEY_JIRA_REST_PATH = 'jira.rest_path'
CONFIG_KEY_JIRA_API_VERSION = 'jira.rest_api_version'
CONFIG_KEY_JIRA_USER = "jira.user"
CONFIG_KEY_JIRA_PASS = "jira.password"
# CONFIG_KEY_SPRINT = "sprint.name"
# CONFIG_KEY_TEAM_MEMBERS = "teams.%s.members"
# CONFIG_KEY_TEAM_PROJECT = "teams.%s.project"
# CONFIG_KEY_TEAM_PROJECT_ABBR = "teams.%s.project_abbr"
# CONFIG_KEY_TEAM_LABEL = "teams.%s.label"
# CONFIG_KEY_TEAM_COMPONENT = "teams.%s.component"
# team name - cmd line parameter stored here
# CONFIG_KEY_TEAM_NAME = "team.name"
# config key -> option to execute
# CONFIG_KEY_OPTION = "execute.option"
# config key -> output file for report writing
# CONFIG_KEY_OUTPUT_FILE = "output.file"
# CONFIG_KEY_DAYS_BACK = "days.back"
# CONFIG_KEY_USE_SIMPLE_REPORT = "use.simple.report"
# CONFIG_KEY_SHOW_LABEL_COLUMN = "show.label.column"
CONST_JIRA_ISSUES_BATCH_SIZE = 50
# CONST_TEAMS_LIST = ("ada", "ada_all", "nova", "nova_all", "bmtef", "bmtef_all")
IMPLEMENTS = "Implementation"
IMPLEMENTS_TYPE = "implements / must come before"

# useful JQL queries for JIRA
JQL_SPRINT_ISSUES = 'sprint = "{}"'
JQL_CLOSED_ISSUES_FOR_USER = 'assignee = {} AND status changed to (Closed, Done)'
JQL_CLOSED_ISSUES_FOR_USER_SINCE = 'assignee = {} AND status changed to (Closed, Done) after -{}d'
JQL_USER_CURRENT_STATUS = 'assignee = {} AND status = "In Progress"'


# todo: move methods for reporting to another script???
# todo: config -> self.config.get(CONFIG_KEY_OUTPUT_FILE, '')
class JiraUtility(object):
    """ Class JIRAUtilityBase. Intended for interaction with JIRA and performing some useful actions. """

    # internal read-only property, could be accessed outside, but cannot be assigned
    @property
    def jira(self):
        return self.__jira

    @property
    def config(self):
        return self.__config

    def __init__(self, config):
        """System method-initializer for JIRAUtilityBase class.
        :param config: Configuration object
        """
        # init logger
        self.log = logging.getLogger(__name__)
        self.log.addHandler(logging.NullHandler())
        self.log.debug("Initializing JIRA Utility class.\nConfig [{}].".format(config))
        # init internal state
        if config:
            self.__config = config
        else:
            raise JiraException("Empty configuration object provided!")
        # init internal JIRA instance state
        self.__jira = None

    def connect(self):
        """Internal system method: connect to JIRA instance (with params from config - initialized in constructor).
        Also init internal field [jira].
        """
        self.log.debug("connect() is working.")
        # check - if we aren't connected -> connect, otherwise - skip (just inform)
        if not self.jira:
            self.log.info("JIRA: isn't connected yet. Connecting.")

            # collect connection parameters
            user = self.config.get(CONFIG_KEY_JIRA_USER, '')
            password = self.config.get(CONFIG_KEY_JIRA_PASS, '')
            http_proxy = self.config.get(consts.CONFIG_KEY_PROXY_HTTP, '')
            https_proxy = self.config.get(consts.CONFIG_KEY_PROXY_HTTPS, '')

            # create connection options
            options = {
                'server':           self.config.get(CONFIG_KEY_JIRA_ADDRESS, ''),
                'rest_path':        self.config.get(CONFIG_KEY_JIRA_REST_PATH, ''),
                'rest_api_version': self.config.get(CONFIG_KEY_JIRA_API_VERSION, ''),
                'verify':           False
            }

            self.log.info("JIRA: connection to server:\n\tuser -> {}\n\toptions -> {}".format(user, options))

            # todo: just a workaround to get rid of warnings :)
            import urllib3
            urllib3.disable_warnings()

            # connecting (and get instance of JIRA object)
            if http_proxy or https_proxy:  # connect through proxy
                proxies = {}
                if http_proxy:
                    proxies['http'] = http_proxy
                if https_proxy:
                    proxies['https'] = https_proxy
                self.log.info("Using proxy -> {}".format(proxies))
                self.__jira = JIRA(options=options, basic_auth=(user, password), proxies=proxies)
            else:  # direct connect
                self.__jira = JIRA(options=options, basic_auth=(user, password))

            self.log.info("JIRA: successfully connected.")
        else:
            self.log.info("JIRA: already connected.")

    def execute_jql(self, jql):
        """Utility method for executing JQL in JIRA and get resulting issues list.
        :param jql: JQL query to be executed in a JIRA
        :return: found raw issues list
        """
        self.log.debug("execute_jql() is working. JQL [{}].".format(jql))
        if not jql or not jql.strip():  # fast-fail check
            raise JiraException('Provided JQL is empty!')
        self.connect()
        # search for issues by provided jql and return them
        issues = []
        batch_size = CONST_JIRA_ISSUES_BATCH_SIZE
        total_processed = 0
        while batch_size == CONST_JIRA_ISSUES_BATCH_SIZE:
            # get issues part (in a size of batch, default = 50 - see CONST_JIRA_ISSUES_BATCH_SIZE)
            issues_batch = self.jira.search_issues(jql_str=jql, maxResults=False, startAt=total_processed)
            batch_size = len(issues_batch)  # update current batch size
            total_processed += batch_size   # update total processed count
            for issue in issues_batch:      # add all found issues to resulting list
                issues.append(issue)
        return issues

    def get_project_key(self, project_name):
        """Returns project key from JIRA by provided project name (return first occurrence).
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
        """Returns component object by specified name for specified project.
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

    def add_component_to_issues(self, issues, project_name, component_name):
        """Add specified component (from specified project) to issues list.
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
                if counter % consts.CONST_PROGRESS_STEP_COUNTER == 0:  # report progress
                    self.log.debug("Processed -> {}/{}".format(counter, len(issues)))
            self.log.debug("Summary: updated [{}] issue(s).".format(counter))
        else:
            self.log.debug("Component [{}] not found!".format(component_name))

    def add_label_to_issues(self, issues, label_name):
        """Add specified label to specified issues list.
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
            if counter % consts.CONST_PROGRESS_STEP_COUNTER == 0:  # report progress
                self.log.debug("Processed -> {}/{}".format(counter, len(issues)))
        self.log.debug("Summary: updated [{}] issue(s).".format(counter))

    def get_not_implements_tasks(self, issues):
        self.log.debug("get_not_implements_tasks() is working. Issues count [{}].".format(len(issues)))

        if not issues or len(issues) == 0:  # fail-fast
            raise JiraException("Empty issues list!")

        counter = 0
        found_issues = []
        for issue in issues:
            print("\nProcessing issue [%s]." % issue.key)
            # if issue has links - check them for target
            if issue.fields.issuelinks:
                is_link_found = False
                for link in issue.fields.issuelinks:
                    # outward links
                    if hasattr(link, "outwardIssue"):
                        outward_issue = link.outwardIssue
                        # debug output, switch it off in case of many output
                        print("\tOutward [{}], link type [{}], out type [{}], in type [{}]." \
                            .format(outward_issue.key, link.type, link.type.outward, link.type.inward))

                        # check issue type
                        if str(link.type) == IMPLEMENTS and str(link.type.outward) == IMPLEMENTS_TYPE:
                            print("Found link! Link: %s" % link.outwardIssue)
                            is_link_found = True
                            break  # we've found needed link, don't need to continue cycle

                    # inward links
                    # if hasattr(link, "inwardIssue"):
                    #     inwardIssue = link.inwardIssue
                    #     print("\tInward: " + inwardIssue.key)

                # (AFTER INTERNAL FOR) if outward link "Implements" not found -> add issue to issues list
                if not is_link_found:
                    found_issues.append(issue)

            else:  # issue doesn't have links, add to resulting list
                print("Issue [%s] doesn't have links." % issue.key)
                found_issues.append(issue)

            counter += 1
            if counter % consts.CONST_PROCESSING_STEP_COUNTER == 0:
                print("Processed [%s] issues." % counter)

    # def execute_option(self, option):
    #     self.log.debug("JIRAUtilityExtended.execute_option() is working. Option [{}].".format(option))
    #     if not option or not option.strip() or option not in JIRA_OPTIONS:
    #         raise JiraException("Invalid or empty option provided [%s]!" % option)
    #     # call corresponding method
    #     print "Calling method [%s]." % JIRA_OPTIONS[option]
    #     JIRA_OPTIONS[option](self)

    def create_issue(self, issue):
        self.log.info("Creating Jira issue [%s]." % issue)
        # self.jira.create_issue(fields=issue.)

    def update_issue(self, issue):
        self.log.info("Updating Jira issue [%s]." % issue)


class JiraException(Exception):
    """JIRA Exception, used if something is wrong with/in JIRA interaction."""


# options with corresponding method name
# JIRA_OPTIONS = {
#    'teamStatus':   JiraUtility.print_current_status_report,
#    'printClosed':  JiraUtility.print_closed_issues_report,
#    'sprintIssues': JiraUtility.print_sprint_issues_report,
#    'addComponent': JiraUtility.add_component_to_sprint_issues,
#    'addLabel':     JiraUtility.add_label_to_sprint_issues
# }


# some dirty testing...
if __name__ == '__main__':

    # init logging
    # LOG_IS_WORKING = '{}() is working.'
    setup_logging()
    log = logging.getLogger('jira_utility')
    log.info('Starting dirty testing for jira_utility... :)')

    # load config
    config_xls = Configuration(path_to_config='configs/jira.yml',
                               dict_to_merge={'jira.password': 'Vinnypuhh55'},
                               is_merge_env=False)

    log.info("Loaded XLS  Configuration:\n\t{}".format(config_xls.config_dict))

