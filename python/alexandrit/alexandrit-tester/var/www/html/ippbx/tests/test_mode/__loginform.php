<?php

//Работает на Астре

include 'lib.php';
session_start();
header('Content-Type: text/html; charset=utf-8');
$login = $_POST["login"];
$pass = $_POST["pass"];
if (isset($_POST["flag"])) {
    $f = $_POST["flag"];
} else {
    $f = '0';
}

function __autoload($data) {
    include $data . '.class.php';
}

$db = db::getInstance();
$login = (stripslashes(strip_tags($login)));
$pass = md5((stripslashes(strip_tags($pass))));
$sess_id = session_id();
$target_role=-1;
switch($_SERVER["REMOTE_USER"]){
	case __SED_SUBSTITUTE_SUPER__:
		$target_role=1;
		break;
	case __SED_SUBSTITUTE_ADMIN__:
		$target_role=2;
		break;
	case __SED_SUBSTITUTE_USER__:
		$target_role=3;
		break;
	default:
		$target_role=-1;
		break;
}
$sql = "SELECT id, name, role, ip, session_id, online FROM web_users WHERE login = '" . $login . "' and pass='" . $pass . "' and role=".$target_role;
$result = $db->query($sql);
$obj = $result->fetch(PDO::FETCH_OBJ);
$ip_last=$db->query("select ip_client from events where user_login='".$login."' and event_type=1 order by id desc limit 1 ")->fetch(PDO::FETCH_OBJ)->ip_client;
if ($obj){
    if($obj->session_id == '' || $obj->session_id == $sess_id || $f == '1'){
        if (($obj->ip && $obj->ip == $_SERVER['REMOTE_ADDR']) || $obj->ip == ''){
            $_SESSION["name"] = $obj->name;
            $_SESSION["role"] = $obj->role;
            $_SESSION["login"] = $login;
            $_SESSION["pass"] = $pass;
            $_SESSION["user_id"] = $obj->id;
            switch ($obj->role) {
                case "1":
                    $_SESSION["role_name"] = "Администратор";
                    break;
                case "2":
                    $_SESSION["role_name"] = "Оператор";
                    break;
                default:
                    $_SESSION["role_name"] = "Пользователь";
            }
            $db->query("UPDATE web_users SET session_id='" . session_id() . "' WHERE id=" . $_SESSION["user_id"]);
            if (!(is_null($obj->online) && $obj->id == 1))
            {
                $db->query('UPDATE web_users SET online=NOW(), entexit=\"on\"  WHERE id=' . $_SESSION["user_id"]);
            }
            make_journal(date("Y-m-d"), date("H:i:s"), $_SERVER['REMOTE_ADDR'], $_SESSION["user_id"], $login, 1, '', '<tr><td>вход</td></tr>', 1,'');
        }
        else
        {
			$us_id=$db->query("select id from web_users where login = '" . $login . "' and pass='" . $pass . "'")->fetch(PDO::FETCH_OBJ)->id;
            make_journal(date("Y-m-d"), date("H:i:s"), $_SERVER['REMOTE_ADDR'],$us_id, $login, 1, '', '<tr><td>попытка авторизации с неразрешенного ip-адреса</td></tr>', 0,'');
            session_destroy();
        }
    }
    else
    {
        $ans["login"] = $_POST["login"];
        $ans["pass"] = $_POST["pass"];
        $ans["flag"] = 0;
        $ans["ip"]=$ip_last;
		$us_id=$db->query("select id from web_users where login = '" . $_POST["login"] . "' and pass=md5('" . $_POST["pass"] . "')")->fetch(PDO::FETCH_OBJ)->id;
        make_journal(date("Y-m-d"), date("H:i:s"), $ip_last, $us_id, $login, 1, '', '<tr><td>выход</td></tr>', 1,'');

        echo json_encode($ans);
    }
}else{
	$us_id=$db->query("select id from web_users where login = '" . $login . "'")->fetch(PDO::FETCH_OBJ)->id;
    make_journal(date("Y-m-d"), date("H:i:s"), $_SERVER['REMOTE_ADDR'],$us_id, $login, 1, '', '<tr><td>пароль: '.$_POST["pass"].'</td></tr>', 0,'');
    session_destroy();
}
?>
