#  -*- coding: utf-8 -*-
__author__ = 'alex'
import psycopg2
import datetime


class EventTypesEnum:
    CHECKSUM_ERROR_FOUND = 0
    FILE_RESTORED_SUCCESS = 1
    START = 2
    STOP = 3
    CONFIG_LOAD_FAIL = 4
    MODULE_FAIL = 5


class Event(object):
    def __init__(self, type):
        self.type = type

class ModuleFailEvent(Event):
    def __init__(self, error):
        super(ModuleFailEvent, self).__init__(EventTypesEnum.MODULE_FAIL)
        self.filePath = filePath


class CheckSumErrorEvent(Event):
    def __init__(self, filePath):
        super(CheckSumErrorEvent, self).__init__(EventTypesEnum.CHECKSUM_ERROR_FOUND)
        self.filePath = filePath


class FileRestoredSuccessEvent(Event):
    def __init__(self, filePath):
        super(FileRestoredSuccessEvent, self).__init__(EventTypesEnum.FILE_RESTORED_SUCCESS)
        self.filePath = filePath


class StartEvent(Event):
    def __init__(self):
        super(StartEvent, self).__init__(EventTypesEnum.START)


class StopEvent(Event):
    def __init__(self):
        super(StopEvent, self).__init__(EventTypesEnum.STOP)


class ConfigLoadFailEvent(Event):
    def __init__(self):
        super(ConfigLoadFailEvent, self).__init__(EventTypesEnum.CONFIG_LOAD_FAIL)


class Logger(object):
    user = None
    password = None
    dbName = None
    logger = None

    @staticmethod
    def set_credentials(user, password, dbname):
        Logger.user = user
        Logger.password = password
        Logger.dbName = dbname

    def __init__(self):
        if Logger.user == None or Logger.password == None:
            raise Exception("Set credentials first!")
        self.conn = psycopg2.connect(host="localhost", database=Logger.dbName, user=Logger.user, password=Logger.password)

    @staticmethod
    def getLogger():
        if Logger.logger == None:
            Logger.logger = Logger()
        return Logger.logger

    @staticmethod
    def onExit():
        if Logger.logger != None:
            Logger.logger.conn.commit()
            Logger.logger.conn.close()

    @staticmethod
    def log(text, success=True):
        logger = Logger.getLogger().logger
        text = "<tr><td>" + text + "</td></tr>"
        dt = datetime.datetime.now().replace(microsecond=0)
        logger.conn.cursor().execute(
            "INSERT INTO events (event_date, event_time, event_note, event_status, event_type) VALUES (%s, %s, %s, %s, %s)",
            (dt.date(), dt.time(), text, 1, 5))
        logger.conn.commit()


def log(event):
    handlers = {
        EventTypesEnum.CHECKSUM_ERROR_FOUND: logChecksumError,
        EventTypesEnum.FILE_RESTORED_SUCCESS: logFileRestoredSuccess,
        EventTypesEnum.START: logStart,
        EventTypesEnum.STOP: logStop,
        EventTypesEnum.CONFIG_LOAD_FAIL: log_configError,
        EventTypesEnum.MODULE_FAIL : logModuleFail
    }
    handlers[event.type](event)


def logModuleFail(e):
    message = u"Авария модуля безопасности"
    Logger.log(message)

def log_configError(e):
    message = u"Ошибка загрузки списка файлов"
    Logger.log(message)


def logStop(e):
    message = u"Модуль безопасности остановлен"
    Logger.log(message)


def logStart(e):
    message = u"Модуль безопасности запущен"
    Logger.log(message)


def logFileRestoredSuccess(e):
    message = u"Файл успешно восстановлен " + e.filePath
    Logger.log(message)


def logChecksumError(e):
    message = u"Обнаружно нарушение целостности файла " + e.filePath
    Logger.log(message)
