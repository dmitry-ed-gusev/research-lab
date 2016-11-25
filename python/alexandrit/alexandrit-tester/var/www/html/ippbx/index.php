<?php
include 'php/lib.php';
session_start();

$db = db::getInstance();
if (!isset($_SESSION['user_id']) && $_SERVER['REQUEST_URI'] != "/index.php") {
    header("Location: /index.php");
}
if (isset($_SESSION['user_id'])) {
    $sess_id = $db->query('SELECT session_id from web_users where id=' . $_SESSION["user_id"])->fetch(PDO::FETCH_OBJ)->session_id;

    if ($sess_id != session_id() && $_SERVER['REQUEST_URI'] != "/index.php") {
        session_destroy();
        header("Location: /index.php");
    }
}
header('Content-Type: text/html; charset=utf-8');
function __autoload($name) {
    include 'php/' . $name . '.class.php';
}
if (isset($_GET["di"])) {
    $con2 = $_GET["di"];
    $con2 = (int) trim((stripslashes(strip_tags($con2))));
}
//echo $_GET["id"];
if (isset($_GET["id"])) {
    $con = $_GET["id"];
    $con = (int) trim((stripslashes(strip_tags($con))));
    if ($con == 0) {
        header("Location: ?id=55");
$login=$db->query('SELECT login FROM web_users WHERE id='.$_SESSION["user_id"])->fetch(PDO::FETCH_OBJ)->login;
make_journal(date("Y-m-d"), date("H:i:s"), $_SERVER['REMOTE_ADDR'], $_SESSION["user_id"], $login, 1, '', '', 1,'');
    }
} else {
    $con = 0;
}
if (isset($_GET["di"]) && isset($_GET["id"])) {

    $cntMenu = $db->query('SELECT count(id) as c FROM web_menu WHERE id=' . $con2 . ' AND uroven=' . $con . ' AND role >=' . $_SESSION["role"])->fetch(PDO::FETCH_OBJ)->c;
    if ($cntMenu != 1) {
        header("Location: /index.php");
    }
}

?>	

<!DOCTYPE HTML>
<html>
    <head>
        <meta charset="utf-8">
        <meta name="SKYPE_TOOLBAR" content="SKYPE_TOOLBAR_PARSER_COMPATIBLE" />
        <title>Административная панель IP-ATC</title>
        <script src="js/jq.js" type="text/javascript" ></script>
        <script src="js/jq_ui.js" type="text/javascript" ></script>
        <script src="js/jdock.js" type="text/javascript" ></script>
        <script src="js/my_style.js" type="text/javascript" ></script>
        <script src="js/mousewheel.js" type="text/javascript" ></script>
        <script src="js/scroll.js" type="text/javascript" ></script>
        <script type="text/javascript" src="js/jquery.form.js"></script>
        <script type="text/javascript" src="js/dimaJS.js"></script>
        <script type="text/javascript" src="js/jquery.treeview.js" ></script>

        <script type="text/javascript" src="/js/us.js"></script>


        <script src="/js/jquery.inputmask.js" type="text/javascript"></script>
        <script src="/js/jquery.inputmask.extensions.js" type="text/javascript"></script>
        <script src="/js/jquery.inputmask.date.extensions.js" type="text/javascript"></script>
        <script src="/js/jquery.inputmask.numeric.extensions.js" type="text/javascript"></script>
        <!--<script src="/js/jquery.inputmask.custom.extensions.js" type="text/javascript"></script>-->
        <script type="text/javascript" src="/js/chosen.jquery.js"></script>
        <script type="text/javascript" src="/js/chosen.proto.js"></script>
        <script type="text/javascript" src="/js/antares.js"></script>
        <script type="text/javascript" src="/js/show_event_temp.js"></script>
        <script type="text/javascript" src="/js/mod_cid.js"></script>
        <script type="text/javascript" src="/js/update.js"></script>
        <script src="/js/underscore.js" type="text/javascript"></script>
        <script type="text/javascript" src="/js/jquery.poshytip.js"></script>
        <script type="text/javascript" src="js/journal_event.js"></script>
        
        <link rel="stylesheet" type="text/css" href="/css/chosen.css">

        <link rel="stylesheet" href="/css/treeview.css" type="text/css" media="screen, projection" />
        <link rel="stylesheet" href="css/jq_ui.css" type="text/css" media="screen, projection" />
        <link rel="stylesheet" href="css/style.css" type="text/css" media="screen, projection" />
        <link rel="stylesheet" href="css/scroll.css" type="text/css" media="screen, projection" />
        <link rel="stylesheet" href="css/tip-yellowsimple.css" type="text/css" media="screen, projection" />
        <link rel="stylesheet" href="css/tip-darkgray.css" type="text/css" media="screen, projection" />
    </head>
    <body>
        <div id="iconanadsys"></div>
        <div id="wrapper">
            <div id="header">
                <div id="logo"><?php
/* $han = fopen("version", "r");
  echo fgets($han);
  fclose($han); */
if(!exec("stat /var/run/test_mode_running")){
if($_SESSION["role"]=='1'){
echo "<a href='#' id='test_mode'></a>";
}

}
/*
$sql = 'SELECT cur_ver as ver FROM general_config ';
$cur_ver = $db->query($sql)->fetch(PDO::FETCH_OBJ)->ver;
echo $cur_ver;
*/

$f = fopen("/var/www/html/ippbx/files/checksum", "r");
$check_sum = fgets($f);
fclose($f);
if($check_sum)
{
    echo "<br>$check_sum";
}
?></div>
                <div id="login">
                    <?php if(!exec("stat /var/run/test_mode_running")){ include'php/login_top.php';} ?>
                </div>
            </div>
            <div id="menu">
                <?php if(!exec("stat /var/run/test_mode_running")){if (isset($_SESSION["role"])) top_menu($_SESSION["role"], $con); }?>
            </div>
            <div>
                <!--    <div style="width:1000px; height:570px; background-image:url(img/fon_shadow.png);">
                -->        <div id="content">
                    <?php
if(exec("stat /var/run/test_mode_running")){
echo '<img src="img/loading51.gif" style="margin-top:150px;margin-left:420px;" />';
echo '<div style="margin-left:200px;margin-right:200px;margin-top:50px;text-align:center;"><p style="margin:0px;margin-top:5px;font-size:11pt;">АТС находится в режиме тестирования.</p>
<p style="margin:0px;margin-top:5px;font-size:11pt;">Подождите несколько секунд.</p>
<p style="margin:0px;margin-top:5px;font-size:11pt;">По окончании тестирования в журнале событий появится запись о результатах.</p></div>';
exit();

}

  
                  if ((isset($_SESSION["role"])) && (isset($con2))) {
                        include content($_SESSION["role"], $con2);
                    } else {
                        ?><img src="img/aleksandrit.jpg" width="993px" height="563px" style="margin-top:-10px;-moz-border-radius-topright: 10px;-webkit-border-top-right-radius: 10px;border-top-right-radius: 10px;-moz-border-radius-bottomright: 10px;-webkit-border-bottom-right-radius: 10px;border-bottom-right-radius: 10px;-moz-border-radius-bottomleft: 5px;-webkit-border-bottom-left-radius: 5px;border-bottom-left-radius: 5px;" /><?php
                }

                if ($con == 0 && isset($_GET["id"])) {
                    include content($_SESSION["role"]);
                }
                    ?>	
                </div>
            </div>
            <div style="width:1000px; height:50px;"></div>   
            <div id="footer" style="text-align:center;">
                <?php
                if (!((empty($_SESSION["login"])) && (empty($_SESSION["pass"])))) {
                    mainmenu($_SESSION["role"]);
                }
                ?>
            </div>
        </div>
<div id='modal_test_mode' style="width:471px;" title="Запуск тестирования системы">
        <table>
            <tr>
                <td colspan="2">
                    <span>Вы уверены, что хотите запустить тестирование АТС?</span>

                </td>
            </tr>
<tr height="14px"><td colspan="2"></td></tr>
            <tr >
                <td style="vertical-align:top;">
                    <span >ВНИМАНИЕ:</span>
                </td>
				<td >
                    <span> В процессе тестирования любому пользователю все операции с WEB интерфейсом будут недоступны, при этом все активные разговоры будут завершены принудительно.
<br> По окончании процесса тестирования в журнале событий появится информация о результате теста.<br> Тестирование займет менее одной минуты.</span>
                </td>

            </tr>
        </table>
        <div style="width:444px;height:42px;">
                    <div id="test_ok" align="center" style="margin-right: 9px; margin-top: 5px;"></div>
                    <div id="test_cancel" align="center" style="margin-top:5px;"></div>
        </div>
    </div>

    </body>
</html>
