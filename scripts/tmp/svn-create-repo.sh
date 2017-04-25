#! /bin/bash
#
# ===================================================================
#   Create subversion repository with svn:// access and standard
#   access control list (dept 306). Directory structure (catalogs) is
#   standard - see dept 306 documentation.
#   This script needs sudo rights.
#
#   Created:  Gusev Dmitry, 02.11.2015
#   Modified:
# ===================================================================

# -- Check command line arguments count - should be at least one (lt -> is less than)
if [ "$#" -lt 1 ]; then
	echo "Illegal number of parameters, should be at least 1!"
	exit
fi 

# -- Check cmd line arguments and set FALSE flag for some installations
for arg in "$@"
do
	case "$arg" in
    -nostruct) CREATE_STRUCTURE=NO
			   ;;
	esac
done

# -- Call other script for set environment for current process
source set_env.sh 

# -- Create repository named $1 with appropriate structure
sudo svnadmin create $SVN_REPOS_HOME/$1
echo "Repository $1 created."

# -- Copy config files to created repository
sudo cp -fv subversion/passwd $SVN_REPOS_HOME/$1/conf
sudo cp -fv subversion/svnserve.conf $SVN_REPOS_HOME/$1/conf
echo "Files [svnserve.conf] and [passwd] copied to repository $1."

# -- Write (append) admin user to repository passwd file
echo "$SVN_REPOS_ADMIN = $SVN_REPOS_ADMIN_PASS" | sudo tee -a $SVN_REPOS_HOME/$1/conf/passwd
echo "Admin user: $SVN_REPOS_ADMIN/$SVN_REPOS_ADMIN_PASS added to repository config."

# -- Restart svnserve to changes to take effect
sudo /etc/init.d/$SVN_STARTUP_SCRIPT restart
echo "Subversion restarted."

if [ "$CREATE_STRUCTURE" == "NO" ]; then
	echo "!!! Repository structure creation skipped !!!"
else
	# -- Create appropriate structure in repository
	sudo svn mkdir svn://localhost/$1/implementation/database -m "$SVN_INITIAL_MSG" --parents --username $SVN_REPOS_ADMIN --password $SVN_REPOS_ADMIN_PASS --non-interactive
	sudo svn mkdir svn://localhost/$1/implementation/documents -m "$SVN_INITIAL_MSG" --parents --username $SVN_REPOS_ADMIN --password $SVN_REPOS_ADMIN_PASS --non-interactive
	sudo svn mkdir svn://localhost/$1/implementation/scripts -m "$SVN_INITIAL_MSG" --parents --username $SVN_REPOS_ADMIN --password $SVN_REPOS_ADMIN_PASS --non-interactive
	sudo svn mkdir svn://localhost/$1/implementation/sources/trunk -m "$SVN_INITIAL_MSG" --parents --username $SVN_REPOS_ADMIN --password $SVN_REPOS_ADMIN_PASS --non-interactive
	sudo svn mkdir svn://localhost/$1/implementation/sources/tags -m "$SVN_INITIAL_MSG" --parents --username $SVN_REPOS_ADMIN --password $SVN_REPOS_ADMIN_PASS --non-interactive
	sudo svn mkdir svn://localhost/$1/implementation/sources/branches -m "$SVN_INITIAL_MSG" --parents --username $SVN_REPOS_ADMIN --password $SVN_REPOS_ADMIN_PASS --non-interactive
	sudo svn mkdir svn://localhost/$1/implementation/utils -m "$SVN_INITIAL_MSG" --parents --username $SVN_REPOS_ADMIN --password $SVN_REPOS_ADMIN_PASS --non-interactive
	sudo svn mkdir svn://localhost/$1/management -m "$SVN_INITIAL_MSG" --parents --username $SVN_REPOS_ADMIN --password $SVN_REPOS_ADMIN_PASS --non-interactive
	sudo svn mkdir svn://localhost/$1/production -m "$SVN_INITIAL_MSG" --parents --username $SVN_REPOS_ADMIN --password $SVN_REPOS_ADMIN_PASS --non-interactive
	sudo svn mkdir svn://localhost/$1/requirements -m "$SVN_INITIAL_MSG" --parents --username $SVN_REPOS_ADMIN --password $SVN_REPOS_ADMIN_PASS --non-interactive
	sudo svn mkdir svn://localhost/$1/standards -m "$SVN_INITIAL_MSG" --parents --username $SVN_REPOS_ADMIN --password $SVN_REPOS_ADMIN_PASS --non-interactive
	sudo svn mkdir svn://localhost/$1/tests/cases -m "$SVN_INITIAL_MSG" --parents --username $SVN_REPOS_ADMIN --password $SVN_REPOS_ADMIN_PASS --non-interactive
	sudo svn mkdir svn://localhost/$1/tests/data -m "$SVN_INITIAL_MSG" --parents --username $SVN_REPOS_ADMIN --password $SVN_REPOS_ADMIN_PASS --non-interactive
	sudo svn mkdir svn://localhost/$1/tests/plan -m "$SVN_INITIAL_MSG" --parents --username $SVN_REPOS_ADMIN --password $SVN_REPOS_ADMIN_PASS --non-interactive
	sudo svn mkdir svn://localhost/$1/tests/results -m "$SVN_INITIAL_MSG" --parents --username $SVN_REPOS_ADMIN --password $SVN_REPOS_ADMIN_PASS --non-interactive
fi
