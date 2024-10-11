#!/usr/bin/env bash

###############################################################################
#
#       Script builds project and run Sonar. Script is doing following:
#         * builds project with maven (using profile env-prod-all)
#         * using docker-composer start up docker sonar environment
#         * execute sonar analysis with maven (mvn sonar:sonar)
#
#       Created:  Dmitrii Gusev, 19.05.2019
#       Modified: Dmitrii Gusev, 18.06.2019
#
###############################################################################

# using docker composer start up sonar environment
docker-compose up -d

# wait 60 seconds for mysql/sonar to start
sleep 60

# execute sonar check for the project
mvn clean install sonar:sonar -Penv-dev-all,sonar
