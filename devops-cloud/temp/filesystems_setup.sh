#
#	- `df` - report file system disk space usage. Useful is command with keys: `df -hT`
#	- `lsblk` - list block devices, useful tool for listing existing devices
#	- `blkid` - locate/print block device attributes, see device UUID
#	- `fdisk` - manipulate disk partition table
#	- `resize2fs` - ext2/ext3/ext4 file system resizer
#	
#	

# unmount all from /etc/fstab file
sudo umount -a
# force unmount
sudo umount -f [TARGET]
# dry run - show what will be done - no writes in /etc/mtab
sudo umount -n [TARGET]
# simulate unmount, useful wit h-v/--verbose simulation
sudo umount --fake [-v/--verbose] [TARGET]

# fdisk interactive mode
sudo fdisk [TARGET]

