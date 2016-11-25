__author__ = 'alex'
import random, string
import User
def randomword(length):
   return ''.join(random.choice(string.lowercase) for i in range(length))

def getName():
    return randomword(15)


def getPassword():
    return User.getPassword()


def getCredentials():
    return {
        "name": getName(),
        "password": getPassword()
    }