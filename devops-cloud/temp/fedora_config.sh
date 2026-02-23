###############################################################################

# --- fedora 38 to 39 / 39 to 40 / 40 to 41 ---
# upgrade the current system + install upgrade plugin, next time - can be skipped
sudo dnf upgrade --refresh
sudo dnf install dnf-plugin-system-upgrade
# download ver 39/40/41/42
sudo dnf system-upgrade download --releasever=39

# perform upgrade - reboot and wait... (38 -> 39 -> 40 -> 41)
sudo dnf system-upgrade reboot
# perform upgrade - 41 -> 42
sudo dnf5 offline reboot


# after upgrade - reboot again. The last command is cleaning all the old Linux kernels.
sudo dnf system-upgrade clean
sudo dnf clean packages
# remove old kernels - except the latest one
sudo dnf remove $(dnf repoquery --installonly --latest-limit=-1 -q) -y
sudo reboot

###############################################################################

# install only once, it is saved between upgrades
sudo dnf install remove-retired-packages
remove-retired-packages

###############################################################################

# show duplicate packages
sudo dnf repoquery --duplicates
# remove duplicates - for fedora 40 and below, doesn't worn on fedora 41
sudo dnf remove --duplicates

###############################################################################

# --- option 1. install docker + docker-compose on fedora 42 - from fedora repos
sudo dnf install docker-cli containerd
sudo dnf docker-compose
sudo dnf docker-switch (docker-compose-switch???)

# --- option 2. install docker+docker-compose on fedora 42 - from docker's repos
# remove old/proprietary packages
sudo dnf remove docker \
                docker-client \
                docker-client-latest \
                docker-common \
                docker-latest \
                docker-latest-logrotate \
                docker-logrotate \
                docker-selinux \
                docker-engine-selinux \
                docker-engine
# install plugins and docker repo
sudo dnf -y install dnf-plugins-core
sudo dnf-3 config-manager --add-repo https://download.docker.com/linux/fedora/docker-ce.repo
# install the latest version of docker + docker-compose plugin. This command installs Docker, but it doesn't start Docker.
# It also creates a docker group, however, it doesn't add any users to the group by default
sudo dnf install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
# this command configures the Docker systemd service to start automatically when you boot your system,
# if you don't want Docker to start automatically, use <sudo systemctl start docker> instead.
sudo systemctl enable --now docker
# verify docker installation - run simple hello-world image
sudo docker run hello-world
# review linux post-install steps: https://docs.docker.com/engine/install/linux-postinstall/
# post-install steps (running docker without root)
sudo groupadd docker
sudo usermod -aG docker $USER
# check you may run docker without root privileges
docker run hello-world











