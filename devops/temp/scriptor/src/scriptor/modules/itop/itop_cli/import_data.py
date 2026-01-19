"""
Utility to import objects.
"""
from json import load
from json.decoder import JSONDecodeError

from .connection import ItopConnection


class Import:
    """
    Utility to import objects.
    """

    def __init__(self, class_name, data, search_keys="name", **kwargs):
        self.itop = ItopConnection.connection()
        self.class_name = class_name
        try:
            self.elements = load(data)
        except IOError as exception:
            exit(str(exception))
        except JSONDecodeError as exception:
            exit("Error parsing JSON data : {}".format(str(exception)))
        self.search_keys = search_keys.split(",")
        self.to_create = []
        self.to_update = []
        self.bypass = kwargs['bypass']

    def import_data(self):
        """
        Imports data. Will first simulate the ask if it should be executed
        :return: None
        """
        self.simulate_()
        nb_to_create = len(self.to_create)
        nb_to_update = len(self.to_update)
        if nb_to_create >= 1 or nb_to_update >= 1:
            if not self.bypass:
                print("Will create {} object(s) and update {} object(s)".format(nb_to_create, nb_to_update))
                answer = ""
                while answer not in ('y', 'n'):
                    answer = input("Do you want to continue ? (y/n): ")
                if answer == "y":
                    self.do_()
                else:
                    print("Nothing to do !")
            else:
                self.do_()
        else:
            print("Nothing to do !")

    def do_(self):
        """
        Executes import
        :return:  None
        """
        for element in self.to_update:
            response = self.itop.update(self.class_name, "key", element['key'], **Import.fields_(element))
            if response['code'] == 0:
                print("Updated {}".format(self.filter_(element)))
            else:
                print("Wasn't able to update {} because of : ".format(response['message']))
        for element in self.to_create:
            response = self.itop.create(self.class_name, **Import.fields_(element))
            if response['code'] == 0:
                print("Created {}".format(self.filter_(element)))
            else:
                print("Wasn't able to create {} because of : ".format(response['message']))

    def simulate_(self):
        """
        Simulates the import
        :return: None
        """
        for element in self.elements:
            objects = self.get_(element)
            if objects is not None:
                if len(objects) == 1:
                    element['key'] = objects[0]['key']
                    self.to_update.append(element)
                    print("Will try to update {}".format(self.filter_(element)))
                if len(objects) > 1:
                    print(
                        "Found {nb} element(s) for {filter} - Won't do anything, too ambiguous".format(
                            nb=len(objects),
                            filter=self.filter_(
                                element)))
            else:
                self.to_create.append(element)
                print("No element found for {}".format(self.filter_(element)))

    def get_(self, element):
        """
        Searchs object
        :param element: object to seach
        :return: object if found
        """
        query = "SELECT {} WHERE {}".format(self.class_name, self.filter_(element))
        response = self.itop.get(self.class_name, query)
        found = response['message'].split(" ")[1]
        if found == "0":
            return None
        return list(response["objects"].values())

    @staticmethod
    def fields_(elements):
        """
        Split fields
        :param elements: list of fields
        :return: dictionary of fields
        """
        fields = {}
        for key, value in elements['fields'].items():
            if not isinstance(value, list) and value is not None:
                if isinstance(value, str):
                    if value.strip() != "":
                        fields[key] = value
                else:
                    fields[key] = value
        return fields

    def filter_(self, element):
        """
        Joins fields to create a filter
        :param element: object
        :return: iTop OQL filter
        """
        filters = []
        for key in self.search_keys:
            try:
                filters.append("{key}=\"{value}\"".format(key=key, value=element['fields'][key]))
            except KeyError:
                exit("Unknown key {}".format(key))
        filter_ = " AND ".join(filters)
        return filter_


def import_data(class_name, data, search_keys="name", **kwargs):
    """
    Imports data
    :param class_name: class of objects to import
    :param data: objects content
    :param search_keys: keys to find existing objects (prevents duplication); "name" will be used by default
    :return: None
    """
    Import(class_name, data, search_keys, **kwargs).import_data()
