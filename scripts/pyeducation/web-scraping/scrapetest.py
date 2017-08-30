"""
    https://stackoverflow.com/questions/1450132/proxy-with-urllib2
"""

from urllib2 import urlopen
from urllib2 import build_opener
from urllib2 import install_opener
from urllib2 import HTTPError
from urllib2 import ProxyHandler
from bs4 import BeautifulSoup

# some useful constants
HTML_PARSER = "html.parser"
PROXY_SERVER = "webproxy.merck.com:8080"


def get_bs_object(url, http_proxy="", https_proxy=""):
    """
    Returns html by specified url
    :param https_proxy:
    :param http_proxy:
    :param url: target url for getting the whole html
    :return:
    """
    if not url:   # fast check and return
        return None
    try:
        # if specified proxy - set it
        if http_proxy or https_proxy:
            print "Using proxies: http [{}], https [{}].".format(http_proxy, https_proxy)
            # set up proxy handler
            proxy = ProxyHandler({
                'http': http_proxy,
                'https': https_proxy
            })
            # install proxy handler for urllib2
            opener = build_opener(proxy)
            install_opener(opener)

        return BeautifulSoup(urlopen(url).read(), HTML_PARSER)

    except HTTPError as e:
        print "HTTP Error: {}".format(e)
        return None


def get_page_data(url, http_proxy="", https_proxy=""):
    """
    Returns title from web-page (html) from specified url
    :param https_proxy:
    :param http_proxy:
    :param url:
    :return:
    """
    try:
        # read url and get BeautifulSoup object
        bs_obj = get_bs_object(url, http_proxy, https_proxy)
        if not bs_obj:
            print "Url [{}] returned empty content!".format(url)
            return

        # print page title
        print "Page title -> {}". format(bs_obj.body.h1)

        # print all persons names
        names_list = bs_obj.findAll("span", {"class" : "green"})
        # names_list = bs_obj.findAll(class_="green")
        for name in names_list:
            print name.get_text()

    except AttributeError as e:
        print "HTML Attribute Error: {}".format(e)

# ===============================================

page1 = "http://www.pythonscraping.com/pages/page1.html"
page2 = "http://www.pythonscraping.com/pages/warandpeace.html"
page3 = "http://www.pythonscraping.com/pages/page3.html"

# get and print page title
# get_page_data(page1, http_proxy=PROXY_SERVER, https_proxy=PROXY_SERVER)
get_page_data(page1)
