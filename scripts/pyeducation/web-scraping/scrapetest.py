"""
    https://stackoverflow.com/questions/1450132/proxy-with-urllib2
"""

from urllib2 import urlopen
from urllib2 import build_opener
from urllib2 import install_opener
from urllib2 import HTTPError
from urllib2 import ProxyHandler
from bs4 import BeautifulSoup
import re

# some useful constants
HTML_PARSER = "html.parser"
PROXY_SERVER = "webproxy.merck.com:8080"
REGEX_EMAIL = "[A-Za-z0-9\._+]+@[A-Za-z]+\.(com|org|edu|net|ru)"


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
        # print "Page title -> {}". format(bs_obj.body.h1)

        # print all persons names
        # names_list = bs_obj.findAll("span", {"class" : "green"})
        # names_list = bs_obj.findAll(class_="green")
        # for name in names_list:
        #    print name.get_text()

        # print siblings
        # for sibling in bs_obj.find("table", {"id" : "giftList"}).tr.next_siblings:
        #    print sibling

        # print "===> {}".format(bs_obj.find("img", {"src": "../img/gifts/img1.jpg"}).parent.previous_sibling.get_text())

        # print all images addresses
        images = bs_obj.findAll("img", {"src": re.compile("\.\.\/img\/gifts/img.*\.jpg")})
        for image in images:
            print image["src"]

        tags = bs_obj.findAll(lambda tag: len(tag.attrs) == 2)
        for tag in tags:
            print "tag -> {}".format(tag)

    except AttributeError as e:
        print "HTML Attribute Error: {}".format(e)

# ===============================================

page1 = "http://www.pythonscraping.com/pages/page1.html"
page2 = "http://www.pythonscraping.com/pages/warandpeace.html"
page3 = "http://www.pythonscraping.com/pages/page3.html"

# get and print page title
# get_page_data(page1, http_proxy=PROXY_SERVER, https_proxy=PROXY_SERVER)
#get_page_data(page1)
#print

#get_page_data(page2)
#print

get_page_data(page3)
print
