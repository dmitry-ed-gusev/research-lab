__author__ = 'alex'

NAME = "test_admin_04823334"
PASSWORD = "ta12345678"

def getName():
    return NAME


def getPassword():
    return PASSWORD


def getCredentials():
    return {
        "name": getName(),
        "password": getPassword()
    }