#!/bin/bash
#
# ===================================================================
#   This script installs Redmine bug/task tracker. Script should not
#   be started as user 'root' (sudo ./<script_name>)!
#  
#   Created:  Gusev Dmitry, 27.10.2015
#   Modified: Gusev Dmitry, 02.11.2015
# ===================================================================

# -- Call other script for set environment for current process
source set_env.sh

# -- Install Redmine (current user and www-data should be in "rvm" group)
# - install some necessary packages (order of packages is important!)
sudo apt-get -qy install libmysqlclient-dev apache2-threaded-dev libmagickwand-dev libcurl4-openssl-dev
# - set up MySql database for Redmine
mysql -u root -p$MYSQL_PASS -e "CREATE DATABASE $REDMINE_DB_NAME CHARACTER SET utf8;"
mysql -u root -p$MYSQL_PASS -e "CREATE USER '$REDMINE_DB_USER'@'localhost' IDENTIFIED BY '$REDMINE_DB_PASS';"
mysql -u root -p$MYSQL_PASS -e "GRANT ALL PRIVILEGES ON $REDMINE_DB_NAME.* TO '$REDMINE_DB_USER'@'localhost';"
# - extract Redmine archive and rename resulting directory
tar -xf $REDMINE_NAME-$REDMINE_VERSION.tar.gz
mv $REDMINE_NAME-$REDMINE_VERSION $REDMINE_NAME
# - create installation dir and move Redmine to installation directory
sudo mkdir -v $REDMINE_INSTALL_PATH
sudo mv -f $REDMINE_NAME $REDMINE_INSTALL_PATH
# - go to Redmine install directory
cd $REDMINE_INSTALL_PATH/$REDMINE_NAME
# - configure Redmine DB connection (create database.yml file)
cd config
sudo echo "production:" | tee database.yml
sudo echo "  adapter: mysql2" | tee -a database.yml
sudo echo "  database: $REDMINE_DB_NAME" | tee -a database.yml
sudo echo "  host: localhost" | tee -a database.yml
sudo echo "  username: $REDMINE_DB_USER" | tee -a database.yml
sudo echo "  password: $REDMINE_DB_PASS" | tee -a database.yml
cd $REDMINE_INSTALL_PATH/$REDMINE_NAME
pwd
# - install ruby bundler
gem install bundler
bundle install --without development test
# - generate secret token for Redmine sessions
bundle exec rake generate_secret_token
# - create DB structure, objects, default data
RAILS_ENV=production bundle exec rake db:migrate
RAILS_ENV=production REDMINE_LANG=$REDMINE_LANG bundle exec rake redmine:load_default_data
# - install Apache integration module - passenger
gem install passenger
passenger-install-apache2-module
# - copy files into apache directory (check contents!!!)
sudo cp $SCRIPT_HOME/apache/passenger.load $APACHE_SETTINGS/mods-available
sudo cp $SCRIPT_HOME/apache/passenger.conf $APACHE_SETTINGS/mods-available
echo "!!!!! Check settings in files: $APACHE_SETTINGS/mods-available/passenger.[conf|load] !"
# - enable installed mod under Apache2
sudo a2enmod passenger
sudo service apache2 restart
# - add virtual host on Apache2 (create redmine.conf file for host)
echo "<VirtualHost *:$REDMINE_PORT>" | sudo tee $APACHE_SETTINGS/sites-available/redmine.conf
echo "  DocumentRoot $REDMINE_INSTALL_PATH/$REDMINE_NAME/public" | sudo tee -a $APACHE_SETTINGS/sites-available/redmine.conf
echo "  <Directory $REDMINE_INSTALL_PATH/$REDMINE_NAME/public>" | sudo tee -a $APACHE_SETTINGS/sites-available/redmine.conf
echo "    AllowOverride all" | sudo tee -a $APACHE_SETTINGS/sites-available/redmine.conf
echo "    Options -MultiViews" | sudo tee -a $APACHE_SETTINGS/sites-available/redmine.conf
echo "    Require all granted" | sudo tee -a $APACHE_SETTINGS/sites-available/redmine.conf
echo "  </Directory>" | sudo tee -a $APACHE_SETTINGS/sites-available/redmine.conf
echo "</VirtualHost>" | sudo tee -a $APACHE_SETTINGS/sites-available/redmine.conf
sudo a2ensite redmine
# - advice for user
echo "!!!!! Add port $REDMINE_PORT to $APACHE_SETTINGS/ports.conf as line <Listen $REDMINE_PORT> and restart Apache !!!!!" 
# - last config option for Redmine
sudo cp $REDMINE_INSTALL_PATH/$REDMINE_NAME/public/dispatch.fcgi.example $REDMINE_INSTALL_PATH/$REDMINE_NAME/public/dispatch.fcgi
echo "!!!!! Restart Apache and check Redmine installation !!!!!"