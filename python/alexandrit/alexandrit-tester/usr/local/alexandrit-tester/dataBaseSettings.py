__author__ = 'alex'
from users import Admin

HOST = "localhost"
PORT = None


def getDBName():
    return "asterisk"


def getDBUserName():
    return Admin.getName()


def getDBPassword():
    return Admin.getPassword()


def getHost():
    return HOST  #set None if local


def getPort():
    return PORT  #set None if local
