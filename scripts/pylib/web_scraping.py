#!/usr/bin/python
#  -*- coding: utf-8 -*-

"""
    Common utilities/function for web scraping in python. Can be useful in different cases.
    Created: Gusev Dmitrii, 22.04.2018
    Modified:
"""

import logging
import logging.config
from urllib2 import urlopen

# configure logger on module level. it isn't a good practice, but it's convenient.
# don't forget to set disable_existing_loggers=False, otherwise logger won't get its config!
log = logging.getLogger(__name__)
# to avoid errors like 'no handlers' for libraries it's necessary to add NullHandler.
log.addHandler(logging.NullHandler())


def get_url(url):
    log.debug('get_url() is working.')
    html = urlopen("http://www.pythonscraping.com/exercises/exercise1.html")
    return html.read()


if __name__ == '__main__':
    print "web_scraping: Don't try to execute library as a standalone app!"
