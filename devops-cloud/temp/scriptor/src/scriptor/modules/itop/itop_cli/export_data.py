"""
Utility to export objects.
"""
from .action import Action

class Export(Action):
    """
    Utility to export objects.
    """

    def __init__(self, class_name, **kwargs):
        super().__init__(class_name, **kwargs)
        self.output_fields = "*"
        if "output_fields" in kwargs:
            self.output_fields = kwargs['output_fields']

    def get(self):
        """
        Gets the objects data
        :return: JSON data as string
        """
        try:
            data = self.itop.get(self.class_name, self.oql(), self.output_fields)
            if data['code'] != 0:
                print(data['message'])
                return None
            found = data['message'].split(" ")[1]
            if found == "0":
                print("No object found for {}".format(self.query))
                return None
            return list(data["objects"].values())
        except IOError as exception:
            exit(str(exception))


def export_data(class_name, **kwargs):
    """
    Exports data
    :param class_name: class of the objects to export
    :param query: OQL query
    :param output_fields: list of fields to display
    :return: objects found; if no query provided, it will return all objects of the class
    """
    return Export(class_name, **kwargs).get()
