-------------------------------------------------------------------------------
This file contains some useful things for python/bash scripting.

Created:  Gusev Dmitrii, 18.03.2018
Modified:

-------------------------------------------------------------------------------

1. Python: create requirements file: [pip freeze > requirements.txt]

2. Python: install according to requirements file: [pip install -r <path_to_reuqirements_file>]

3. Python: requirements format:
    SomePackage            # latest version
    SomePackage==1.0.4     # specific version
    'SomePackage>=1.0.4'   # minimum version