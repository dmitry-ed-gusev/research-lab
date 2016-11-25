__author__ = 'alex'
import random
import string


def randomword(length):
   return ''.join(random.choice(string.lowercase) for i in range(length))