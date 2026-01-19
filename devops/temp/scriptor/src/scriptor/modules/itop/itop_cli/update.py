"""
Utility to update objects.
"""
from .action import Action


class Update(Action):
    """
    Utility to update objects.
    """

    def __init__(self, class_name, search, **kwargs):
        super().__init__(class_name, **kwargs)
        self.key = search.split("=", 1)[0]
        self.key_value = search.split("=", 1)[1]

    def update(self):
        """
        Execute the creation.
        :return: None
        """
        try:
            response = self.itop.update(self.class_name, self.key, self.key_value, **self.fields)
            if response['code'] == 0 and response['message'] is None:
                print("Updated object {}".format(list(response['objects'].keys())[0]))
            else:
                raise RuntimeError("Error updating {} : {}".format(self.class_name, response['message']))
        except IOError as exception:
            raise RuntimeError(str(exception))


def update(class_name, search, **kwargs):
    """
    Updates an object.
    :param class_name: class of the object to update
    :param search: simple search query in key=value format
    :param fields: content of the object
    :return:
    """
    Update(class_name, search, **kwargs).update()
