#!/usr/bin/env python
# coding=utf-8

"""
    Script for loading commissions data from excel into db (sqlite).
    Pandas isn't enough for this script. xlrd - for reading, xlwt - fro writing excel files.
"""

import os
import logging
import xlrd  # most suitable for xls
# import pandas as pd  # most suitable for xslx
import sqlite3 as sql
from pylib.pyutilities import setup_logging

# common constants
LOGGER_NAME = 'geoprocessor'
LOGGER_CONFIG = 'logging.yml'
DB_NAME = 'geodata.sqlite'
XLS_SOURCE_FILE = 'nw-uiks.xls'
# database script
DB_SCRIPT = """
    -- drop tables
    DROP TABLE IF EXISTS areas;
    DROP TABLE IF EXISTS commissions;
    DROP TABLE IF EXISTS addresses;
    -- create tables
    CREATE TABLE areas (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT);
    CREATE TABLE commissions(id INTEGER PRIMARY KEY AUTOINCREMENT, city TEXT, 
      territory_commission TEXT, sector_commission TEXT);
    CREATE TABLE addresses(id INTEGER PRIMARY KEY AUTOINCREMENT, street TEXT, buildings TEXT, 
      commission_id INTEGER REFERENCES commissions(id) ON DELETE RESTRICT);
"""

# todo: move db-related things to geodb.py file!

# module initialization
setup_logging(default_path='logging.yml')
log = logging.getLogger(LOGGER_NAME)


def db_create(dbname):
    log.debug('db_create: creating database structure.')
    # connect to sqlite db
    conn = sql.connect(dbname)
    cur = conn.cursor()
    log.debug('Connected to DB [{}].'.format(dbname))
    # execute db setup script
    for query in DB_SCRIPT.split(';'):
        cur.execute(query)
    log.debug('DB structure created.')


def db_add_areas(dbname, areas_list):
    """
    Add multiple areas at a time.
    :param dbname:
    :param areas_list:
    :return:
    """
    log.debug('db_add_areas(): adding areas {}.'.format(areas_list))
    insert_sql = 'INSERT INTO areas(name) VALUES ("{}")'
    # connect to db and in cycle insert records
    connection = sql.connect(dbname)
    cursor = connection.cursor()
    log.debug('Connected to DB [{}].'.format(dbname))
    for area in areas_list:
        cursor.execute(insert_sql.format(area))
    connection.commit()
    log.debug('All areas added.')


def db_add_commissions(dbname, commissions_list):
    """
    Add multiple commissions at a time.
    :param dbname:
    :param commissions_list:
    :return:
    """
    log.debug('db_add_commissions(): adding commissions.')


def db_add_commission(dbname, city, territory_commission, sector_commission):
    """
    Add one commisiion at a time, return inserted id.
    :param dbname:
    :param city:
    :param territory_commission:
    :param sector_commission:
    :return:
    """
    log.debug('db_add_commission(): adding commission [{}, {}, {}].'
              .format(city, territory_commission, sector_commission))


def load_xls_data(xls_file):
    log.debug('load_xls_data(): loading data from source file [{}].'.format(xls_file))

    # Load the whole spreadsheet
    # excel = pd.ExcelFile(XLS_SOURCE_FILE)
    excel_book = xlrd.open_workbook(XLS_SOURCE_FILE, encoding_override='cp1251')

    # load areas in database
    sheets_names = []
    sheets = []
    for sheet in excel_book.sheet_names():  # excel.sheet_names:
        sheets_names.append(sheet.encode('utf-8'))  # encode value to UTF-8

        sheet_object = excel_book.sheet_by_name(sheet)
        sheets.append(sheet_object)

        #process_excel_sheet(sheet_object)

        #vals = [sheet_object.row_values(rownum) for rownum in range(sheet_object.nrows)]
        #print '\n->', vals

    sheet = excel_book.sheet_by_index(0)
    print 'sheet name:', sheet.name

    city = ''
    territory_commission = ''
    sector_commission = ''
    commissions_list = []
    for rownumber in range(30):  # range(sheet.nrows):
        if rownumber == 0 or rownumber == 1:  # skip first row
            continue

        for colnumber in range(sheet.ncols):
            value = sheet.cell_value(rownumber, colnumber)
            value_type = sheet.cell_type(rownumber, colnumber)

            if value and value_type != xlrd.XL_CELL_EMPTY:  # cell isn't empty
                if colnumber == 0:  # city name
                    city = value
                elif colnumber == 1:  # territory commission
                    territory_commission = value
                elif colnumber == 2:  # sector commission
                    sector_commission = value
                    commissions_list.append([city, territory_commission, sector_commission])

            print 'cell[{}, {}]:'.format(rownumber, colnumber), \
                sheet.cell_value(rownumber, colnumber), 'type: ', sheet.cell_type(rownumber, colnumber)

            if sheet.cell_type(rownumber, colnumber) == xlrd.XL_CELL_NUMBER:
                print '!!!', int(sheet.cell_value(rownumber, colnumber))
        print '\n===============================================\n'

    #db_add_areas(DB_NAME, sheets_names)

    # Load a sheet into a DataFrame by name: df1
    # df1 = xl.parse('Sheet1')


if __name__ == '__main__':
    log.info('Starting GeoProcessor module...')

    if not os.path.exists(DB_NAME):
        log.warn("Database [{}] doesn't exist! Creating...".format(DB_NAME))
        db_create(DB_NAME)  # create target db

    # load data from xls file
    load_xls_data(XLS_SOURCE_FILE)
