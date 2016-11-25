__author__ = 'alex'
import Operator
def getName():
    return Operator.getName()


def getPassword():
    return Operator.getPassword()


def getCredentials():
    return {
        "name": getName(),
        "password": getPassword()
    }