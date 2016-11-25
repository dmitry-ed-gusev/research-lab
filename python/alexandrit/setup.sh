#!/bin/sh

MODULE_NAME=security-module
PACKAGE_NAME=$MODULE_NAME.deb
TESTER_PACKAGE_NAME=alexandrit-tester.deb
INSTALL_DIR=/usr/local/$MODULE_NAME
CONFIG_FILE=/etc/security-module.conf.py
BACKUP_DIR=/usr/security-module-backups


DEPS="python-egenix-mxdatetime python-egenix-mxtools python-psycopg2"
for i in $DEPS;
    do 
	apt-get download $i
	dpkg -i $i*
done

dpkg -i $TESTER_PACKAGE_NAME
dpkg -i $PACKAGE_NAME
echo "Установка пакетов модуля безопасности завершена"

cat /etc/fstab | awk '$1 ~/UUID/' | sed 's/errors=remount-ro/\0,secdel/g' > tmp_fstab
cp tmp_fstab /etc/fstab

echo "Настройка операционной системы для обеспечения запрета вывода на внешние носители и гарантированной очистки памяти"

