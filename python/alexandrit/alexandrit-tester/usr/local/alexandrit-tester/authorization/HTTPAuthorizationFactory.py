__author__ = 'alex'

from Authorizer import Authorizer
import users


class HTTPAuthorizationFactory(object):
    @staticmethod
    def getAuthorizationObject(user):
        return Authorizer(user)

    @staticmethod
    def getAuthorizationAdminObject():
        return HTTPAuthorizationFactory.getAuthorizationObject(users.Admin)

    @staticmethod
    def getAuthorizationFakeAdminObject():
        return HTTPAuthorizationFactory.getAuthorizationObject(users.FakeAdmin)

    @staticmethod
    def getAuthorizationOperatorObject():
        return HTTPAuthorizationFactory.getAuthorizationObject(users.Operator)

    @staticmethod
    def getAuthorizationFakeOperatorObject():
        return HTTPAuthorizationFactory.getAuthorizationObject(users.FakeOperator)

    @staticmethod
    def getAuthorizationUserObject():
        return HTTPAuthorizationFactory.getAuthorizationObject(users.User)

    @staticmethod
    def getAuthorizationFakeUserObject():
        return HTTPAuthorizationFactory.getAuthorizationObject(users.FakeUser)