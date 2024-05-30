<!-- cspell:ignore pywinrm -->

# Ansible Education Module

## Prepare Environment

1. First of all: create and activate the virtual environment: `python -m venv .venv --prompt .venv-ansible`
2. Activate it: `source .venv/bin/activate`
3. Optionally, you may upgrade pip in the virtual environment: `python.exe -m pip install --upgrade pip`
4. Install ansible with additional dependencies (for managing windows and docker): `pip install ansible pywinrm docker`
5. ???

## Vagrant: usage and restrictions

Since vagrant is closed for Russia, here is additional repository for it (russian vagrant): [ru vagrant](http://vagrant.elab.pro/).
Also see downloaded vagrant boxes on Yandex Cloud. It is worth to read also [this article](https://habr.com/ru/articles/735700/).

In order to add locally downloaded boxes to vagrant and start the vm, just perform:

```bash
    vagrant box add foo-box /path/to/vagrant-box.box
    vagrant init foo-box
    vagrant up [default]
```

After executing the command `vagrant init ...` the Vagrantfile will be created in the current folder. All further commands should be executed from the folder containing Vagrantfile.
After some time (approx. several minutes) you should see the output like below:

```sh
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

Below you can see some useful vagrant commands (you should be in the catalog with the Vagrantfile in order to execute below commands):

- `vagrant up` - power on the VM (you should be in a catalog with the Vagrantfile)
- `vagrant halt` - shutdown VM immediately
- `vagrant suspend` - pause the VM
- `vagrant resume` - start previously suspended VM
- `vagrant box list` - list of all imported boxes (images)
- `vagrant status` - status of the vagrant machine state (for the current Vagrantfile)

### Connect to VM

- `vagrant ssh` - connect to the currently running VM from th current Vagrant file (over SSH)
- `ssh vagrant@127.0.0.1 -p 2222 -i .vagrant/machines/default/virtualbox/private_key` - connect using auto generated private key

## Using Ansible

TBD
