#!/usr/bin/env python
# coding=utf-8

"""
    Created: Gusev Dmitrii, 05.02.2017
    Modified: Gusev Dmitrii, 05.02.2017
"""

import os
import logging
import json
import urllib2
from sqlite3 import IntegrityError
from pylib.pyutilities import setup_logging
from geodb import DB_NAME, db_create, db_add_single_geo_point, db_get_not_processed_geo_points_ids, \
    db_mark_geo_point_as_processed, db_add_multiple_geo_points, db_get_geo_point_id

# module initialization
setup_logging(default_path='logging.yml')
log = logging.getLogger('geocik')

# some useful constants
JSON_ENCODING = 'windows-1251'
DATA_ENCODING = 'utf-8'

# useful URLs for processing
URL_TOP = 'http://cikrf.ru/services/lk_tree'
URL_POINT = 'http://cikrf.ru/services/lk_tree/?id={}'
URL_MSK = 'http://cikrf.ru/services/lk_tree/?ret=0&id={}'
URL_SPB = 'http://cikrf.ru/services/lk_tree/?ret=1&id={}'
URL_SPB_AREA = 'http://cikrf.ru/services/lk_tree/?ret=0&id={}'


def add_geo_points(json_points, parent_id, batching=True):
    log.debug('add_geo_points(): adding geo points to db')

    # iterate over children and put them to db
    points_list = []
    for point in json_points:
        point_id = point['id']
        point_text = point['text'].encode(DATA_ENCODING)
        point_children = point['children']
        point_intid = point['a_attr']['intid']
        if not point_intid:
            point_intid = 'NULL'
        point_levelid = point['a_attr']['levelid']

        if batching:  # if batching -> add point to list
            points_list.append([point_id, point_intid, point_text, point_levelid, point_children, parent_id, 0])
        else:  # if not batching -> directly add geo point (one by one)
            try:
                db_add_single_geo_point(DB_NAME, point_id, point_intid, point_text, point_levelid,
                                        point_children, parent_id)
            except IntegrityError as ie:
                log.warn('Geo point already exists! Message: {}'.format(ie.message))

    if batching:
        # add a bunch of points (batch)
        db_add_multiple_geo_points(DB_NAME, points_list)


def init_geo_points(pretty_debug=False):
    """
    Initializing existing (!) geo points db. Operation is idempotent!
    :param pretty_debug:
    :return:
    """
    log.debug('init_geo_points(): initializing.')

    # get top level json from cikrf web-site
    myjson = json.load(urllib2.urlopen(URL_TOP), encoding=JSON_ENCODING)

    # pretty print json (just debug)
    if pretty_debug:
        print json.dumps(myjson, sort_keys=True, indent=4, encoding='windows-1251')

    # get first geo point from json
    id = myjson[0]['id']
    text = myjson[0]['text'].encode('utf-8')
    children = 'True'
    intid = myjson[0]['a_attr']['intid']
    levelid = myjson[0]['a_attr']['levelid']

    # just debug
    log.debug('id -> {}, text -> {}, children -> {}, intid -> {}, levelid -> {}'
              .format(id, text, type(children), intid, levelid))

    try:
        # add first point to db
        last_id = db_add_single_geo_point(DB_NAME, id, intid, text, levelid, children, 0, processed=1)
    except IntegrityError as ie:
        log.warn('Top level element already added! Message: {}'.format(ie.message))
        last_id = db_get_geo_point_id(DB_NAME, id, intid, text, levelid)

    # add top-level points to db (without batching, one by one). if we use batching and adding one by one, we
    # won't miss any top level point that isn't exist in db (we will add missed and won't touch existing)
    add_geo_points(myjson[0]['children'], last_id, batching=False)


def process_geo_points():
    """
    Process (fill in with necessary data/fetch data) geo points db, can be executed multiple times - will
    continue add data to existing database.
    """
    log.debug('process_geo_points(): processing geo points.')
    # get not processed from db and process them
    not_processed = db_get_not_processed_geo_points_ids(DB_NAME)
    while len(not_processed) > 0:

        # process not processed points one by one
        for geo_point in not_processed:
            # get current point info
            geo_point_id = geo_point[0]
            id = geo_point[1]
            # intid = geo_point[2]
            cik_text = geo_point[3]

            # select url
            if u'Санкт-Петербург' in cik_text:
                url = URL_SPB
            elif u'Москва' in cik_text:
                url = URL_MSK
            else:
                url = URL_POINT

            # get json
            myjson = json.load(urllib2.urlopen(url.format(id)), encoding='windows-1251')
            # add all found geo points to db
            add_geo_points(myjson, geo_point_id)
            # mark current geo point as processed
            db_mark_geo_point_as_processed(DB_NAME, geo_point_id)

        # after processing first part - get new not processed points
        not_processed = db_get_not_processed_geo_points_ids(DB_NAME)


# starting point for [geocik] module
log.info('Starting [geocik] module...')
# create db if not exists
if not os.path.exists(DB_NAME):
    log.warn("Database [{}] doesn't exist! Creating...".format(DB_NAME))
    db_create(DB_NAME)  # create target db

# init db first time
init_geo_points()
# process/continue with geo points information
process_geo_points()
