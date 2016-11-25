#! /bin/bash
#
# ===================================================================
#   Backup script for MAIN (dept306) server. Script does backup:
#    - Redmine system
#    - Subversion repositories (by list)
#    - Jenkins server configuration/jobs/plugins
#   Backup file structure:
#    dept306_<date>_<time>.tar.gz
#     |- redmine
#         |- /files            <- Redmine attachements (files)
#         |- redmine.sql       <- Redmine DB backup script (mysql dump)
#     |- svn-<repo name>.dump  <- Subversion repository dump(s)
#     |- ....................   
#     |- svn-<repo name>.dump
#     |- jenkins               <- Jenkins config/jobs/plugins/other
#         |- <jenkins files> 
#
#   Created:  Gusev Dmitry, 29.10.2015
#   Modified: Gusev Dmitry, 04.12.2015
# ===================================================================

# -- Call other script for set environment for current process
source set_env.sh 

# -- Subversion repositories list for backup
SVN_REPOS_LIST="alexandrit balance dept306 most premier tavolga istok-spo"

# -- Temporary directory for server backup
mkdir -v /tmp/$BACKUP_NAME
echo "Backup directory [/tmp/$BACKUP_NAME] created."

# =================== REDMINE Backup
mkdir -v /tmp/$BACKUP_NAME/redmine
# -- Backup MySQL database for Redmine
mysqldump --user=$REDMINE_DB_USER --password=$REDMINE_DB_PASS $REDMINE_DB_NAME > /tmp/$BACKUP_NAME/redmine/$REDMINE_DB_BACKUP
echo "Redmine DB backup done."
# -- Backup files (attachements/documents) from Redmine
rsync -a $REDMINE_HOME/files /tmp/$BACKUP_NAME/redmine
echo "Redmine files backup done to [/tmp/$BACKUP_NAME/redmine]."

# =================== SUBVERSION Backup
for REPO in $SVN_REPOS_LIST
do
	# -- Dump specified repository
	# sudo needed for repository dump because permissions for repository's folders
	# have got only root user - in such manner svn was installed
	sudo svnadmin dump $SVN_REPOS_HOME/$REPO > /tmp/$BACKUP_NAME/$REPO.dump
	echo "Repository [$REPO] dumped to [/tmp/$BACKUP_NAME/$REPO.dump] file."
done

# =================== JENKINS Backup
sudo rsync -a $JENKINS_HOME /tmp/$BACKUP_NAME
# -- allow access to jenkins files for all (for backup)
sudo chmod -R 777 /tmp/$BACKUP_NAME/$JENKINS_NAME
echo "Jenkins files backup done to [/tmp/$BACKUP_NAME/$JENKINS_NAME]."

# =================== Create TAR GZIP, copy to destination, cleanup
# -- Create TAR GZIP archive
tar -czf /tmp/$BACKUP_NAME.tar.gz -C /tmp/$BACKUP_NAME .
echo "Backup TAR GZIP archive [/tmp/$BACKUP_NAME.tar.gz] created."
# -- Remove temporary dir (sudo - we copied jenkins files with sudo cmd)
rm -fdr /tmp/$BACKUP_NAME
echo "Clean up done (dir [/tmp/$BACKUP_NAME] deleted)."
# -- Copy server backup to destination storage
mkdir -p $BACKUP_DIR
cp /tmp/$BACKUP_NAME.tar.gz $BACKUP_DIR
echo "Server backup [/tmp/$BACKUP_NAME.tar.gz] copied to [$BACKUP_DIR]."