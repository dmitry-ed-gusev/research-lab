#!/bin/bash

stop_services()
{
    daemon_pid=`ps axl 2>/dev/null | grep get_ami_log.php | grep -v grep | awk '{print $3}'`
    /usr/sbin/service asterisk stop
    kill $daemon_pid
    /usr/sbin/service apache2 stop
    /usr/sbin/service postgresql stop
    #stop security module
    /usr/sbin/service security-module stop
}

start_services()
{
    /usr/sbin/service postgresql start
    /usr/sbin/service asterisk start
    sleep 1
    /usr/bin/php /var/lib/asterisk/agi-bin/get_ami_log.php >/dev/null 2>&1 &
    /usr/sbin/service apache2 start
    #start security module
    /usr/sbin/service security-module start
}

sed_prepare_file()
{
    template=$1
    output=$2
    user_super=$3
    user_admin=$4
    user_user=$5
    
    cat $template | sed "{s/__SED_SUBSTITUTE_SUPER__/'$user_super'/;s/__SED_SUBSTITUTE_ADMIN__/'$user_admin'/;s/__SED_SUBSTITUTE_USER__/'$user_user'/}" >$output 2>/dev/null
}

make_test_loginform()
{
    user_super=$1
    user_admin=$2
    user_user=$3
    
    mkdir -p /var/www/html/ippbx/tests/test_mode/.backup
    cp -pf /var/www/html/ippbx/php/loginform.php /var/www/html/ippbx/tests/test_mode/.backup/loginform.php
    
    sed_prepare_file /var/www/html/ippbx/tests/test_mode/__loginform.php /var/www/html/ippbx/php/loginform.php $user_super $user_admin $user_user
}

make_default_loginform()
{
    cp -pf /var/www/html/ippbx/tests/test_mode/.backup/loginform.php /var/www/html/ippbx/php/loginform.php
}

make_test_db_class()
{
    user_super=$1
    user_admin=$2
    user_user=$3
    
    mkdir -p /var/www/html/ippbx/tests/test_mode/.backup
    cp -pf /var/www/html/ippbx/php/db.class.php /var/www/html/ippbx/tests/test_mode/.backup/db.class.php
    
    sed_prepare_file /var/www/html/ippbx/tests/test_mode/__db.class.php /var/www/html/ippbx/php/db.class.php $user_super $user_admin $user_user
}

make_default_db_class()
{
    cp -pf /var/www/html/ippbx/tests/test_mode/.backup/db.class.php /var/www/html/ippbx/php/db.class.php
}

make_test_pg_hba()
{
    user_super=$1
    user_admin=$2
    user_user=$3
    
    mkdir -p /var/www/html/ippbx/tests/test_mode/.backup
    cp -pf /etc/postgresql/9.1/alexandrit/pg_hba.conf /var/www/html/ippbx/tests/test_mode/.backup/pg_hba.conf
    
    echo "local	asterisk	$user_super	md5" >> /etc/postgresql/9.1/alexandrit/pg_hba.conf
    echo "host	asterisk	$user_super	127.0.0.0/8	md5" >> /etc/postgresql/9.1/alexandrit/pg_hba.conf
    echo "local	asterisk	$user_admin	md5" >> /etc/postgresql/9.1/alexandrit/pg_hba.conf
    echo "host	asterisk	$user_admin	127.0.0.0/8	md5" >> /etc/postgresql/9.1/alexandrit/pg_hba.conf
    echo "local	asterisk	$user_user	md5" >> /etc/postgresql/9.1/alexandrit/pg_hba.conf
    echo "host	asterisk	$user_user	127.0.0.0/8	md5" >> /etc/postgresql/9.1/alexandrit/pg_hba.conf
}

make_default_pg_hba()
{
    cp -pf /var/www/html/ippbx/tests/test_mode/.backup/pg_hba.conf /etc/postgresql/9.1/alexandrit/pg_hba.conf
}

get_test_user_name()
{
    role=$1
    cat /var/www/html/ippbx/tests/test_mode/users | grep '^'$role | awk -F ':' '{print $2}'
}

get_test_user_pass()
{
    role=$1
    cat /var/www/html/ippbx/tests/test_mode/users | grep '^'$role | awk -F ':' '{print $3}'
}

new_os_user()
{
    name=$1
    pass=$2
    role=$3
    case $role in
        "s")
            au_opts="--shell=/bin/false --no-create-home --ingroup alexandrit"
        ;;
        "a")
            au_opts="--shell=/bin/false --no-create-home --ingroup alexandrit"
        ;;
        "u")
            au_opts="--shell=/bin/false --no-create-home"
        ;;
    esac
    echo -e $pass'\n'$pass'\n\n\n\n\n\nY\n' | adduser $au_opts $name
    uid=`id -u $name`
    gid=`id -g $name`
    echo "$name:0:0:0:0" > /etc/parsec/macdb/$uid
    chmod 0640 /etc/parsec/macdb/$uid
    chown root:$gid /etc/parsec/macdb/$uid
    setfacl -m group::r /etc/parsec/macdb/$uid
    setfacl -m other::- /etc/parsec/macdb/$uid
    echo "$name:0:0" > /etc/parsec/capdb/$uid
    chmod 0640 /etc/parsec/capdb/$uid
    chown root:$gid /etc/parsec/capdb/$uid
    setfacl -m group::r /etc/parsec/capdb/$uid
    setfacl -m other::- /etc/parsec/capdb/$uid
    if [ "x$role" = "xu" ]
    then
        setfacl -R -m u:$name:r /var/www/html/ippbx
        find /var/www/html/ippbx -type d -exec setfacl -m u:$name:rx {} \;
    fi
}

delete_os_user()
{
    name=$1
    uid=`id -u $name`
    setfacl -R -x u:$name: /var/www/html/ippbx
    deluser $name
    rm -f /etc/parsec/macdb/$uid
    rm -f /etc/parsec/capdb/$uid
}

new_db_user()
{
    name=$1
    pass=$2
    role=$3
    case $role in
        "s")
            sql_opts="alexandrit"
        ;;
        "a")
            sql_opts="adba"
        ;;
        "u")
            sql_opts="adbu"
        ;;
    esac
    sql="CREATE ROLE $name WITH INHERIT LOGIN PASSWORD '$pass' IN ROLE $sql_opts;"
    cmd='psql -c "'$sql'" asterisk'
    su -c "$cmd" alexandrit
}

delete_db_user()
{
    name=$1;
    sql="DROP ROLE $name;"
    cmd='psql -c "'$sql'" asterisk'
    su -c "$cmd" alexandrit
}

new_web_user()
{
    name=$1
    pass=$2
    role=$3
    case $role in
        "s")
            sql_opts="1"
        ;;
        "a")
            sql_opts="2"
        ;;
        "u")
            sql_opts="3"
        ;;
    esac
    sql="INSERT INTO web_users(name,login,pass,role,online,time_prof) VALUES('User_$role','$name', md5('$pass'),'$sql_opts','1970-01-01 00:00:00','1');"
    cmd='psql -c "'$sql'" asterisk'
    su -c "$cmd" alexandrit
}

delete_web_user()
{
    name=$1;
    sql="DELETE FROM web_users WHERE login='$name';"
    cmd='psql -c "'$sql'" asterisk'
    su -c "$cmd" alexandrit
}

create_test_table()
{
    owner=$1
    sql="CREATE TABLE test_tbl(id SERIAL NOT NULL PRIMARY KEY,var_name CHARACTER VARYING(255) NOT NULL UNIQUE,var_value CHARACTER VARYING(255) NOT NULL DEFAULT '');"
    cmd='psql -c "'$sql'" asterisk'
    su -c "$cmd" alexandrit
    sql="ALTER TABLE test_tbl OWNER TO $owner;"
    cmd='psql -c "'$sql'" asterisk'
    su -c "$cmd" alexandrit
    sql="GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE test_tbl TO adba;"
    cmd='psql -c "'$sql'" asterisk'
    su -c "$cmd" alexandrit
    sql="GRANT ALL ON SEQUENCE test_tbl_id_seq TO adba;"
    cmd='psql -c "'$sql'" asterisk'
    su -c "$cmd" alexandrit
    sql="GRANT SELECT ON TABLE test_tbl TO adbu;"
    cmd='psql -c "'$sql'" asterisk'
    su -c "$cmd" alexandrit
}

drop_test_table()
{
    sql="DROP TABLE test_tbl;"
    cmd='psql -c "'$sql'" asterisk'
    su -c "$cmd" alexandrit
}

lock_web_gui()
{
    echo $$ >/var/run/test_mode_running
}

unlock_web_gui()
{
    rm -f /var/run/test_mode_running
}

update_sec_mod()
{
    /var/www/html/ippbx/tests/test_mode/secmod_update.sh
}

begin_test_mode()
{
    if [ -f /var/www/html/ippbx/tests/test_mode/users ]
    then
        end_test_mode
    fi
    
    lock_web_gui
    
    /var/www/html/ippbx/tests/test_mode/gen_test_users.php
    
    stop_services
    make_test_pg_hba `get_test_user_name s` `get_test_user_name a` `get_test_user_name u`
    make_test_loginform `get_test_user_name s` `get_test_user_name a` `get_test_user_name u`
    make_test_db_class `get_test_user_name s` `get_test_user_name a` `get_test_user_name u`
    #update security module
    update_sec_mod
    start_services
    
    for role in s a u
    do
        user_name=`get_test_user_name $role`
        user_pass=`get_test_user_pass $role`
        new_os_user $user_name $user_pass $role
        new_db_user $user_name $user_pass $role
        new_web_user $user_name $user_pass $role
    done
    
    create_test_table `get_test_user_name s`
}

end_test_mode()
{
    drop_test_table
    
    for role in s a u
    do
        user_name=`get_test_user_name $role`
        delete_web_user $user_name
        delete_db_user $user_name
        delete_os_user $user_name
    done
    
    stop_services
    make_default_pg_hba
    make_default_loginform
    make_default_db_class
    #update security module
    update_sec_mod
    start_services
    
    rm -f /var/www/html/ippbx/tests/test_mode/.backup/*
    rm -f /var/www/html/ippbx/tests/test_mode/users
    
    unlock_web_gui
}

case $1 in
    "begin")
        begin_test_mode
    ;;
    "end")
        end_test_mode
    ;;
    "*")
        echo "set_test_mode.sh [begin|end]"
    ;;
esac
