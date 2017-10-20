"""
    Utilities/useful methods for web scraper.
"""

from urllib2 import urlopen
from urllib2 import build_opener
from urllib2 import install_opener
from urllib2 import HTTPError
from urllib2 import ProxyHandler
from bs4 import BeautifulSoup
import re

# useful constants
HTML_PARSER = "html.parser"
# global variables
is_http_proxy_set = False
is_https_proxy_set = False


class ScraperException(Exception):
    """Scraper exception, used if something is wrong with/in web scraper."""
    pass


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
