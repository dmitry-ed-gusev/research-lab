
import urllib
from BeautifulSoup import *

# -- sample data
#COUNT    = 4
#POSITION = 3
#url = "https://pr4e.dr-chuck.com/tsugi/mod/python-data/data/known_by_Fikret.html"

# -- real data
COUNT    = 7
POSITION = 18
url = "https://pr4e.dr-chuck.com/tsugi/mod/python-data/data/known_by_Jonny.html"

# -- read all html
html = urllib.urlopen(url).read()
# -- parse html with library BeatifulSoup
soup = BeautifulSoup(html)

# Retrieve all of the anchor tags
tags = soup('a')
lastName = None
print "Start iterating cycle..."
for i in range(COUNT):
    lastName = tags[POSITION - 1].contents[0].strip()
    print lastName, " "
    tags = BeautifulSoup(urllib.urlopen(tags[POSITION - 1].get('href', None)).read())('a')
# -- print the last found name in sequence
print "found last name -> ", lastName
