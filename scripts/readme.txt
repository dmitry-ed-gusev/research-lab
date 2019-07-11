-------------------------------------------------------------------------------
This file contains some useful things for python/bash scripting.

Created:  Gusev Dmitrii, 18.03.2018
Modified:

-------------------------------------------------------------------------------


1. Python: create requirements file: [pip freeze > requirements.txt]
-------------------------------------------------------------------------------

2. Python: install according to requirements file: [pip install -r <path_to_reuqirements_file>]
-------------------------------------------------------------------------------

3. Python: requirements format:
-------------------------------------------------------------------------------
    SomePackage            # latest version
    SomePackage==1.0.4     # specific version
    'SomePackage>=1.0.4'   # minimum version

4. Jira properties/info
-------------------------------------------------------------------------------

Issues types: Epic, Story, Task, Sub-task
Statuses: "New"(1), "To Do"(10002), "In Progress"(3), "Blocked"(10300), "In Review"(10001), "Done"(10000), "Closed"(6)


5. Jira useful links
-------------------------------------------------------------------------------
https://issues.merck.com/rest/api/2/project/KDM/statuses

status transitions:
https://community.atlassian.com/t5/Jira-questions/JIRA-How-to-change-issue-status-via-rest/qaq-p/528133
