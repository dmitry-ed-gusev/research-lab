"""
curl -u gusevdm:<password> -X
GET "https://share.merck.com/rest/api/content/281331101?expand=space,history,body.view,metadata.labels,body.storage" | \
python -mjson.tool

Resources:
https://github.com/pycontribs/confluence
https://avleonov.com/2018/01/18/confluence-rest-api-for-reading-and-updating-wiki-pages/
"""

import argparse
import logging
import json
import requests
import pylib.common_constants as myconst
from pylib.pyutilities import setup_logging
from bs4 import BeautifulSoup


def get_page_json(page_id, expand=False):
    if expand:
        suffix = "?expand=" + expand  # body.storage
    else:
        suffix = ""

    url="https://share.merck.com/rest/api/content/" + page_id + suffix
    response = requests.get(url, auth=('gusevdm', 'Vinnypuhh33!'))
    response.encoding = "utf8"
    return json.loads(response.text)


def set_page_json(page_id,json_content):
    headers = {
        'Content-Type': 'application/json',
    }

    response = requests.put("https://confluence.corporation.com/rest/api/content/" + page_id,
                            headers=headers, data=json.dumps(json_content),
                            #auth=(user, password)
                            auth=('', ''))
    return(response.text)


# -------------------------------
setup_logging()
# get module-level logger
log = logging.getLogger(myconst.LOGGER_NAME_GITUTIL)
log.info("Starting GIT Utility...")

json_data = get_page_json("284590357", "body.storage")  # with body content
# json_data = get_page_json("284590357", '')  # without body content
print json_data
print 'Title -> ', json_data['title']

body = json_data['body']['storage']['value']
print 'Body storage value -> ', body

# parse body of page with BeautifulSoup
bs = BeautifulSoup(body, "html.parser")
# find table and all rows in a table
for row in bs.find('table').find_all('tr'):
    cells = row.find_all('td')
    if len(cells) == 3:
        # print len(cells), '->', cells
        print cells[2]

        #print '->', cells[2].find_all('ac')
