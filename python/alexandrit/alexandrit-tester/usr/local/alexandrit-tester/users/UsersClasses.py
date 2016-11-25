# coding=utf-8
import users
__author__ = 'alex'
class UsersClasses:
    ADMIN = u"Администратор"
    OPERATOR = u"Оператор"
    USER = u"Пользователь"
    @staticmethod
    def getUserNameListsWithClasses():
        usernames = {
            users.Admin.getName() : {
                "name" : users.Admin.getName(),
                "class" : UsersClasses.ADMIN
            }
            , users.Operator.getName() : {
                "name" : users.Operator.getName(),
                "class" : UsersClasses.OPERATOR
            }
            , users.User.getName() : {
                "name" : users.User.getName(),
                "class" : UsersClasses.USER
            }
        }
        return usernames


def getUserClass(username):
    return UsersClasses.getUserNameListsWithClasses()[username]["class"]