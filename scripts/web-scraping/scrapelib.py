"""

"""

from urllib2 import urlopen
from urllib2 import build_opener
from urllib2 import install_opener
from urllib2 import HTTPError
from urllib2 import ProxyHandler
from bs4 import BeautifulSoup

# useful constants
HTML_PARSER = "html.parser"

is_http_proxy_set = False
is_https_proxy_set = False


def set_proxy_handler(protocol, proxy):
    if not protocol or not protocol.strip():
        raise
    proxy = ProxyHandler({})


# todo: implement set proxy only once
def set_proxy(http_proxy="", https_proxy=""):
    print "!!!"


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


#Retrieves a list of all Internal links found on a page
def getInternalLinks(bsObj, includeUrl):
    internalLinks = []
    #Finds all links that begin with a "/"
    for link in bsObj.findAll("a", href=re.compile("^(/|.*"+includeUrl+")")):
        if link.attrs['href'] is not None:
            if link.attrs['href'] not in internalLinks:
                internalLinks.append(link.attrs['href'])
    return internalLinks