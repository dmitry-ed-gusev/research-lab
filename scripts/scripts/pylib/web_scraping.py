#!/usr/bin/python
#  -*- coding: utf-8 -*-

"""
    Common utilities/function for web scraping in python. Can be useful in different cases.
    Created: Gusev Dmitrii, 22.04.2018
    Modified: Gusev Dmitrii, 23.04.2018
"""

from urllib2 import build_opener
from urllib2 import install_opener
from urllib2 import HTTPError
from urllib2 import ProxyHandler
from bs4 import BeautifulSoup
import re
import random
import datetime
import logging.config
from pyutilities.utils import setup_logging
from urllib2 import urlopen

# configure logger on module level. it isn't a good practice, but it's convenient.
# don't forget to set disable_existing_loggers=False, otherwise logger won't get its config!
log = logging.getLogger(__name__)
# to avoid errors like 'no handlers' for libraries it's necessary to add NullHandler.
log.addHandler(logging.NullHandler())

# useful constants
HTML_PARSER = "html.parser"
# global variables
is_http_proxy_set = False
is_https_proxy_set = False

# init random generator with current time
random.seed(datetime.datetime.now())
# some useful constants
PROXY_SERVER = "webproxy.merck.com:8080"


class ScraperException(Exception):
    """Scraper exception, used if something is wrong with/in web scraper."""
    pass


def get_url(url):
    """ Read URL and return as text. """
    log.debug('get_url() is working. URL [{}].'.format(url))
    html = urlopen(url)
    return html.read()


def set_proxy(http_proxy="", https_proxy=""):
    """
    Set up HTTP/HTTPS proxy for urllib library.
    :param http_proxy:
    :param https_proxy:
    :return:
    """
    print "scrapelib.set_proxy() is working."
    # preparation
    global is_http_proxy_set
    global is_https_proxy_set
    proxies = {}
    # HTTP proxy
    if http_proxy and http_proxy.strip() and not is_http_proxy_set:
        print "Adding HTTP proxy [{}] to dictionary.".format(http_proxy)
        proxies['http'] = http_proxy
        is_http_proxy_set = True
    else:
        print "Empty HTTP proxy [{}] or proxy already set [{}].".format(http_proxy, is_http_proxy_set)
    # HTTPS proxy
    if https_proxy and https_proxy.strip() and not is_https_proxy_set:
        print "Adding HTTPS proxy [{}] to dictionary.".format(https_proxy)
        proxies['https'] = https_proxy
        is_https_proxy_set = True
    else:
        print "Empty HTTPS proxy [{}] or proxy already set [{}].".format(https_proxy, is_https_proxy_set)
    # set up proxy handler (if there are and not set)
    if proxies:
        print "Setting proxy handler. Proxies [{}].".format(proxies)
        proxy = ProxyHandler(proxies)
        opener = build_opener(proxy)
        install_opener(opener)
    else:
        print "Empty proxies list or proxy is already set."


def get_bs_object(url, http_proxy="", https_proxy=""):
    """
    Returns html (BeautifulSoup object) by specified url.
    :param https_proxy:
    :param http_proxy:
    :param url: target url for getting the whole html
    :return:
    """
    if not url:   # fast check and return
        return None
    try:
        if http_proxy or http_proxy.strip() or https_proxy or https_proxy.strip():
            # set up proxy if not already set
            set_proxy(http_proxy, https_proxy)

        # open url, parse it and create/return beautiful soup object
        return BeautifulSoup(urlopen(url).read(), HTML_PARSER)
    except HTTPError as e:
        print "HTTP Error: {}".format(e)
        return None


def get_internal_links(bs_obj, include_url):
    """
    Retrieves a list of all Internal links found on a page.
    :param bs_obj:
    :param include_url:
    :return:
    """
    internal_links = []
    # Finds all links that begin with a "/"
    for link in bs_obj.findAll("a", href=re.compile("^(/|.*" + include_url + ")")):
        if link.attrs['href'] is not None:
            if link.attrs['href'] not in internal_links:
                internal_links.append(link.attrs['href'])
    return internal_links


def getLinks1(articleUrl, http_proxy="", https_proxy=""):
    bs_obj = get_bs_object("http://en.wikipedia.org" + articleUrl, http_proxy, https_proxy)
    return bs_obj.find("div", {"id": "bodyContent"}).findAll("a", href=re.compile("^(/wiki/)((?!:).)*$"))


def getLinks2(pageUrl, http_proxy="", https_proxy=""):
    global pages
    bs_obj = get_bs_object("http://en.wikipedia.org" + pageUrl, http_proxy, https_proxy)
    try:
        print(bs_obj.h1.get_text())
        print(bs_obj.find(id ="mw-content-text").findAll("p")[0])
        print(bs_obj.find(id="ca-edit").find("span").find("a").attrs['href'])
    except AttributeError:
        print("This page is missing something! No worries though!")

    for link in bs_obj.findAll("a", href=re.compile("^(/wiki/)")):
        if 'href' in link.attrs:
            if link.attrs['href'] not in pages:
                # We have encountered a new page
                newPage = link.attrs['href']
                print("----------------\n"+newPage)
                pages.add(newPage)
                getLinks2(newPage)


def scrap_1():
    links = getLinks1("/wiki/Kevin_Bacon", PROXY_SERVER, PROXY_SERVER)
    while len(links) > 0:
        newArticle = links[random.randint(0, len(links)-1)].attrs["href"]
        print(newArticle)
        links = getLinks1(newArticle)


def scrap_2():
    getLinks2("", PROXY_SERVER, PROXY_SERVER)


def getTitle(url):
    """ get title with error handling """
    try:
        html = urlopen(url)
    except HTTPError as e:
        print(e)
        return None
    try:
        bsObj = BeautifulSoup(html, "html.parser")
        title = bsObj.body.h1
    except AttributeError as e:
        return None
    return title


if __name__ == '__main__':
    # print "web_scraping: Don't try to execute library as a standalone app!"
    setup_logging()
    log.setLevel(logging.DEBUG)
    # print get_url("http://www.pythonscraping.com/exercises/exercise1.html")
    print 'h1 value -> ', get_bs_object("http://www.pythonscraping.com/exercises/exercise1.html").h1

    # scrap_1()
    # pages = set()
    # scrap_2()
