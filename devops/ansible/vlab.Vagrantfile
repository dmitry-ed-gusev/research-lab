# -*- mode: ruby -*-
# vi: set ft=ruby :

####################################################################################################
#
#   Vagrant config file for the Virtual Laboratory - set of virtual machines on the Oracle Virtual
#   Box hypervisor.
#
#   Created:  Dmitrii Gusev, 09.11.2025
#   Modified: Dmitrii Gusev, 10.11.2025
#
####################################################################################################

# - useful constants
VAGRANT_API_VERSION = "2"
VAGRANT_JSON_CONFIG = "vms-vlab-config.json"
# VAGRANT_JSON_CONFIG = "vms-vlab-small-config.json"
# VAGRANT_JSON_CONFIG = "vms-sample-config.json"
VAGRANT_DEFAULT_BOX_NAME = "server/fedora-cloud37-vbox"
# VAGRANT_DEFAULT_BOX_NAME = "server/fedora-cloud43-vbox"

# - initial requirements
Vagrant.require_version ">= 2.0.0" # Vagrant requirement
require 'json' # required JSON module

# -- read JSON file with config details
machines = JSON.parse(File.read(File.join(File.dirname(__FILE__), VAGRANT_JSON_CONFIG)))
# -- local PATH_SRC for mounting
$PathSrc = ENV['PATH_SRC'] || "."

# -- start of the vagrant configuration
Vagrant.configure(VAGRANT_API_VERSION) do |config|

  # -- Vagrant plugins settings
  config.vagrant.plugins = ["vagrant-hostmanager", "vagrant-vbguest"]
  if Vagrant.has_plugin?("vagrant-vbguest") # disable auto-update for the 'vb guest additions'
    config.vbguest.auto_update = false
  end

  # -- common system settings
  config.vm.box_check_update = false # don't check for updates of the base image
  config.vm.boot_timeout = 1200 # wait a while longer for vm to boot

  # -- common SSH settings
  config.ssh.forward_agent = true # disable auto-update for the 'vb guest additions'
  config.ssh.insert_key = false # use the standard vagrant ssh key (same key for all machines)

  # -- common settings - manage vms in the system file /etc/hosts
  config.hostmanager.enabled = true
  config.hostmanager.include_offline = true
  config.hostmanager.manage_guest = true
  config.hostmanager.manage_host = true

  # -- iterate through entries in JSON file - create a vm for each entry
  machines.each do |machine|

    # -- creating one vm with the specified parameters
    config.vm.define machine['name'] do |vmachine|

      # -- VM base parameters
      vmachine.vm.box = machine['box']
      vmachine.vm.hostname = machine['name']

      # -- VM network
      vmachine.vm.network 'private_network', ip: machine['ip_addr']
      vmachine.vm.network :forwarded_port, id: 'http', host: machine['forwarded_port'], guest: machine['app_port']
      vmachine.vm.network :forwarded_port, id: 'https', host: machine['forwarded_port'], guest: machine['app_port']
      vmachine.vm.network :forwarded_port, id: 'ssh', host: machine['forwarded_port'], guest: machine['app_port']

      # -- VM files sharing: set 'no_share' value to false to enable file sharing (description: the first
      #     argument is the path on the host to the actual folder, the second argument is the path on the
      #     guest to mount the folder and the optional third argument is a set of non-required options)
      vmachine.vm.synced_folder ".", "/vagrant", disabled: machine['no_share']

      # -- VM - set virtualbox parameters (customization) for each machine
      vmachine.vm.provider :virtualbox do |virtualbox|
        virtualbox.customize [
          "modifyvm", :id,
          "--audio", "none",
          "--cpus", machine['cpus'],
          "--memory", machine['memory'],
          "--graphicscontroller", "VMSVGA",
          "--vram", "64"
        ]
        virtualbox.gui = machine['gui']
        virtualbox.name = machine['name']
      end # end of customization of the virtualbox for each vm

    end # end of creating one vm

  end # end of for cycle - iteration over JSON

  # # -- install ansible locally on each vm (check???)
  # config.vm.provision "ansible_local" do |ansible|
  #   ansible.compatibility_mode = "2.0"
  #   ansible.galaxy_role_file = "roles/requirements.yml"
  #   ansible.galaxy_roles_path = "roles"
  #   ansible.playbook = "playbook.yml"
  #   ansible.verbose = "vv"
  # end

end
