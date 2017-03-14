import urllib
import json

# -- sample data for test
# url = "http://python-data.dr-chuck.net/comments_42.json"
# -- real data for aasignment
url = "http://python-data.dr-chuck.net/comments_191797.json"

# -- open url, reads all data, parse that data as JSON, get value for 'comments' from dictionary
info = json.loads(urllib.urlopen(url).read())['comments']
# --  iterate over list os pairs, get values, convert them to ints and sums resulting list
total = sum([int(item['count']) for item in info])
# -- result output
print total
