"""
 Web scraper MVP project.
"""

from scrapelib import get_bs_object
import re


# get all links from wiki page
wiki_address = "http://en.wikipedia.org/wiki/Kevin_Bacon"
bs = get_bs_object(wiki_address)

for link in bs.find("div", {"id" : "bodyContent"}).findAll("a", href=re.compile("^(/wiki/)((?!:).)*$")):
    if "href" in link.attrs:
        print link.attrs["href"]
