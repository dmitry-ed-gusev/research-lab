#!/usr/bin/php

<?php

function test_user($username)
{
    if(!posix_getpwnam($username))
    {
        $db = new PDO("pgsql:host=localhost; dbname=asterisk", "alexandrit", "s12345678");
        $sql = "SELECT 1 AS result FROM web_users WHERE login='$username'";
        $query_result = $db->query($sql);
        if($query_result)
        {
            if(!$query_result->rowCount())
            {
                return TRUE;
            }
            else
            {
                return FALSE;
            }
        }
    }
    else
    {
        return FALSE;
    }
}

function new_username($prefix='user')
{
    return sprintf("%s%08d", $prefix, rand(0,99999999));
}

$user_role =    array(
                        0 => 's',
                        1 => 'a',
                        2 => 'u'
                    );
$user_pref =    array(
                        0 => 'test_super_',
                        1 => 'test_admin_',
                        2 => 'test_user_'
                    );
$user_pass =    array(
                        0 => 'ts12345678',
                        1 => 'ta12345678',
                        2 => 'tu12345678'
                    );
$f = fopen('/var/www/html/ippbx/tests/test_mode/users', 'w');
if($f)
{
    for($i = 0; $i < 3; $i++)
    {
        while(!test_user(($un=new_username($user_pref[$i]))));
        $user_string = sprintf("%s:%s:%s", $user_role[$i], $un, $user_pass[$i]);
        fwrite($f, "$user_string\n");
    }
    fclose($f);
}

?>