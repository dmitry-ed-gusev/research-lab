Running python unit tests with coverage (from Maven):

 export PYTHONPATH=./tests/pymocks
 export COVERAGE_FILE=.pycoverage
 coverage run --source=${project.basedir}/pipeline --branch -m unittest discover -v
 coverage html -d ./py-coverage-report

Launching unittests with arguments python -m unittest discover -s C:/projects/research/scripts/pytests -p test_jiralib.py -t C:\projects\research\scripts\pytests in C:\projects\research\scripts\pytests

Creating and installing ssh keys
1. execute ssh-keygen (see params) on local machine (it generates both public/private keys)
2. execute ss-copy-id (see params) on local machine to copy public key to remote server
3. check :)
4. nice resource: https://wiki.archlinux.org/index.php/SSH_keys