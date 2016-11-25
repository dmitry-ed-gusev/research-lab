__author__ = 'alex'
from DataBase.DataBase import DataBase
from users.UsersClasses import getUserClass
def checkSuccessfulLogin(username):
    def f():
        with DataBase() as db:
            return db.isLoginEventLogged(username)
    return f

def checkUnSuccessfulLogin(username, password):
    def f():
        with DataBase() as db:
            return db.isFailLoginEventLogged(username, password)
    return f

def checkNSD(username):
    def f():
        with DataBase() as db:
            return db.isNSDCheckedLogged(username, getUserClass(username))
    return f