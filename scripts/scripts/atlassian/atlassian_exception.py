#!/usr/bin/env python
# coding=utf-8

"""

    Internal exception for atlassian scripts.

    Created:  Dmitrii Gusev, 25.05.2019
    Modified:

"""


class AtlassianException(Exception):

    def __init__(self, msg: str):
        self.msg = msg

    def __str__(self):  # string representation for print() etc.
        return repr(self.msg)
