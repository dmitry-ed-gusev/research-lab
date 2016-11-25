__author__ = 'alex'
import User
def getName():
    return User.getName()


def getPassword():
    return User.getPassword()


def getCredentials():
    return {
        "name": getName(),
        "password": getPassword()
    }