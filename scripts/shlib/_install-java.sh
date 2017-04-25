#!/bin/bash
#
# =============================================================================
#   This script installs Java and java-related tools:
#     * Oracle Java JDK, version see <_env.sh>
#     * Apache Ant - java build tool (version <_env.sh>). After installation,
#       Ant will be added to system wide PATH variable.
#     * Apache Maven - java build tool (version <_env.sh>). After installation,
#       Maven will be added to system wide PATH variable.
#
#   WARNING! Script should not be started as user 'root' (sudo ./<script_name>)!
#   It is recommended to update whole system before running this script!
#  
#   Created:  Gusev Dmitry, 27.11.2016
#   Modified: Gusev Dmitry, 24.04.2017
# =============================================================================

# todo: check, if tools are already installed!
# todo: setup local maven repository to /opt/.m2 directory (during maven installation)

# -- Installing Java
# - add alternate repository for Oracle Java JDK and update data from it
sudo -E add-apt-repository -y $JAVA_ALT_REPO
sudo apt-get -qy update

# - auto accept Oracle JDK license
sudo echo oracle-java$JAVA_VERSION-installer shared/accepted-oracle-license-v1-1 select true | sudo debconf-set-selections
# - install JDK (allow for unauthenticated)
sudo apt-get -qy --allow-unauthenticated install oracle-java$JAVA_VERSION-installer oracle-java$JAVA_VERSION-set-default

# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "$DEBUG_MODE" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi

# -- Installing Apache Ant
# - delete target Ant TAR GZ file if it exists
if [ -f $ANT_ARCHIVE ] ; then
    rm $ANT_ARCHIVE
fi
# - download Ant from repository
wget $ANT_BINARY_URL
# - extract ANT from archive
tar xvfz $ANT_ARCHIVE
# - set executable bit for <ant> - starting script
sudo chmod +x $ANT_NAME/bin/ant
# - move ANT to /opt directory
sudo mv $ANT_NAME /opt
# - add path to ANT executable to PATH variable (system wide).
# "" - in this quotes variables will be processed (values used)
# '' - in this quotes variables will be used 'as is' (no values)
echo "ANT_HOME=/opt/$ANT_NAME" | sudo tee /etc/profile.d/ant.sh
echo 'PATH="$ANT_HOME/bin:$PATH"' | sudo tee -a /etc/profile.d/ant.sh
sudo chmod +x /etc/profile.d/ant.sh

# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "$DEBUG_MODE" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi

# -- Installing Apache Maven
# - delete target Hadoop TAR GZ file if it exists
if [ -f $MAVEN_ARCHIVE ] ; then
    rm $MAVEN_ARCHIVE
fi
# - download Maven from repository
wget $MAVEN_BINARY_URL
# - extract MAVEN from archive
tar xvfz $MAVEN_ARCHIVE
# - set executable bit for <mvn> - starting script
sudo chmod +x $MAVEN_NAME/bin/mvn
# - move MAVEN to /opt directory
sudo mv $MAVEN_NAME /opt
# -- add path to MAVEN executable to PATH variable (system wide).
# "" - in this quotes variables will be processed (values used)
# '' - in this quotes variables will be used 'as is' (no values)
echo "M2_HOME=/opt/$MAVEN_NAME" | sudo tee /etc/profile.d/maven.sh
echo 'PATH="$M2_HOME/bin:$PATH"' | sudo tee -a /etc/profile.d/maven.sh
sudo chmod +x /etc/profile.d/maven.sh

# ***** DEBUG OUTPUT (wait for any key press) *****
if [ "$DEBUG_MODE" == "true" ]; then
	read -rsp $'Press any key to continue...\n' -n1 key
fi

# -- Reboot system after updating
if [ "$REBOOT_AFTER_UPDATE" == "YES" ]; then
    sudo reboot now
fi
