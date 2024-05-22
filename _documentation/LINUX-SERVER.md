# Setup Linux Server

**Last update: 01.01.2023**

As an example used servers:

- Ubuntu Server 22 LTS
- (TBD) Fedora Server ???
- (TBD) Fedora Workstation ???
- (TBD) CentOS ???

## Useful Resources

- [Red Hat docs source](https://www.redhat.com/sysadmin/)
- [Add user](https://linuxize.com/post/how-to-create-users-in-linux-using-the-useradd-command/)
- [USB mount/automount](https://linuxhint.com/automount-usb-ubuntu/)
- [FSTAB options (1)](https://linuxopsys.com/topics/linux-fstab-options)
- [FSTAB options (2)](https://help.ubuntu.com/community/Fstab)
- [SAMBA setup (1)](https://dev.to/techlearners/how-to-set-up-a-samba-server-in-ubuntu-and-share-files-seamlessly-5foa)
- [SAMBA setup (2)](https://www.makeuseof.com/set-up-network-shared-folder-ubuntu-with-samba/)
- [Share USB device](https://sleeplessbeastie.eu/2022/03/14/how-to-share-usb-device-over-network/)
- [LVM Guide](http://xgu.ru/wiki/LVM)
- [LVM Tutorial](https://linuxhint.com/lvm-ubuntu-tutorial/)
- [LVM Partitioning](https://www.redhat.com/sysadmin/lvm-vs-partitioning)
- [???](xxx)
- [???](xxx)

## Users Setup

- add user: without home catalog -> `sudo useradd username` with home catalog -> `sudo useradd -m username`
  For user being able to log in - need to set a password: `sudo passwd username`
- list users: `cat /etc/passwd | more`

## Toolset (useful utilities)

- `df` - report file system disk space usage. Useful is command with keys: `df -hT`
- `lsblk` - list block devices, useful tool for listing existing devices
- `blkid` - locate/print block device attributes, see device UUID
- `fdisk` - manipulate disk partition table
- `resize2fs` - ext2/ext3/ext4 file system resizer
- `` - ???
- `` - ???

## Mount USB drive (+make persistent mount)

By default Ubuntu server (and other server distrs) doesn't support USB automount. So we need to find our attached USB device and mount it by hands, then add automount option.

1. Attach the USB device
2. Use `lsblk` to list block devices and search for your USB device. Let's guess we found it here: `/dev/sdb1`. All attached system devices should be listed under **/dev** folder. Using **lsblk** you may see your device as **/sdb/sdb1**, but check the folder **/dev** - you may see your device as **/dev/sdb1** etc.
3. Use `blkid` to see attributes/UUIDs of the devices, UUID - can be used for automount and updating the **/etc/fstab** file
4. Create a folder for your drive: `sudo mkdir /mnt/<drive_name>`
5. Mount drive: `mount /dev/sdb1 /mnt/<drive_name>`
6. For permanent mount edit the file **/etc/fstab** and add the line like this one: `UUID=<your_device_UUID> /mnt/<drive_name> <fstype> defaults 0 0`, where fstype - file system type for your device (ext4, etc.). Recommended settings line is: `UUID=<your_device_UUID> /mnt/<drive_name> auto user,umask=000,utf8 0 0` - I've tried it in order mounted folder to be writeable.
7. After changing the **/etc/fstab** execute command `mount -a` - in order to re-mount all partitions
8. *(optional)* Try to reboot and see that your USB drive mounted automatically

## Share Folder Over Network (using SAMBA)

1. Create or select a folder for sharing, for example: `/media/myusbdrive`
2. Edit file **/etc/samba/smb.conf** and add to the end of the file:
    <pre>
    [myusbdrive]  # share name, may be differ from folder name
        comment = Samba on Ubuntu
        path = /media/myusbdrive
        read only = no
        browseable = yes
        available = yes
        public = yes
        # guest ok = yes  # synonym for public
        writeable = yes
        valid users = myusername  # add your user
        create mask = 0644  # -rw-r--r--
        directory mask = 0755  # -rwxr-xr-x
        force user = myusername
    </pre>
   With the setup above, the share will be visible/writable without authentication - in the same network. Note: myusername should be a valid system user - see below how to add it to samba
3. Restart the service: `sudo service smbd restart`
4. Add samba to UFW on the server: `sudo ufw allow samba` [(Uncomplicated Firewall)](https://wiki.ubuntu.com/UncomplicatedFirewall)
5. Add user for SAMBA: `sudo smbpasswd -a <username>` *Note: the user should be existing system user.*
6. Check the SAMBA status: `sudo systemctl status smbd`

## Practice experience: share folder/usb from Linux to Mac (Samba)

1. Create partition/USB, choose vfat filesystem on it (use `mkfs.vfat <target path>` to format partition), use **exfat** on USB drive
2. Use `blkid` to find UUID of the device
3. Add device to the **/etc/fstab** file for permanent mount (add the line like below in order your share should be writable):
   `UUID=B558-638D /media/share-name auto user,umask=000,utf8 0 0`
   Use your UUID and mount folder.
4. Install samba if you haven't done it (depends on the OS):
   - Ubuntu: `sudo apt install samba`
   - Fedora: `TBD`
   - CentOS: `TBD`
5. Add the following to the file **/etc/samba/smb.conf**:
   <pre>
    [myusbdrive]  # share name, may be differ from folder name
        comment = Samba on Ubuntu
        path = /media/myusbdrive
        read only = no
        browseable = yes
        available = yes
        public = yes
        # guest ok = yes  # synonym for public
        writeable = yes
        valid users = myusername  # add your user
        create mask = 0644  # -rw-r--r--
        directory mask = 0755  # -rwxr-xr-x
        force user = myusername
    </pre>
6. Add system user (if not already) - myusername (any type of user) and add samba to UFW (see above)
7. Add user to samba: `sudo smbpasswd -a myusername` with new password
8. Check your shared folder is accessible/writeable from outside in the same network

## Working with LVM

In order to start working with LVM use: `sudo lvm`. For help about commands use: `>lvm help` - inside the LVM. The following commands suppose to be executed in the LVM shell (cmd line should be like `>lvm ...`), in case command should be executed in the system shell - it will be mentioned.

- find all existing block devices: `lvmdiskscan`
- display physical volumes/volumes groups/logical volumes - use the corresponding command: `pvdisplay` or `vgdisplay` or `lvdisplay`
- **create logical volume (LV)**
  - use commands `vgscan` and `vgdisplay` for review of the current volume groups (where you want to create your logical volume)
  - use command `lvcreate -L 10G -n lv1 vg1` for creating a logical volume with size of 10G, name lv1 in the volume group vg1
  - use command `lvdisplay` to see the logical volumes info and see that your volume was added
  - the logical volume further should be managed as any other partition. It needs a filesystem and a mount point - see the corresponding section
- **resize existing logical volume (LV)**
  - use `lvscan` to see the logical volumes list and size, also it is worth to see the physical size of the device(s): `pvscan` - see free space available for resizing
  - use command `lvresize -L +10G vg1/lv1` with your volume group and logical volume names and necessary size for increase
  - check that volume size was changed: `lvscan`
  - after the logical volume resize, you have to resize the partition located on the volume, use the command `sudo resize2fs <path>` (path for existing partition can be found using `df -h` command)
- **zzzz**
