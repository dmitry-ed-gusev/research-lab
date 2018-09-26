#!/usr/bin/python
#  -*- coding: utf-8 -*-

"""
    Common utilities in python. Can be useful in different cases.
    Created: Gusev Dmitrii, 04.04.2017
    Modified: Gusev Dmitrii, 26.09.2018
"""

import os
import errno
import sys
import csv
import yaml
import xlrd
import logging
import logging.config
from os import walk
from subprocess import Popen

# configure logger on module level. it isn't a good practice, but it's convenient.
# don't forget to set disable_existing_loggers=False, otherwise logger won't get its config!
log = logging.getLogger(__name__)
# to avoid errors like 'no handlers' for libraries it's necessary to add NullHandler.
log.addHandler(logging.NullHandler())

# some useful constants
GIT_EXECUTABLE = 'git'


def setup_logging(default_path='configs/logging.yml', default_level=logging.INFO, env_key='LOG_CFG'):
    """
        Setup logging configuration - load it from YAML file.
        :param default_path path to logging config YAML file
        :param default_level default logging level - INFO
        :param env_key environment variable key to override settings from cmd line,
               like LOG_CFG=my_logging_config.yml
    """
    path = default_path
    value = os.getenv(env_key, None)
    if value:
        path = value
    if os.path.exists(path):
        with open(path, 'rt') as f:
            config = yaml.safe_load(f.read())
        logging.config.dictConfig(config)
        log.info('Loaded logging config from [{}].'.format(path))
    else:
        logging.basicConfig(level=default_level)
        log.info('Using basic logging config. Can"t load config from [{}].'.format(path))


def count_lines(filename):
    """
    Count lines in any given file.
    :return: count of lines
    """
    counter = 0
    # open file, received as first cmd line argument, mode - read+Unicode
    with open(filename, mode='rU') as infile:
        # skip initial space - don't work without it
        reader = csv.reader(infile, delimiter=b',', skipinitialspace=True, quoting=csv.QUOTE_MINIMAL, quotechar=b'"',
                            lineterminator="\n")
        # counting rows in a cycle
        for _ in reader:
            # print row  # <- just a debug output
            counter += 1
    # debug - print count to console
    print "Lines count: {}".format(counter)
    return counter


def _list_files(path, files_buffer, out_to_console=False):
    """
    Internal function for listing (recursively) all files in specified directory.
    Don't use it directly, use list_files()
    :param path: path to iterate through
    :param files_buffer: buffer list for collection files
    :param out_to_console: out to console processing file
    """
    # print "STDOUT encoding ->", sys.stdout.encoding  # <- just a debug output
    for (dirpath, dirnames, filenames) in walk(unicode(path)):
        for filename in filenames:
            abs_path = dirpath + '/' + filename
            if out_to_console:  # debug output
                if sys.stdout.encoding is not None:  # sometimes encoding may be null!
                    print abs_path.encode(sys.stdout.encoding, errors='replace')
                else:
                    print abs_path
            files_buffer.append(abs_path)


def list_files(path, out_to_console=False):
    """
    List all files in a specified path and return list of found files.
    :param path: path to directory
    :param out_to_console: do or don't output to system console
    :return: list of files
    """
    log.debug("list_files() is working. Path [{}].".format(path))
    if not path or not path.strip():  # fail-fast #1
        raise IOError("Can't list files in empty path!")
    if not os.path.exists(path) or not os.path.isdir(path):  # fail-fast #2
        raise IOError("Path [{}] doesn't exist or not a directory!".format(path))
    files = []
    _list_files(path, files, out_to_console)
    return files


def parse_yaml(file_path):
    """
    Parses single YAML file and return its contents as object (dictionary).
    :param file_path: path to YAML file to load settings from
    :return python object with YAML file contents
    """
    log.debug("parse_yaml() is working. Parsing YAML file [{}].".format(file_path))
    if not file_path or not file_path.strip():
        raise IOError("Empty path to YAML file!")
    with open(file_path, 'r') as cfg_file:
        cfg_file_content = cfg_file.read()
        if "\t" in cfg_file_content:  # no tabs allowed in file content
            raise IOError("Config file [{}] contains 'tab' character!".format(file_path))
        return yaml.load(cfg_file_content)


def git_set_global_proxy(http=None, https=None):  # todo: unit tests! make it decorator.
    """
    Set specified proxies (both http/https) for local git globally.
    :param http:
    :param https:
    """
    log.debug("git_set_global_proxy() is working. Setting proxies: http -> [{}], https -> [{}]".format(http, https))
    if http:
        log.debug("Setting HTTP proxy: {}".format(http))
        process = Popen([GIT_EXECUTABLE, 'config', '--global', 'http.proxy', http])
        process.wait()
    if https:
        log.debug("Setting HTTPS proxy: {}".format(http))
        process = Popen([GIT_EXECUTABLE, 'config', '--global', 'https.proxy', https])
        process.wait()


def git_clean_global_proxy():  # todo: unit tests! make it decorator.
    """
    Clear git global proxies (both http/https).
    """
    log.debug("git_clean_global_proxy() is working.")
    process = Popen([GIT_EXECUTABLE, 'config', '--global', '--unset', 'http.proxy'])
    process.wait()
    process = Popen([GIT_EXECUTABLE, 'config', '--global', '--unset', 'https.proxy'])
    process.wait()


def save_file_with_path(file_path, content):  # todo: move it to utilities module
    log.debug('save_file_with_path(): saving content to [{}].'.format(file_path))
    if not os.path.exists(os.path.dirname(file_path)):
        try:
            os.makedirs(os.path.dirname(file_path))
        except OSError as exc:  # guard against race condition
            if exc.errno != errno.EEXIST:
                raise
    # write content to a file
    with open(file_path, "w") as f:
        f.write(content)


# todo: functions (decorators) below are copied from inet :) - take a look
def benchmark(func):
    """
    Декоратор, выводящий время, которое заняло
    выполнение декорируемой функции.
    """
    import time

    def wrapper(*args, **kwargs):
        t = time.clock()
        res = func(*args, **kwargs)
        print func.__name__, time.clock() - t
        return res
    return wrapper


def logger(func):
    """
    Декоратор, логирующий работу кода.
    (хорошо, он просто выводит вызовы, но тут могло быть и логирование!)
    """
    def wrapper(*args, **kwargs):
        res = func(*args, **kwargs)
        print func.__name__, args, kwargs
        return res
    return wrapper


def counter(func):
    """
    Декоратор, считающий и выводящий количество вызовов
    декорируемой функции.
    """
    def wrapper(*args, **kwargs):
        wrapper.count += 1
        res = func(*args, **kwargs)
        print "{0} была вызвана: {1}x".format(func.__name__, wrapper.count)
        return res
    wrapper.count = 0
    return wrapper


def filter_str(string):
    """
    Filter out all symbols from string except letters, numbers, spaces, commas.
    By default, decode input string in unicode (utf-8).
    :param string:
    :return:
    """
    if not string or not string.strip():  # if empty, return 'as is'
        return string
    # filter out all, except symbols, spaces, or comma
    return ''.join(char for char in string if char.isalnum() or char.isspace() or
                   char in 'абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ.,/-№')


def get_str_val(value, value_type, encoding):
    if xlrd.XL_CELL_EMPTY == value_type or xlrd.XL_CELL_BLANK == value_type:
        return ''
    elif xlrd.XL_CELL_NUMBER == value_type:
        return filter_str(str(int(value)).encode(encoding))
    elif xlrd.XL_CELL_TEXT == value_type:
        return filter_str(value.encode(encoding))
    else:
        return filter_str(str(value))


def get_int_val(value, value_type, encoding):
    if xlrd.XL_CELL_EMPTY == value_type or xlrd.XL_CELL_BLANK == value_type:
        return 0
    elif xlrd.XL_CELL_NUMBER == value_type:
        return int(value)
    else:
        raise StandardError("Can't convert value [{}] to integer!".format(value.encode(encoding)))


if __name__ == '__main__':
    print "pyutilities: Don't try to execute library as standalone app!"
    # list_files('/media/vinnypuhh/MyData/Cloud/YandexDisk/DOCS AND BOOKS')
    # list = list_files('/media/vinnypuhh/MyData', True)
    # print len(list)
