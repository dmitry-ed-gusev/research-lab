#!/bin/bash

mkdir target
cp -f setup.sh target/ &&

chmod 0775 alexandrit-tester/DEBIAN/postinst alexandrit-tester/DEBIAN/preinst &&
dpkg -b ./alexandrit-tester ./target/alexandrit-tester.deb &&

chmod 0775 security-module/DEBIAN/postinst &&
dpkg -b ./security-module ./target/security-module.deb &&
cd .. &&
exit 0
exit 1