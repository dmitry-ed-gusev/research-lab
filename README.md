# Project Research Laboratory

This project intended for any kind of research, experiments, etc.
*Dmitrii Gusev, 2014 - 2022*

## Repository contents ##
Repository contains some modules:  
* **[bigdata]**
    * [hw1.mapreduce]
    * [hw2.hdfs]
    * [storm]
* [chat-app]
    * [chat-client]
    * [chat-server]
* [docs]
* [j2ee]
* [marklogic]
* **[scripts]** This module contains scripts: bash, python. For Python scripts there are 
    dependencies (modules). Most of them can by simply installed with pip as  
    *pip install **module name***  
    For Windows-based OS you may use cygwin package.
    * **mock** - mocking library for python
    * **virtualenv** - tool for creating virtual environments for python
    * **matplotlib** (https://pypi.python.org/pypi/matplotlib), installation cmd line for Windows:  
      *python -m pip install --user matplotlib-2.0.2-cp27-cp27m-win_amd64.whl*  
      whl file name may vary, depending on python version and matplotlib version, for *nix system simply use pip.    
    * **jira** - library for interaction with JIRA by rest api
    * **prettytable** - library for generating pretty text ASCII tables
    * **pygal** -  ???
    * **gviz_api** - library for google vizualization API (Google Chart) 
    * **beautifulsoup4** - library for parsing html
    * **pygame** - library for 2D simple game applications
    * **pygal** - library for data visualization in python
    * **pygal_maps_world** - set of maps for pygal module
* [socialnet]
* [tmpresearch]
    * [hibernate]
    * [jsf2-example]
    * [unsorted]
    * [xml]
* [utilities]

## Some technical details ##
### Module [bigdata]. Deploy to cluster (by scp) ###
For deploy to remote/local server (via ssh/scp) specify cmd line parameters for maven:
* -DskipDeploy=false - deploy to remote host is off by default, turn it on
* -Dhost=\<host value> - remote host
* -Dport=\<port value> - remote port 
* -Duser=\<user value> - user for remote host
* -Dpass=\<pass value> - remote host user's password
* -Dpath=\<path value> - path for deploy on remote host

### Build on MSD-tuned Maven  ###
Create empty settings.xml file (with just empty settings tag), put it somewhere and, when build, just
specify: mvn [goals] -s [abs path to your custom (empty) settings.xml file]

**Example:**

mvn clean install -DskipDeploy=false -Duser=myuser -Dpass=mypass -Dhost=myhost -Dpath=/home/user

### What is this repository for? ###

* Quick summary
* Version
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### How do I get set up? ###

* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact