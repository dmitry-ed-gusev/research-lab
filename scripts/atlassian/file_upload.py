
# See docs here -> https://docs.atlassian.com/atlassian-confluence/REST/5.10.8/

# ===============
# upload attachment
# ===============
import requests
import json

# ~gusevdmi -> Test page -> id = 302318367 (content id)
# base url: https://iapi.merck.com/confluence/v1/

# [CONFLUENCE-BASE-URL], i.e.: http://localhost:8090
# [CONTENT-ID], get content ID by running '[CONFLUENCE-BASE-URL]/rest/api/content'
# url = '[CONFLUENCE-BASE-URL]/rest/api/content/[CONTENT-ID]/child/attachment'

#url = 'https://iapi.merck.com/confluence/v1/rest/api/content/302318367/child/attachment'

att_id = "att302334654"

url = "https://share.merck.com/rest/api/content/302318367/child/attachment"
headers = {"X-Atlassian-Token": "nocheck"}
data = {"comment": "New content..."}


# please, uncomment to attach inline content
# files = {'file': ('report.xml', '&lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;note&gt;&lt;to&gt;RECIPIENT&lt;/to&gt;&lt;from&gt;SENDER&lt;/from&gt;&lt;heading&gt;ATTACHMENT&lt;/heading&gt;&lt;body&gt;CONTENT&lt;/body&gt;&lt;/note&gt;')}
# please uncomment to attach external file
# files = {'file': open('text.txt', 'rb')}

files = {'file': open('requirements.txt', 'rb')}

# upload file to page
# [USERNAME], i.e.: admin
# [PASSWORD], i.e.: admin
user = 'gusevdmi'
password = 'Vinnypuhh99'

# create attach
# r = requests.post(url + "/" + att_id, data=data, auth=(user, password), files=files, headers=headers)
# update attach (info)
# r = requests.put(url + "/" + att_id, data=data, auth=(user, password), files=files, headers=headers)
# update attach data (file) itself
r = requests.post(url + "/" + att_id + "/data", data=data, auth=(user, password), files=files, headers=headers)

print(r.status_code)
print(r.text)
