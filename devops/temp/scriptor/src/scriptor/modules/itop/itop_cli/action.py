"""
Action on iTop Object
"""
from .connection import ItopConnection


class Action:
    """
    Action on iTop Object
    """

    def __init__(self, class_name, **kwargs):
        self.itop = ItopConnection.connection()
        self.class_name = class_name
        if 'fields' in kwargs:
            self.fields = {item.split("=", 1)[0]: item.split("=", 1)[1] for item in kwargs['fields']}
        self.query = None
        if 'query' in kwargs:
            self.query = kwargs['query']

    def oql(self):
        """
        OQL query from original query or list of filters.
        :return: string
        """
        if self.query is not None:
            return self.query
        query = "SELECT {}".format(self.class_name)
        if self.fields:
            filters = []
            for key, value in self.fields.items():
                if "%" in value:
                    filters.append("{} LIKE {}".format(key, value))
                else:
                    filters.append("{}={}".format(key, value))
            query += " WHERE " + " AND ".join(filters)
        self.query = query
        return query
