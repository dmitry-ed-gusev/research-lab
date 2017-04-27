import urllib
from BeautifulSoup import *

# -- sample data
#url = "http://python-data.dr-chuck.net/comments_42.html"
# -- real data
url = "http://python-data.dr-chuck.net/comments_191796.html"
#url = raw_input('Enter - ')

# -- read all html
html = urllib.urlopen(url).read()
# -- parse html via library BeatifulSoup
soup = BeautifulSoup(html)
# -- retrieve all of the <span> tags
tags = soup('span')
# -- iterate over tags list and process them
sum = 0
for tag in tags:
    # Look at the parts of a tag
    #print 'TAG:', tag
    #print 'URL:',tag.get('href', None)
    #print 'Contents:', tag.contents[0]
    sum += int(tag.contents[0])
    #print 'Attrs:', tag.attrs
print "sum = ", sum
