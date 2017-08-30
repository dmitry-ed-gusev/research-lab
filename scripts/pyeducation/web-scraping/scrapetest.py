"""
    https://stackoverflow.com/questions/1450132/proxy-with-urllib2
"""

from urllib2 import urlopen
from urllib2 import build_opener
from urllib2 import install_opener
from urllib2 import HTTPError
from urllib2 import ProxyHandler
from bs4 import BeautifulSoup


def get_html(url, http_proxy="", https_proxy=""):
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

        return urlopen(url)

    except HTTPError as e:
        print "HTTP Error: {}".format(e)
        return None


def get_page_title(url, http_proxy="", https_proxy=""):
    """
    Returns title from web-page (html) from specified url
    :param https_proxy:
    :param http_proxy:
    :param url:
    :return:
    """
    # read html form url
    html = get_html(url, http_proxy, https_proxy)
    if not html:
        print "Url [{}] returned empty page!".format(url)
        return None

    # getting content (title) from returned html
    try:
        # create BeautifulSoup object
        bs_obj = BeautifulSoup(html.read(), "html.parser")
        # get title from h1 tag
        page_title = bs_obj.body.h1
    except AttributeError as e:
        print "HTML Attribute Error: {}".format(e)
        return None
    return page_title

# get and print page title
title = get_page_title("http://www.pythonscraping.com/pages/page1.html", http_proxy='webproxy.merck.com:8080', https_proxy='webproxy.merck.com:8080')

if not title:
    print("Title could not be found")
else:
    print(title)
