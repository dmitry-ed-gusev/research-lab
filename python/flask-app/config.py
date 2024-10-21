# cspell:ignore loguru
# -*- coding: utf-8 -*-

import os

from loguru import logger


class Config:
    """Application Configuration."""

    SECRET_KEY = os.environ.get('SECRET_KEY') or 'you-will-never-guess'
    NAME = os.environ.get('NAME') or 'UNDEFINED'

    def __init__(self) -> None:
        logger.debug("Created instance of Config class (application configuration).")
