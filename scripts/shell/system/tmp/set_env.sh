#!/bin/bash
# ===================================================================
#   Script sets up environment for other scripts from package.
#   Don't directly change other scripts parameters - put them here.
#
#   Created:  Gusev Dmitry, 29.10.2015
#   Modified: Gusev Dmitry, 10.03.2016
# ===================================================================

# ============================== COMMON SETTINGS ==============================
# - Get current date/time (for backup/other purposes)
CURRENT_DATE=$(date +"%Y-%m-%d")
CURRENT_TIME=$(date +"%H%M%S")
SOFTWARE_HOME=software
SCRIPT_HOME=$(pwd)

# ============================== SETTINGS: MYSQL ==============================
MYSQL_USER=root
MYSQL_PASS=root

# ============================== SETTINGS: JAVA/JENKINS/ANT/MAVEN/SONAR ==============================
JAVA_ALT_REPO=ppa:webupd8team/java
JAVA_VERSION=7
# -- Jenkins version 1.596LTS is the last version with JDK6 support (for master/slaves).
JENKINS_DEB=jenkins_1.596.3_all.deb
JENKINS_KEY_SERVER=https://jenkins-ci.org/debian/jenkins-ci.org.key
JENKINS_SOURCES_STRING="http://pkg.jenkins-ci.org/debian binary/"
JENKINS_APACHE_PORT=5000
JENKINS_INTERNAL_PORT=8181
JENKINS_NAME=jenkins
JENKINS_HOME=/var/lib/$JENKINS_NAME
# -- Ant/Maven tools
ANT_NAME=apache-ant-1.9.6
MAVEN_NAME=apache-maven-3.3.3
ANT_ARCHIVE=$ANT_NAME-bin.tar.gz
MAVEN_ARCHIVE=$MAVEN_NAME-bin.tar.gz
# -- Sonar server
SONAR_NAME=sonarqube-4.5.6
SONAR_JDBC_URL="jdbc:mysql://localhost:3306/sonar?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&useConfigs=maxPerformance"
SONAR_PORT=9000
SONAR_CONTEXT=/sonar
# -- platform for Sonar (see Sonar bin directory)
SONAR_PLATFORM=linux-x86-64

# ============================== SETTINGS: VCS (SVN/GIT/HG) ==============================
SVN_STARTUP_SCRIPT=svnserve
SVN_REPOS_HOME=/var/svn-repos
SVN_REPOS_ADMIN=admin
SVN_REPOS_ADMIN_PASS=svnadmin
SVN_INITIAL_MSG="Repository structure creation (init)."
GITLAB_PACKAGE_NAME=gitlab-ce_8.5.4-ce.0_amd64.deb
GITLAB_HOST=http://localhost
GITLAB_PORT=7000

# ============================== SETTINGS: RUBY/RVM/RAILS/REDMINE ==============================
RVM_KEY_SERVER=hkp://keys.gnupg.net
RVM_SERVER=https://get.rvm.io
RVM_KEY=409B6B1796C275462A1703113804BB82D39DC0E3
REDMINE_DB_NAME=redmine
REDMINE_DB_USER=redmine
REDMINE_DB_PASS=redmine
REDMINE_NAME=redmine
REDMINE_VERSION=3.1.1
REDMINE_INSTALL_PATH=/var/www/vhosts
REDMINE_HOME=$REDMINE_INSTALL_PATH/$REDMINE_NAME
REDMINE_PORT=3000
REDMINE_LANG=ru

# ============================== SETTINGS: APACHE ==============================
APACHE_SETTINGS=/etc/apache2

# ============================== SETTINGS: BACKUP/RESTORE ==============================
BACKUP_NAME=dept306_"$CURRENT_DATE"_"$CURRENT_TIME"
BACKUP_DIR=/home/$USER/dept306-backup
REDMINE_DB_BACKUP=redmine.sql