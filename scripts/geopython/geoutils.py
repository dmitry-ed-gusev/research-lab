#!/usr/bin/env python
# coding=utf-8

"""
    Some useful utilities for geo processing module.

    Created: Gusev Dmitrii, 04.02.2017
    Modified:
"""

import xlrd


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
