# iTop CLI

Utility to interact with iTop

## Features

    Usage:
      itop.py delete <class> <query> (--env=<env>|--config=<config>)
      itop.py export <class> [<query>] (--env=<env>|--config=<config>)
      itop.py import <class> --input=<input_file> [--search_keys=<search_keys>] (--env=<env>|--config=<config>)
      itop.py create <class> [FIELDS]... (--env=<env>|--config=<config>)
      itop.py -h | --help | --version
  
    Arguments:
      FIELDS                         Key value pairs. Ex : "description=Ceci est une description". If not overridden, the script will use the org_id of the config file
  
    Options:
      --env=<env>                    Will search ~/.itop/<venv>.json as configuration file
      --search_keys=<search_keys>    Key(s) to search objects, comma separated [default: name]
  
    Examples:
      itop.py delete Person 'SELECT Person WHERE status="inactive"' --env=dev
      itop.py export SynchroDataSource --env=dev
      itop.py export Server "SELECT Server WHERE name LIKE 'SRVTEST'" --env=dev
      itop.py import SynchroDataSource --input=/tmp/out.json --search_keys=database_table_name
      itop.py create Server "name=SRVTEST" "description=Serveur de test" --env=dev

## Configuration file

    {
      "url": "http://myhost.example.com/itop_dev/webservices/rest.php",
      "version": "1.3",
      "user": "user",
      "password": "password",
      "org_name": "My org"
    }
