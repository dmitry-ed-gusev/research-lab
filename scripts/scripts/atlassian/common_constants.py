#!/usr/bin/env python
# coding=utf-8

"""
    List of useful common constants for all modules.
    Created: Gusev Dmitrii, 17.12.2017
    Modified: Gusev Dmitrii, 20.02.2017
"""

# loggers for utilities
LOGGER_NAME_JIRAUTIL = 'jirautil'
LOGGER_NAME_GITUTIL = 'gitutil'
LOGGER_NAME_CONFUTIL = 'confutil'

# common constants/defaults
CONST_GIT_CONFIG_FILE = "configs/git.yml"
CONST_JIRA_CONFIG_FILE = "configs/jira.yml"
CONST_NO_VALUE = "[none]"
CONST_PROGRESS_STEP_COUNTER = 5
CONST_PROCESSING_STEP_COUNTER = 30
CONST_COMMON_ENCODING = "utf-8"
# teams list, should be correlated to teams list in config file
CONST_TEAMS_LIST = ("ada", "ada_all", "nova", "nova_all", "bmtef", "bmtef_all")
# JIRA specific constants
CONST_JIRA_ISSUES_BATCH_SIZE = 50

# settings for Maven build (not on msd machine)
CONFIG_KEY_MVN_SETTINGS = 'mvn_settings'
# option - clone repositories
CONFIG_KEY_GIT_CLONE = 'git-clone'
# option - switch build off
CONFIG_KEY_MVN_BUILD_OFF = 'no-build'
# options - download javadoc
CONFIG_KEY_MVN_JAVADOC = 'javadoc'
CONFIG_KEY_MVN_SOURCES = 'sources'

# keys for proxy (common)
CONFIG_KEY_PROXY_HTTP = 'proxy.http'
CONFIG_KEY_PROXY_HTTPS = 'proxy.https'
# key for argparse -> config file name (common)
CONFIG_KEY_CFG_FILE = "config.file"

# config keys for JIRA
CONFIG_KEY_JIRA_ADDRESS = "jira.address"
CONFIG_KEY_JIRA_REST_PATH = 'jira.rest_path'
CONFIG_KEY_JIRA_API_VERSION = 'jira.rest_api_version'
CONFIG_KEY_JIRA_USER = "jira.user"
CONFIG_KEY_JIRA_PASS = "jira.password"

# config keys for GIT/Stash
CONFIG_KEY_STASH_ADDRESS = "stash.address"
CONFIG_KEY_STASH_USER = "stash.user"
CONFIG_KEY_STASH_PASS = "stash.password"

# keys for repositories hierarchy
CONFIG_KEY_REPO = 'repositories'
CONFIG_KEY_REPO_KEY = 'repositories.{}'
CONFIG_KEY_REPO_BUILD = 'repositories.{}.build'

CONFIG_KEY_REP_LOCATION_WIN = 'location.win'
CONFIG_KEY_REP_LOCATION_LINUX = 'location.linux'
CONFIG_KEY_REP_LOCATION_MACOS = 'location.macos'

# config key -> sprint name
CONFIG_KEY_SPRINT = "sprint.name"
# config key -> JQL query for direct execution
CONFIG_KEY_JQL = "jql.query"
# config key -> project name
# CONFIG_KEY_PROJECT = "project.name"
# config key -> component name
# CONFIG_KEY_COMPONENT = "component.name"
# todo: replace % formatting with {} formatting
# config file key -> team members, team specified by placeholder
CONFIG_KEY_TEAM_MEMBERS = "teams.%s.members"
CONFIG_KEY_TEAM_PROJECT = "teams.%s.project"
CONFIG_KEY_TEAM_PROJECT_ABBR = "teams.%s.project_abbr"
CONFIG_KEY_TEAM_LABEL = "teams.%s.label"
CONFIG_KEY_TEAM_COMPONENT = "teams.%s.component"
# team name - cmd line parameter stored here
CONFIG_KEY_TEAM_NAME = "team.name"
# config key -> option to execute
CONFIG_KEY_OPTION = "execute.option"
# config key -> output file for report writing
CONFIG_KEY_OUTPUT_FILE = "output.file"
CONFIG_KEY_DAYS_BACK = "days.back"
CONFIG_KEY_USE_SIMPLE_REPORT = "use.simple.report"
CONFIG_KEY_SHOW_LABEL_COLUMN = "show.label.column"
