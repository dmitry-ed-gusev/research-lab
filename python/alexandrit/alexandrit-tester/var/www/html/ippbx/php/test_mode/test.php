<?php

include '../lib.php';

session_start();
header('Content-Type: text/html; charset=utf-8');

function __autoload($name) {
    include '../' . $name . '.class.php';
}

switch($_SERVER['REQUEST_METHOD'])
{
    case 'GET':
        $cur_script_role = 3;
        $action = 1;
        break;
    case 'POST':
        {
            switch($_POST['action'])
            {
                case 'set':
                    $cur_script_role = 2;
                    $action = 2;
                    break;
                case 'remove':
                    $cur_script_role = 1;
                    $action = 3;
                    break;
                default:
                    http_response_code(500);
                    die("Bad action");
                    break;
            }
        }
        break;
    default:
        http_response_code(500);
        die("Bad request");
        break;
}

$db = db::getInstance();
$user_data = $db->query("select session_id, role from web_users where login='" . $_SESSION["login"] . "'")->fetch(PDO::FETCH_OBJ);
$ses_id = $user_data->session_id;
$user_role = $user_data->role;
if($cur_script_role < $user_role)
{
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
    http_response_code(500);
    die("Access violation");
}
else
{
    if($ses_id != session_id())
    {
       http_response_code(500);
       die("Bad session");
    }
}
$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
try
{
    switch($action)
    {
        case 1:
            {
                $query_result = $db->query("SELECT var_name,var_value FROM test_tbl");
                echo "<html>\n";
                echo "<head>\n";
                echo "\t<title>\n";
                echo "\t\tTEST\n";
                echo "\t</title>\n";
                echo "</head>\n";
                echo "<body>\n";
                while(($result_row = $query_result->fetch(PDO::FETCH_OBJ)))
                {
                    echo sprintf("%s\t=\t%s\n", $result_row->var_name, $result_row->var_value);
                }
                echo "</body>\n";
                echo "</html>\n";
            }
            break;
        case 2:
            $db->beginTransaction();
            $var_name = $_POST['var_name'];
            $var_value = $_POST['var_value'];
            $sql = "INSERT INTO test_tbl(var_name, var_value) VALUES ('$var_name', '$var_value')";
            $query_result = $db->query($sql);
            $db->commit();
            $arr = array();
            $arr['result'] = 1;
            $arr['row_count'] = $query_result->rowCount();
            echo json_encode($arr);
            break;
        case 3:
            $db->beginTransaction();
            $var_name = $_POST['var_name'];
            $sql = "DELETE FROM test_tbl WHERE var_name = '$var_name'";
            $query_result = $db->query($sql);
            $db->commit();
            $arr = array();
            $arr['result'] = 1;
            $arr['row_count'] = $query_result->rowCount();
            echo json_encode($arr);
            break;
        default:
            break;
    }
}
catch(PDOException $e)
{
    if($db->inTransaction())
    {
        $db->rollBack();
    }
    if($e->getCode() == "42501")
    {
        http_response_code(500);
        die("Access denied");
    }
    else
    {
        //$s = var_dump($e);
        //echo "$s\n";
        http_response_code(500);
        die("Exception");
    }
}
?>