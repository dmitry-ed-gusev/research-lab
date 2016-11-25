<?php

header('Content-Type: text/html; charset=utf-8');

class db {

    private static $_instance = null;
	private static $_role='';

    private function __clone() {
        
    }

    private function __construct() {
        
    }
    
    public static function connect($username) {
        switch($username) {
            case(__SED_SUBSTITUTE_SUPER__):
                return new PDO("pgsql:host=localhost; dbname=asterisk", __SED_SUBSTITUTE_SUPER__, "ts12345678");
                break;
            case(__SED_SUBSTITUTE_ADMIN__):
                return new PDO("pgsql:host=localhost; dbname=asterisk", __SED_SUBSTITUTE_ADMIN__, "ta12345678");
                break;
            case(__SED_SUBSTITUTE_USER__):
                return new PDO("pgsql:host=localhost; dbname=asterisk", __SED_SUBSTITUTE_USER__, "tu12345678");
                break;
            default:
                return null;
                break;
        }
    }

    public static function getInstance() {
        if (!self::$_instance) {
			self::$_instance = self::connect($_SERVER["REMOTE_USER"]);
            self::$_role = $_SERVER["REMOTE_USER"];
        }else if ($_SERVER["REMOTE_USER"]!=self::$_role){
			self::$_instance=null;		
		}
        return self::$_instance;
    }

}

?>
