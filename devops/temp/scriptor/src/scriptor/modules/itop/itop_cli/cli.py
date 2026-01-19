#!/usr/bin/env python
"""
  Usage:
    itop delete <class> (--query=<query>|[FIELDS]...) (--env=<env>|--config=<config>)
    itop export <class> (--query=<query>|[FIELDS]...) [--output_fields=<output_fields>] (--env=<env>|--config=<config>) [--pretty]
    itop import <class> --input=<input_file> [--search_keys=<search_keys>] [--yes] (--env=<env>|--config=<config>)
    itop create <class> [FIELDS]... (--env=<env>|--config=<config>)
    itop update <class> <search> [FIELDS]... (--env=<env>|--config=<config>)
    itop -h | --help | --version
    {usage}

  Arguments:
    FIELDS                         Key value pairs. Ex : "description=Ceci est une description". If not overridden, the script will use the org_id of the config file
    <query>                        OQL query.
    <search>                       Simple search element. "key=value" format
    {arguments}

  Options:
    -e <env> --env=<env>                                    Will search ~/.itop/<venv>.json as configuration file
    -c <config> --config=<config>                           Path to config file
    -s <search_keys> --search_keys=<search_keys>            Key(s) to search objects, comma separated [default: name]
    -f <output_fields> --output_fields=<output_fields>      Filed(s) to export, comma separated
    -i <input_file> --input=<input_file>                    File to use for data input
    -p --pretty                                             Prettify JSON output
    -q <query> --query=<query>                              OQL query
    -y --yes                                                Will apply all changes automatically (use with care)
    {options}

  Examples:
    itop delete Person -q "SELECT Person WHERE status='inactive'" --env=dev
    itop delete Person 'status="inactive"' --env=dev
    itop export SynchroDataSource --env=dev
    itop export Server -q "SELECT Server WHERE name LIKE 'SRVTEST'" --env=dev -f name
    itop export Server "name='SRVTEST'" --env=dev -f name
    itop import SynchroDataSource --input=/tmp/out.json --search_keys=database_table_name
    itop create Server "name=SRVTEST" "description=Serveur de test" --env=dev
    itop update Server "name=SRVTEST" "description=Serveur de prod" --env=dev
    itop update Server "name=SRVTEST" "description=Serveur de prod" "brand_id=SELECT Brand WHERE name='Altiris'" --env=dev
    {examples}
"""

from json import load, dumps
from os.path import join, expanduser

from docopt import docopt

from . import import_data, export_data, delete, create, update
from .extension import Extension
from itop_cli.connection import ItopConnection


class Itop:
    """
    Main class
    """

    def __init__(self, extension=None) -> None:
        super().__init__()
        self.extension = extension
        self.conf = None
        self.c_arguments = None
        self.main_org_id = None
        self.search_keys = extension.search_keys

    def org_id(self, org_name):
        """
        Search the id of an organization
        :param org_name: name of the organization to search
        :return:
        """
        response = ItopConnection.connection().get('Organization', 'SELECT Organization WHERE name = "%s"' % org_name)
        if "code" not in response:
            raise BaseException(response)
        if response['code'] != 0 and response['message'] != 'Found: 1':
            exit("Organization '{}' not found".format(org_name))
        code = list(response['objects'].values())[0]['key']
        return code

    def check_args(self):
        """
        Sanity check of arguments and configuration
        :return: None
        """
        if self.c_arguments["--config"] is not None:
            conf_path = expanduser(self.c_arguments["--config"])

        if self.c_arguments["--env"] is not None:
            conf_path = join(expanduser('~'), ".itop", self.c_arguments["--env"] + ".json")

        try:
            self.conf = load(open(conf_path, "r"))
        except IOError as exception:
            exit(str(exception))

        conf = {"url", "version", "user", "password", "org_name"}
        if not all(item in self.conf.keys() for item in conf):
            exit("Wrong config file format")

    def main(self):
        """
        Main function
        :return: None
        """
        self.c_arguments = docopt(
            __doc__.format(usage=self.extension.usage, arguments=self.extension.arguments,
                           options=self.extension.options, examples=self.extension.examples),
            version='2.0.1')

        self.check_args()

        ItopConnection.set_connection_params(self.search_keys, self.conf["url"], self.conf["version"],
                                             self.conf["user"], self.conf["password"])
        # Connection test. This value will also be used for creations
        try:
            self.main_org_id = self.org_id(self.conf["org_name"])
            # pass
        except BaseException as exception:
            exit(str(exception))

        # try:
        if self.c_arguments["delete"]:
            delete(self.c_arguments["<class>"], fields=self.c_arguments["FIELDS"],
                   query=self.c_arguments["--query"])

        if self.c_arguments["export"]:
            export = export_data(self.c_arguments["<class>"], fields=self.c_arguments["FIELDS"],
                                 query=self.c_arguments["--query"],
                                 output_fields=self.c_arguments["--output_fields"])
            if self.c_arguments["--pretty"]:
                print(dumps(export, sort_keys=True, indent=4, separators=(',', ': ')))
            else:
                print(dumps(export))

        if self.c_arguments["import"]:
            data = open(self.c_arguments["--input"], "r")
            import_data(self.c_arguments["<class>"],
                        data,
                        self.c_arguments["--search_keys"],
                        bypass=self.c_arguments["--yes"])

        if self.c_arguments["create"]:
            fields = self.c_arguments["FIELDS"]
            if "org_id" not in fields:
                fields.append("org_id=%s" % self.main_org_id)
            create(self.c_arguments["<class>"], fields=self.c_arguments["FIELDS"])

        if self.c_arguments["update"]:
            fields = self.c_arguments["FIELDS"]
            update(self.c_arguments["<class>"], self.c_arguments["<search>"],
                   fields=self.c_arguments["FIELDS"])

        if self.extension is not None:
            self.extension(itop=self)

        # except Exception as exception:
        #    exit("{} : {}".format(type(exception), str(exception)))


def main_itop(extension=Extension()):
    """
    Launches main util
    :param extension: Extension instance
    :return: None
    """
    Itop(extension).main()
