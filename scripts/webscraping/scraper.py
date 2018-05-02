"""
 Web scraper MVP project.
"""

from scrapelib import set_proxy
from scrapelib import get_bs_object
import datetime
import random
import re

from urllib2 import urlopen
from urllib2 import build_opener
from urllib2 import install_opener
from urllib2 import HTTPError
from urllib2 import ProxyHandler
from urllib2 import getproxies


# init random generator with current time
random.seed(datetime.datetime.now())

# some useful constants
PROXY_SERVER = "webproxy.merck.com:8080"


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

# ===========================================
#scrap_1()

pages = set()
scrap_2()


#set_proxy(PROXY_SERVER, PROXY_SERVER)
#set_proxy(PROXY_SERVER, PROXY_SERVER)
#print "-> ", urlopen("http://ya.ru").read()
