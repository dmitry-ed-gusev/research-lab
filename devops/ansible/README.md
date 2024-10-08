<!-- cspell:ignore pywinrm testserver keypair newkey keyout rcvboxdrv -->

# Ansible Education Module

Ansible learning and education module. Used for learning purposes + various sources.
**Last updated: 11.08.2024**

Table Of Contents

[TOC]

## Ansible Version / Python Version

**Important Note!**

- for ansible 2.9.xx you need python 3.10.xx and older, the newer versions are not compatible with ansible
- for ansible 10 (latest versions) the python version - the newer = the better

## Prepare Environment

1. First of all: create and activate the virtual environment: `python -m venv .venv --prompt .venv-ansible`
2. Activate it: `source .venv/bin/activate`
3. Optionally, you may upgrade pip in the virtual environment: `python.exe -m pip install --upgrade pip`
4. Install ansible with additional dependencies (for managing windows and docker): `pip install ansible pywinrm docker` - this step is optional
5. Install ansible and (I would recommend) ansible-lint: `pip install ansible==2.9.7 ansible-lint=5.4.0`. In case you need more recent version of ansible/ansible-lint - do not specify exact versions.

## Vagrant: usage and restrictions

### Virtual Box Note

Check Virtual Box kernel drivers with command `sudo /sbin/rcvboxdrv <status|restart|...>` - sometimes after kernel upgrade Virtual Box needs to rebuild some modules with the provided kernel-dev modules (new dev modules with new kernel). If you need to install new dev modules - this command can help you with the necessary modules names (for installation).

Also after building you may need to run: `sudo /sbin/rcvboxdrv setup`

### Vagrant Usage

Since vagrant is closed for Russia, below is the additional repository for vagrant (russian vagrant). Also see the downloaded vagrant boxes on Yandex Cloud. It is worth to read also the article - see below.

- [ru vagrant I - boxes](http://vagrant.elab.pro/)
- [ru vagrant II](https://yamadharma.github.io/ru/post/2021/11/12/using-vagrant/)
- [habr article](https://habr.com/ru/articles/735700/)

#### Add Locally Downloaded Boxes to Vagrant

In order to add locally downloaded boxes to vagrant and start the vm, just perform:

```bash
    vagrant box add foo-box /path/to/vagrant-box.box
    vagrant init foo-box
    vagrant up [default]
```

#### Start Vagrant Machine

After executing the command `vagrant init ...` the Vagrantfile will be created in the current folder. All further commands should be executed from the folder containing Vagrantfile. After some time (approx. several minutes) you should see the output like below:

```bash
$ vagrant up
Bringing machine 'default' up with 'virtualbox' provider...
==> default: Importing base box 'ubuntu-focal64'...
==> default: Matching MAC address for NAT networking...
==> default: Setting the name of the VM: playbook_default_1717094266989_83025
==> default: Clearing any previously set network interfaces...
==> default: Preparing network interfaces based on configuration...
    default: Adapter 1: nat
==> default: Forwarding ports...
    default: 22 (guest) => 2222 (host) (adapter 1)
==> default: Running 'pre-boot' VM customizations...
==> default: Booting VM...
==> default: Waiting for machine to boot. This may take a few minutes...
    default: SSH address: 127.0.0.1:2222
    default: SSH username: vagrant
    default: SSH auth method: private key
    default: Warning: Connection reset. Retrying...
    default: Warning: Connection aborted. Retrying...
    default: 
    default: Vagrant insecure key detected. Vagrant will automatically replace
    default: this with a newly generated keypair for better security.
    default: 
    default: Inserting generated public key within guest...
    default: Removing insecure key from the guest if it's present...
    default: Key inserted! Disconnecting and reconnecting using new SSH key...
==> default: Machine booted and ready!
==> default: Checking for guest additions in VM...
    default: The guest additions on this VM do not match the installed version of
    default: VirtualBox! In most cases this is fine, but in rare cases it can
    default: prevent things such as shared folders from working properly. If you see
    default: shared folder errors, please make sure the guest additions within the
    default: virtual machine match the version of VirtualBox you have installed on
    default: your host and reload your VM.
    default:
    default: Guest Additions Version: 6.1.38
    default: VirtualBox Version: 7.0
==> default: Mounting shared folders...
```

**Note!** In some cases the VM is not able to start, so you can 'reset' the state - just delete the **Vagrantfile** and directory **.vagrant**.

#### Vagrant: Useful Commands

Below you can see some useful vagrant commands (you should be in the catalog with the Vagrantfile in order to execute below commands):

- `vagrant up` - power on the VM (you should be in a catalog with the Vagrantfile)
- `vagrant halt` - shutdown VM immediately
- `vagrant suspend` - pause the VM
- `vagrant resume` - start previously suspended VM
- `vagrant box list` - list of all imported boxes (images)
- `vagrant status` - status of the vagrant machine state (for the current Vagrantfile)
- `vagrant destroy -f` - destroy your virtual machine

### Connect to VM over SSH

TBD

## Single Vagrant VM

Firstly, run `vagrant ssh-config` in order to check ssh config for vagrant, the most important lines in the output are:

```yaml
  Host default
    HostName 127.0.0.1
    User vagrant
    Port 2202
    IdentityFile /home/dmitrii/projects/research-lab/devops/ansible/playbooks/.vagrant/machines/default/virtualbox/private_key
```

- `vagrant ssh` - connect to the currently running VM from the current Vagrant file (over SSH)
- `ssh vagrant@127.0.0.1 -p 2202 -i .vagrant/machines/default/virtualbox/private_key` - connect using auto generated private key

## Multiple Vagrant VMs

For 3 vagrant vms configuration, this key was suitable: **~/.vagrant.d/insecure_private_keys/vagrant.key.rsa**, also check the keys here: ~/.vagrant.d/insecure_private_keys/ and the key: ~/.vagrant.d/insecure_private_key.

## Using Ansible

See below some useful commands for Ansible:

```bash
    # ping the host from inventory, flag -vvvv - for verbosity
    ansible testserver -i inventory/vagrant.ini -m ping -vvvv

    # with ansible.cfg even simpler
    ansible testserver -m ping

    # uptime command on the remote server
    ansible testserver -m command -a uptime
    # same (as command is default module)
    ansible testserver -a uptime

    # execute command and return result
    ansible netbook -a "tail /var/log/dmesg"

    # run with root privileges -b/--become or settings in ansible.cfg (see there)
    ansible testserver -b -a "tail /var/log/dmesg"

    # install nginx on ubuntu (update_cache=yes - run 'apt update' before)
    ansible testserver -b -m package -a "name=nginx update_cache=yes"
    # restart nginx service
    ansible testserver -b -m service -a "name=nginx state=restarted"
```

## Advanced Ansible

TBD

## Some Additional Things

1. Creating self-signed SSL/TLS certificate (for nginx):
   `openssl req -x509 -nodes -days 365 -newkey rsa:2048 -subj /CN=localhost -keyout files/nginx.key -out files/nginx.crt`

2. ???

TBD
