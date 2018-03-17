"""
curl -u gusevdm:<password> -X
GET "https://share.merck.com/rest/api/content/281331101?expand=space,history,body.view,metadata.labels,body.storage" | \
python -mjson.tool

Resources:
https://github.com/pycontribs/confluence
"""

import argparse
import logging
import pylib.common_constants as myconst
from pylib.pyutilities import setup_logging

setup_logging()
# get module-level logger
log = logging.getLogger(myconst.LOGGER_NAME_GITUTIL)
log.info("Starting GIT Utility...")
