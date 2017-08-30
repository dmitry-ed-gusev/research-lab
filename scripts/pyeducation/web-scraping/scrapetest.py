from urllib2 import urlopen
from urllib2 import HTTPError
from bs4 import BeautifulSoup

# https://stackoverflow.com/questions/1450132/proxy-with-urllib2
def get_html(url):
    """
    Returns html by specified url
    :param url: target url for getting the whole html
    :return:
    """
    if not url:   # fast check and return
        return None
    try:
        return urlopen(url)
    except HTTPError as e:
        print "HTTP Error: {}".format(e)
        return None


def get_page_title(url):
    """
    Returns title from web-page (html) from specified url
    :param url:
    :return:
    """
    # read html form url
    html = get_html(url)
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
title = get_page_title("http://www.pythonscraping.com/pages/page1.html")

if not title:
    print("Title could not be found")
else:
    print(title)
