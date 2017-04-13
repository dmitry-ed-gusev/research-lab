#!/bin/bash
# ===================================================================
#   Script sets up environment for other scripts from package.
#   Don't directly change other scripts parameters - put them here.
#
#   Created:  Gusev Dmitry, 26.11.2016
#   Modified: Gusev Dmitry, 12.04.2017
# ===================================================================

# ============================== COMMON SETTINGS ==============================
# - Usage file - utility description
USAGE_FILE='usage.txt'
# - Proxy server settings
# todo: replace hardcoded settings with cmdline argument
# todo: move logic of set up system proxy to another place
PROXY=http://webproxy.merck.com:8080
# export proxy variables for current session
# todo: switch setting up a proxy on/off by cmd line argument
export {HTTP,HTTPS}_PROXY=${PROXY}
export {http,https}_proxy=${PROXY}
# - Set up system proxy for current user (put it in ~/.profile file)
grep -Fq "export HTTP_PROXY=" ~/.profile
if [ $? -ne 0 ]; then
    echo "echo '' >> ~/.profile" | sudo sh
    echo "echo 'export HTTP_PROXY=\"${PROXY}\"' >> ~/.profile" | sudo sh
fi
grep -Fq "export HTTPS_PROXY=" ~/.profile
if [ $? -ne 0 ]; then
    echo "echo '' >> ~/.profile" | sudo sh
    echo "echo 'export HTTPS_PROXY=\"${PROXY}\"' >> ~/.profile" | sudo sh
fi
grep -Fq "export http_proxy=" ~/.profile
if [ $? -ne 0 ]; then
    echo "echo '' >> ~/.profile" | sudo sh
    echo "echo 'export http_proxy=\"${PROXY}\"' >> ~/.profile" | sudo sh
fi
grep -Fq "export https_proxy=" ~/.profile
if [ $? -ne 0 ]; then
    echo "echo '' >> ~/.profile" | sudo sh
    echo "echo 'export https_proxy=\"${PROXY}\"' >> ~/.profile" | sudo sh
fi

# - Debug mode for script. If DEBUG_MODE=true script will wait for a key press after every logic part
# - of installation. Set DEBUG value to any other, than "true" - script will slip any question.
DEBUG_MODE=false
# - get current date/time (for backup/other purposes)
CURRENT_DATE=$(date +"%Y-%m-%d")
CURRENT_TIME=$(date +"%H%M%S")
#SOFTWARE_HOME=software
SCRIPT_HOME=$(pwd)
# - reboot after update, yes by default
REBOOT_AFTER_UPDATE=YES

# ============================== SETTINGS: Java/Ant/Maven/Sonar/Jenkins ==============================
# -- Oracle Java JDK settings
JAVA_ALT_REPO=ppa:webupd8team/java
JAVA_VERSION=8
# -- Apache Ant settings
ANT_VERSION="1.9.9"
ANT_NAME="apache-ant-$ANT_VERSION"
ANT_ARCHIVE="$ANT_NAME-bin.tar.gz"
ANT_BINARY_URL="http://apache-mirror.rbc.ru/pub/apache/ant/binaries/$ANT_ARCHIVE"
# -- Apache Maven settings
MAVEN_VERSION="3.3.9"
MAVEN_NAME="apache-maven-$MAVEN_VERSION"
MAVEN_ARCHIVE="$MAVEN_NAME-bin.tar.gz"
MAVEN_BINARY_URL="http://apache-mirror.rbc.ru/pub/apache/maven/maven-3/$MAVEN_VERSION/binaries/$MAVEN_ARCHIVE"
# -- Jenkins version 1.596LTS is the last version with JDK6 support (for master/slaves).
JENKINS_DEB=jenkins_1.596.3_all.deb
JENKINS_KEY_SERVER=https://jenkins-ci.org/debian/jenkins-ci.org.key
JENKINS_SOURCES_STRING="http://pkg.jenkins-ci.org/debian binary/"
JENKINS_APACHE_PORT=5000
JENKINS_INTERNAL_PORT=8181
JENKINS_NAME=jenkins
JENKINS_HOME=/var/lib/${JENKINS_NAME}
# -- Sonar server
SONAR_NAME=sonarqube-4.5.6
SONAR_JDBC_URL="jdbc:mysql://localhost:3306/sonar?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&useConfigs=maxPerformance"
SONAR_PORT=9000
SONAR_CONTEXT=/sonar
# -- platform for Sonar (see Sonar bin directory)
SONAR_PLATFORM=linux-x86-64

# ============================== Settings: Big Data tools ==============================
# -- Apache Hadoop settings
HADOOP_VERSION=2.7.3
HADOOP_NAME="hadoop-$HADOOP_VERSION"
HADOOP_ARCHIVE="$HADOOP_NAME.tar.gz"
HADOOP_BINARY_URL="https://archive.apache.org/dist/hadoop/common/$HADOOP_NAME/$HADOOP_ARCHIVE"

# ============================== SETTINGS: MYSQL ==============================
MYSQL_ROOT_PASS="rootroot"