__author__ = 'alex'
import random, string

def randomword(length):
   return ''.join(random.choice(string.lowercase) for i in range(length))

def getName():
    return "admin"


def getPassword():
    return randomword(10)


def getCredentials():
    return {
        "name": getName(),
        "password": getPassword()
    }