<?php
session_start();
header('Content-Type: text/html; charset=utf-8');
include 'lib.php';

function __autoload($name) {
    include  $name . '.class.php';
}
$cur_script_role=1;
$db = db::connect("alexandrit");
//$db = db::connect("antares");
$user_data = $db->query("select session_id, role from web_users where login='" . $_SESSION["login"] . "'")->fetch(PDO::FETCH_OBJ);
$ses_id = $user_data->session_id;
$user_role = $user_data->role;
if($cur_script_role < $user_role){
    switch($user_role)
    {
        case 1:
            $user_class = "Администратор";
            break;
        case 2:
            $user_class = "Оператор";
            break;
        case 3:
            $user_class = "Пользователь";
            break;
        default:
            $user_class = "Неизвестно";
            break;
    }
    $msg = "НСД: пользователь класса ".$user_class." запросил скрипт ". __FILE__;
	make_journal(date("Y-m-d"), date("H:i:s"), $_SERVER['REMOTE_ADDR'],$_SESSION["user_id"],$_SESSION["login"], 5, '', '<tr><td>'.$msg.'</td></tr>', 0,'',$db);
    exit();
}else{
    if ($ses_id != session_id()){
       exit();
    }
}
$db = db::getInstance();
session_destroy();
$db->query("UPDATE web_users SET session_id=''");
//exec("/var/www/html/ippbx/tests/test_mode/set_test_mode.sh begin");
exec('/var/www/html/ippbx/files/cmdexec "/var/www/html/ippbx/tests/test_mode/run_test_mode.sh"')
?>
