Running python unit tests with coverage (from Maven):

 export PYTHONPATH=./tests/pymocks
 export COVERAGE_FILE=.pycoverage
 coverage run --source=${project.basedir}/pipeline --branch -m unittest discover -v
 coverage html -d ./py-coverage-report

