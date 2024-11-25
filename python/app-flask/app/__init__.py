# -*- coding: utf-8 -*-

from flask import Flask
from loguru import logger

from config import Config

# create a variable app as instance of class Flask in a module app
app = Flask(__name__)
logger.debug(f"Created a Flask() instance app = {app}")

# loading application config from Config object
app.config.from_object(Config)
logger.debug("Loaded configuration of the application.")

# -- other code here --

# import routes from module app (added at the bottom/last - to avoid circles)
from app import routes
