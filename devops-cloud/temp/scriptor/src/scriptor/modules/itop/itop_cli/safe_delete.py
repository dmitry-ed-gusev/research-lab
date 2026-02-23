"""
Utility to delete objects. Will perform some tests.
"""
from .action import Action


class SafeDelete(Action):
    """
    Utility to delete objects. Will perform some tests.
    """

    def __init__(self, class_name, synchro_replica=True):
        super().__init__(class_name)
        self.synchro_replica = synchro_replica

    def delete(self, element):
        """
        Deletes an object
        :param element: object to delete
        :return: None
        """
        key = element['key']
        if self.synchro_replica:
            query = ('SELECT SynchroReplica WHERE dest_id={key}'
                     ' AND dest_class="{class_name}"').format(key=key, class_name=self.class_name)
            response = self.itop.delete('SynchroReplica', key=query)
            if response['code'] > 0:
                print("Warning : {}".format(response['message']))
        simulation = self.itop.delete(self.class_name, simulate=True, key=element['key'])
        if simulation['code'] == 0 and simulation['message'] == "SIMULATING: Deleted: 1":
            response = self.itop.delete(self.class_name, key=element['key'])
            print("Deleted {name} ({key}) / Response : {message}".format(name=element['fields']['friendlyname'],
                                                                         key=element['key'],
                                                                         message=response['message']))
        else:
            print("Wont't delete {name} ({key}) because of {message}".format(name=element['fields']['friendlyname'],
                                                                             key=element['key'],
                                                                             message=simulation['message']))
