#!/usr/bin/env bash

###############################################################################
#
#   Development Lab environment script. The current script sets up the
#   following services:
#       - docker network for all services
#       - GitLab service container
#       - MySql and Sonar containers in the mentioned network
#       - ???
#
#   Created:  Dmitrii Gusev, 30.03.2019
#   Modified: Dmitrii Gusev, 21.01.2024
#
###############################################################################

# -- Set bash strict mode
set -euo pipefail
IFS=$'\n\t'

# -- General environment variables setup

# --- docker setup
export DOCKER_NETWORK=dev_lab_network

# --- gitlab setup

# --- sonar setup
# SONAR_IMAGE_NAME=sonarqube:latest
# SONAR_CONTAINER_NAME=sonar

# --- mysql for sonar setup
# MYSQL_IMAGE_NAME=mysql:5.7
# MYSQL_CONTAINER_NAME=mysql.5.7
# MYSQL_ROOT_PASS=root
# MYSQL_DB=sonar
# MYSQL_USER=sonar
# MYSQL_PASS=sonar

# Create docker network for mysql and sonar (remove if exists and create)
printf "Creating docker network: %s\n" ${DOCKER_NETWORK}
docker network rm ${DOCKER_NETWORK} || printf "Network %s not found!\n" ${DOCKER_NETWORK}
docker network create ${DOCKER_NETWORK}

docker network list | grep \"${DOCKER_NETWORK}\"
 #[[ -n "docker network list | grep \"${DOCKER_NETWORK}\"" ]] && echo 
exit 1

# Simple first run (init) of new/clean Mysql container instance.
# Root password will be set to: root. DB to be created: sonar, user to be crated: sonar/sonar.
# Auto remove container on exit (--rm option) - switched off.
# It's possible to expose Mysql port - add [-p 3306:3306] option.
# Run detached: -d option.
docker run -d --name ${MYSQL_CONTAINER_NAME} --net ${DOCKER_NETWORK} \
    -e MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASS} \
    -e MYSQL_DATABASE=${MYSQL_DB} \
    -e MYSQL_USER=${MYSQL_USER} \
    -e MYSQL_PASSWORD=${MYSQL_PASS} \
    ${MYSQL_IMAGE_NAME}

# sleep for X seconds - wait till Mysql full start
sleep 15

# Simple first run of new/clean SonarQube container instance.
# Auto remove container on exit (--rm option) - switched off.
# Run detached: -d option.
docker run -d --name ${SONAR_CONTAINER_NAME} -p 9000:9000 --net ${DOCKER_NETWORK} \
    -e sonar.jdbc.username=${MYSQL_USER} \
    -e sonar.jdbc.password=${MYSQL_PASS} \
    -e "sonar.jdbc.url=jdbc:mysql://${MYSQL_CONTAINER_NAME}:3306/${MYSQL_DB}?useUnicode=true&characterEncoding=utf8
    &rewriteBatchedStatements=true&useConfigs=maxPerformance" \
    ${SONAR_IMAGE_NAME}
