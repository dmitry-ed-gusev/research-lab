__author__ = 'alex'

NAME = "test_super_82496601"
PASSWORD = "ts12345678"
def getName():
    return NAME


def getPassword():
    return PASSWORD


def getCredentials():
    return {
        "name": getName(),
        "password": getPassword()
    }