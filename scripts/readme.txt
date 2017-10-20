Running python unit tests with coverage (from Maven):

 export PYTHONPATH=./tests/pymocks
 export COVERAGE_FILE=.pycoverage
 coverage run --source=${project.basedir}/pipeline --branch -m unittest discover -v
 coverage html -d ./py-coverage-report

Launching unittests with arguments python -m unittest discover -s C:/projects/research/scripts/pytests -p test_jiralib.py -t C:\projects\research\scripts\pytests in C:\projects\research\scripts\pytests