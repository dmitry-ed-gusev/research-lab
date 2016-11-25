__author__ = 'alex'
from ApacheAuthTests.AuthCommonTest import AuthCommonTest
from authorization.HTTPAuthorizationFactory import HTTPAuthorizationFactory
from WebAuthTests.WebAuthCommonTest import WebAuthCommonTest
from SpecialTests.SpecialCommonTest import SpecialCommonTest
import users

class TestCreator(object):
    @staticmethod
    def createAllTests():
        adminAuthorizer = HTTPAuthorizationFactory.getAuthorizationAdminObject()
        operatorAuthorizer = HTTPAuthorizationFactory.getAuthorizationOperatorObject()
        userAuthorizer = HTTPAuthorizationFactory.getAuthorizationUserObject()
        tests = []
        tests += AuthCommonTest.getAuthTestsList(adminAuthorizer, operatorAuthorizer, userAuthorizer)
        tests += WebAuthCommonTest.getAllWebAuthTests(adminAuthorizer, operatorAuthorizer, userAuthorizer)
        adminAuthorizer.setWebAuthorizer(users.WebAdmin)
        operatorAuthorizer.setWebAuthorizer(users.WebOperator)
        userAuthorizer.setWebAuthorizer(users.WebUser)
        tests += SpecialCommonTest.getTests(adminAuthorizer, operatorAuthorizer, userAuthorizer)
        return tests
