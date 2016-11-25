# coding=utf-8
__author__ = 'alex'

import testDescriptions

class TestFunction(object):
    def __init__(self, description=None):
        self.description = description

    def __call__(self, func):
        if self.description is None:
            self.description = testDescriptions.TestDescriptions[func.__name__]
        def wrapped(*args):
            result = func(*args)
            if not result:
                raise TestException(unicode(self.description))
            return result

        return wrapped

class TestException(Exception):
    def __init__(self, msg):
        self.message = unicode(msg)
        super(TestException, self).__init__()