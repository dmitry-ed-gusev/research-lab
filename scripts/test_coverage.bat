rem pip install nose2 cov-core coverage pyyaml mock --proxy webproxy.merck.com:8080
rem pip install nose2 cov-core coverage pyyaml mock
rem set PYTHONPATH=./target/dependency;./pymocks
rem python -m nose2 -v --plugin nose2.plugins.junitxml -X --with-coverage --coverage-report html xml