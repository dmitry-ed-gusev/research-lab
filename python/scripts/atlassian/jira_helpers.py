#!/usr/bin/env python
# coding=utf-8

"""
    Some useful JIRA helper methods.

    Created:  Gusev Dmitrii, 12.01.2019
    Modified: Gusev Dmitrii, 04.03.2019
"""

import logging
from pyutilities.pylog import setup_logging

# todo: move these tricks with logging (below) to pyutilities module
# trick for getting name of working procedure
import inspect
myself = lambda: inspect.stack()[1][3]
# log message template for debugging
LOG_IS_WORKING = '{}() is working.'

# setup logging/init logger for current script
setup_logging()
log = logging.getLogger('jira_helpers')

# list of marks for not existing issue
NOT_EXISTING_ISSUE_MARKS = ['-', '???']


def is_key_empty(key):
    return False if key and key.strip() and key.strip() not in NOT_EXISTING_ISSUE_MARKS else True  # ternary operator


def get_issue_key(epic_key, story_key, task_key):
    """Return issue key from three keys - epic/story/task. In case of error - raise StandardError.
    :param epic_key:
    :param story_key:
    :param task_key:
    :return:
    """
    # log.debug(LOG_IS_WORKING.format(myself()))  # <- too much output

    if is_key_empty(epic_key) and is_key_empty(story_key) and is_key_empty(task_key):  # fast check
        return None

    # check key presence
    if not is_key_empty(epic_key) and is_key_empty(story_key) and is_key_empty(task_key):
        return epic_key.strip()
    elif not is_key_empty(story_key) and is_key_empty(epic_key) and is_key_empty(task_key):
        return story_key.strip()
    elif not is_key_empty(task_key) and is_key_empty(epic_key) and is_key_empty(story_key):
        return task_key.strip()

    raise ValueError("Invalid state: specified more than one key!")  # nothing found - invalid state
