pip install nose2 cov-core coverage pyyaml mock --proxy webproxy.merck.com:8080
rem set PYTHONPATH=./target/dependency;./pymocks
python -m nose2 -v -s tests --plugin nose2.plugins.junitxml -X --with-coverage --coverage pipeline --coverage-report html