__author__ = 'alex'
import Admin
def getName():
    return Admin.getName()


def getPassword():
    return Admin.getPassword()


def getCredentials():
    return {
        "name": getName(),
        "password": getPassword()
    }