import urllib
import xml.etree.ElementTree as ET

# sample URL
#url = "http://python-data.dr-chuck.net/comments_42.xml"
# real data
url = "http://python-data.dr-chuck.net/comments_191793.xml"

print 'Retrieving ->', url
uh = urllib.urlopen(url)
data = uh.read()
print 'Retrieved', len(data), 'characters'
# parse XML, get all <count> tags, generate list of ints from their contents, sum resulting list and print it
print 'total sum ->', sum([int(tag.text) for tag in ET.fromstring(data).findall('.//count')])