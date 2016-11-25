__author__ = 'alex'
import Admin, FakeAdmin, Operator, FakeOperator, User, FakeUser, WebAdmin, FakeWebAdmin, WebOperator, WebUser

USERS_FILE_PATH = "/var/www/html/ippbx/tests/test_mode/users"

class UsersModules:
    MODULES = {
        "s": Admin,
        "a": Operator,
        "u": User
    }

def parseUsersFile():
    with open(USERS_FILE_PATH) as uFile:
        users = [
            parseUserString(string.strip()) for string in uFile
        ]
    return users


def parseUserString(string):
    uType, name, password = string.split(":")
    return {
        "type": uType,
        "name": name,
        "pass": password
    }

def setCredentials(users):
    for user in users:
        UsersModules.MODULES[user["type"]].PASSWORD = user["pass"]
        UsersModules.MODULES[user["type"]].NAME = user["name"]

def setUsersSettings():
    setCredentials(parseUsersFile())

setUsersSettings()
