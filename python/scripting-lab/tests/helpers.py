#!/usr/bin/env python
# coding=utf-8

"""Some helpers methods for unit tests.

    Created: Gusev Dmitrii, 22.12.2017
    Modified:
"""

import yaml
import logging.config


def init_logger():
    with open('pytests/configs/test_logging.yml', 'rt') as f:
        logging.config.dictConfig(yaml.safe_load(f.read()))
