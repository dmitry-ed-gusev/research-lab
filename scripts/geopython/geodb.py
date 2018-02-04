#!/usr/bin/env python
# coding=utf-8

import logging
from pylib.pyutilities import setup_logging
import sqlite3 as sql

# init module logging
setup_logging(default_path='logging.yml')
log = logging.getLogger(__name__)
log.addHandler(logging.NullHandler())

# common constants
DB_NAME = 'geodata.sqlite'
# database script
DB_SCRIPT = """
    -- drop tables
    DROP TABLE IF EXISTS areas;
    DROP TABLE IF EXISTS commissions;
    DROP TABLE IF EXISTS addresses;
    -- create tables
    CREATE TABLE areas (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT);
    CREATE TABLE commissions(id INTEGER PRIMARY KEY AUTOINCREMENT, city TEXT, 
      territory_commission TEXT, sector_commission TEXT, people_count INTEGER);
    CREATE TABLE addresses(id INTEGER PRIMARY KEY AUTOINCREMENT, street TEXT, buildings TEXT, 
      commission_id INTEGER REFERENCES commissions(id) ON DELETE RESTRICT);
"""


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


def db_add_commission(dbname, city, territory_commission, sector_commission, people_count):
    """
    Add one commission at a time, return inserted id.
    :param dbname:
    :param city:
    :param territory_commission:
    :param sector_commission:
    :param people_count:
    :return:
    """
    log.debug('db_add_commission(): adding commission [{}, {}, {}, {}].'
              .format(city, territory_commission, sector_commission, people_count))
    insert_sql = "INSERT INTO commissions(city, territory_commission, sector_commission, people_count) " \
                 "VALUES ('{}', '{}', '{}', {})".format(city, territory_commission, sector_commission, people_count)
    connection = sql.connect(dbname)
    cursor = connection.cursor()
    log.debug('Connected to DB [{}].'.format(dbname))
    cursor.execute(insert_sql)
    last_id = cursor.lastrowid
    connection.commit()
    log.debug('Last inserted id = [{}].'.format(last_id))
    return last_id


def db_add_address(dbname, street, buildings, commission_id):
    """
    Add one address at a time, return inserted id.
    :param dbname:
    :param street:
    :param buildings:
    :param commission_id:
    :return:
    """
    log.debug('db_add_address(): adding address [{}, {}, {}].'
              .format(street, buildings, commission_id))
    insert_sql = "INSERT INTO addresses(street, buildings, commission_id) VALUES ('{}', '{}', '{}')" \
        .format(street, buildings, commission_id)
    connection = sql.connect(dbname)
    cursor = connection.cursor()
    log.debug('Connected to DB [{}].'.format(dbname))
    cursor.execute(insert_sql)
    last_id = cursor.lastrowid
    connection.commit()
    log.debug('Last inserted id = [{}].'.format(last_id))
    return last_id


if __name__ == '__main__':
    print "Don't execute library as an application!"
