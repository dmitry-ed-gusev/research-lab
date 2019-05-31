#!/usr/bin/env python
# coding=utf-8

"""

    Helper module for unit tests.

    Created:  Dmitrii Gusev, 20.05.2019
    Modified: DMitrii Gusev, 25.05.2019

"""

import yaml
import logging
import logging.config


def get_test_logger(name: str):
    """ Initializing logger for testing. """
    log = logging.getLogger(name)
    with open('scripts/pytests/configs/logging.yml', 'rt') as f:
        logging.config.dictConfig(yaml.safe_load(f.read()))
    return log
