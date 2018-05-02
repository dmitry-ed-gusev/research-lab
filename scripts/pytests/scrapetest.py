"""
 web scraper tests/examples
"""

from scrapelib import set_proxy
from scrapelib import get_bs_object
import re

# some useful constants
PROXY_SERVER = "webproxy.merck.com:8080"
REGEX_EMAIL = "[A-Za-z0-9\._+]+@[A-Za-z]+\.(com|org|edu|net|ru)"

# pages for examples
page1 = "http://www.pythonscraping.com/pages/page1.html"
page2 = "http://www.pythonscraping.com/pages/warandpeace.html"
page3 = "http://www.pythonscraping.com/pages/page3.html"

# setup proxy
set_proxy(PROXY_SERVER, PROXY_SERVER)

try:
    # ===== example #1 ======================
    bs_obj = get_bs_object(page1, PROXY_SERVER, PROXY_SERVER)
    if bs_obj:
        # print page title
        print "Page title -> {}". format(bs_obj.body.h1)
    else:
        print "Url [{}] returned empty content!".format(page1)

    # ===== example #2 ======================
    bs_obj = get_bs_object(page2)
    if bs_obj:
        # print all persons names
        names_list = bs_obj.findAll("span", {"class": "green"})
        names_list = bs_obj.findAll(class_="green")
        for name in names_list:
            print name.get_text()

        # print siblings
        # for sibling in bs_obj.find("table", {"id": "giftList"}).tr.next_siblings:
        #    print sibling
        # print "===> {}".format(bs_obj.find("img", {"src": "../img/gifts/img1.jpg"}).parent.previous_sibling.get_text())
    else:
        print "Url [{}] returned empty content!".format(page2)

    # ===== example #3 ======================
    bs_obj = get_bs_object(page3)
    if bs_obj:
        # print all images addresses
        images = bs_obj.findAll("img", {"src": re.compile("\.\.\/img\/gifts/img.*\.jpg")})
        for image in images:
            print image["src"]

        tags = bs_obj.findAll(lambda tag: len(tag.attrs) == 2)
        for tag in tags:
            print "tag -> {}".format(tag)
    else:
        print "Url [{}] returned empty content!".format(page3)

except Exception as e:
    print "Exception: {}".format(e)
