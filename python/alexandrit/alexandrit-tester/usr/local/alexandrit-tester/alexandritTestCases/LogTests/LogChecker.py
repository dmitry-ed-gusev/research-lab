__author__ = 'alex'
from alexandritTestCases.ApacheAuthTests.decorators import TestException


class LogChecker(object):
    def __init__(self, description, checkFunction):
        self.checkFunction = checkFunction
        self.description = description

    def __call__(self, func):
        def wrapped(*args):
            result = func(*args)
            if not self.checkFunction():
                raise TestException(unicode(self.description))
            return result

        return wrapped