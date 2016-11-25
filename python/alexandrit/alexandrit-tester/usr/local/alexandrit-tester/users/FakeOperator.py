__author__ = 'alex'
import random, string
import Operator
def randomword(length):
   return ''.join(random.choice(string.lowercase) for i in range(length))

def getName():
    return randomword(15)


def getPassword():
    return Operator.getPassword()


def getCredentials():
    return {
        "name": getName(),
        "password": getPassword()
    }