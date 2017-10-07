#!/usr/bin/env python
# coding=utf-8

"""
    This python file contains just list of useful constants for JIRA utilities/modules.
"""

# common constants/defaults
CONST_CONFIG_FILE = "configs/jira.yml"
CONST_NO_VALUE = "[none]"
CONST_PROGRESS_STEP_COUNTER = 5
CONST_TEAMS_LIST = ("ada", "ada_all", "nova", "nova_all", "bmtef", "bmtef_all")

# config file keys
CONFIG_KEY_CFG_FILE = "config.file"
CONFIG_KEY_ADDRESS = "jira.address"
CONFIG_KEY_USER = "jira.user"
CONFIG_KEY_PASS = "jira.password"
CONFIG_KEY_SPRINT = "sprint.name"

# config file key
CONFIG_KEY_TEAM_MEMBERS = "teams.%s.members"
# cmd line parameter stored here
CONFIG_KEY_TEAM_NAME = "team.name"

CONFIG_KEY_OUTPUT_FILE = "output.file"
CONFIG_KEY_DAYS_BACK = "days.back"
CONFIG_KEY_USE_SIMPLE_REPORT = "use.simple.report"
CONFIG_KEY_SHOW_LABEL_COLUMN = "show.label.column"

# JIRA specific constants
CONST_JIRA_ISSUES_BATCH_SIZE = 50
