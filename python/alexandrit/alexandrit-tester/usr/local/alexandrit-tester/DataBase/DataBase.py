__author__ = 'alex'
import psycopg2
import dataBaseSettings
import users
import datetime


class DataBase(object):
    dbEngine = psycopg2

    def __enter__(self):
        self.connection = DataBase.dbEngine.connect(database=dataBaseSettings.getDBName(),
                                                    user=dataBaseSettings.getDBUserName(),
                                                    password=dataBaseSettings.getDBPassword(),
                                                    host=dataBaseSettings.getHost(),
                                                    port=dataBaseSettings.getPort())
        self.cur = self.connection.cursor()
        return self

    def __exit__(self, type, value, traceback):
        self.connection.commit()
        self.cur.close()
        self.connection.close()

    def getUserSession(self, user, sessid):
        self.cur.execute("SELECT 1 AS result FROM web_users WHERE login=%s AND session_id=%s", (user, sessid))
        return self.cur.fetchall()

    def cleanUserSession(self, user):
        self.cur.execute("UPDATE web_users SET session_id='' WHERE login=%s", (user,))

    def isLoginEventLogged(self, login):
        self.cur.execute("select * from events where user_login=%s AND event_type=1 AND event_status=1", (login,))
        return len(self.cur.fetchall()) > 0

    def isFailLoginEventLogged(self, login, password):
        self.cur.execute(
            "select * from events where user_login=%s AND event_type=1 AND event_status=0 and event_note like %s",
            (login, "%" + password + "%",))
        res = self.cur.fetchall()
        return len(res) > 0

    def isNSDCheckedLogged(self, username, className):
        self.cur.execute(
            "select * from events where user_login=%s AND event_type=5 AND event_status=0 and event_note like %s",
            (username, "%" + className + "%",))
        res = self.cur.fetchall()
        return len(res) > 0

    def logTestResult(self, res):
        dt = datetime.datetime.now().replace(microsecond=0)
        date = dt.date()
        time = dt.time()
        note = "<tr><td>" + res.description + "</td></tr>"
        result = res.result
        self.cur.execute(
            "INSERT INTO events (event_date, event_time, event_type, event_note, event_status) VALUES (%s, %s, %s, %s, %s)",
            (date, time, 5, note, 1 if result else 0))

    def cleanAllAuthEvents(self):
        adminName = users.WebAdmin.getName()
        operatorName = users.WebOperator.getName()
        userName = users.WebUser.getName()
        self.cur.execute("delete from events where user_login=%s or user_login=%s or user_login=%s",
                         (adminName, operatorName, userName))
