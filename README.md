# Project RESEARCH #
This project intended for any kind of research, experiments, etc.

## Repository contents ##
Repository contains some modules:
* [bigdata]
    * [hw1.mapreduce]
    * [hw2.hdfs]
    * [storm]
* [chat-app]
    * [chat-client]
    * [chat-server]
* [docs]
* [j2ee]
* [marklogic]
* [scripts]
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