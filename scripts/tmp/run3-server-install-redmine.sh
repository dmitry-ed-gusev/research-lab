#!/bin/bash
#
# =============================================================================
#   This script installs Redmine bug/task tracker. Installing Redmine moved to
#   separate script, because we have to restart server after installing 
#   support for Ruby/Rvm/Rails.
#
#   WARNING! Script should not be started as user 'root' (sudo ./<script_name>)!
#  
#   Created:  Gusev Dmitry, 27.10.2015
#   Modified: Gusev Dmitry, 21.03.2016
# =============================================================================

# --- DEBUG MODE (ON/OFF) ---
# - If DEBUG=true script will wait for a key press after every logic part installation. Set DEBUG
# - value to any other, than "true" - script will slip any question.
DEBUG=true

# -- Call other script for set environment for current process
source set_env.sh

# -- Install Redmine (current user and www-data should be in "rvm" group)
# - install some necessary packages (order of packages is important!)
sudo apt-get -qy install libmysqlclient-dev apache2-threaded-dev libmagickwand-dev libcurl4-openssl-dev
# - set up MySql database for Redmine
mysql -u root -p$MYSQL_PASS -e "CREATE DATABASE $REDMINE_DB_NAME CHARACTER SET utf8;"
mysql -u root -p$MYSQL_PASS -e "CREATE USER '$REDMINE_DB_USER'@'localhost' IDENTIFIED BY '$REDMINE_DB_PASS';"
mysql -u root -p$MYSQL_PASS -e "GRANT ALL PRIVILEGES ON $REDMINE_DB_NAME.* TO '$REDMINE_DB_USER'@'localhost';"
# - extract Redmine archive (in current directory) and rename resulting directory
tar -xf $SOFTWARE_HOME/$REDMINE_NAME-$REDMINE_VERSION.tar.gz
# - rename Redmine unpakced directory
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

# - get version of PASSENGER module (after installation)
PASSENGER_VERSION=$(gem list | grep "passenger" | grep -oP "[0-9.]+")
# - root folder for PASSENGER Apache module
PASSANGER_ROOT="PassengerRoot /usr/local/rvm/gems/ruby-2.2.1/gems/passenger-$PASSENGER_VERSION"
# - ruby version for PASSENGER Apache module
PASSENGER_DEFAULT_RUBY="PassengerDefaultRuby /usr/local/rvm/gems/ruby-2.2.1/wrappers/ruby"
# - PASSENGER load module
PASSENGER_MODULE_LOAD="LoadModule passenger_module /usr/local/rvm/gems/ruby-2.2.1/gems/passenger-$PASSENGER_VERSION/buildout/apache2/mod_passenger.so"
# - create file [passenger.load]
echo "$PASSENGER_MODULE_LOAD" | sudo tee $APACHE_SETTINGS/mods-available/passenger.load
# - create file [passenger.conf]
echo "<IfModule mod_passenger.c>" | sudo tee $APACHE_SETTINGS/mods-available/passenger.conf
echo "  $PASSANGER_ROOT" | sudo tee -a $APACHE_SETTINGS/mods-available/passenger.conf
echo "  $PASSENGER_DEFAULT_RUBY" | sudo tee -a $APACHE_SETTINGS/mods-available/passenger.conf
echo "</IfModule>" | sudo tee -a $APACHE_SETTINGS/mods-available/passenger.conf

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

# - add Redmine port to Apache config
echo "Listen $REDMINE_PORT" | sudo tee -a $APACHE_SETTINGS/ports.conf
# - restart Apache
sudo service apache2 restart

# - last config option for Redmine
sudo cp $REDMINE_INSTALL_PATH/$REDMINE_NAME/public/dispatch.fcgi.example $REDMINE_INSTALL_PATH/$REDMINE_NAME/public/dispatch.fcgi

# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "$DEBUG" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi

# -- Reboot after installation
sudo reboot now
