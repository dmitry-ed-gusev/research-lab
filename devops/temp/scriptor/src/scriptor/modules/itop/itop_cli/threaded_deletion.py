"""
Deletes object with threads
"""
from threading import Thread, Lock
from uuid import uuid4

from itop_cli.safe_delete import SafeDelete
from .connection import ItopConnection

from .find import find
from .action import Action

class ThreadDelete(Thread):
    """
    Deletes object with threads
    """

    lock = Lock()
    cursors = {}

    def __init__(self, class_name, elements, operation_uuid):
        Thread.__init__(self)
        self.itop = ItopConnection.connection()
        self.class_name = class_name
        self.instance = SafeDelete(class_name)
        self.operation_uuid = operation_uuid
        self.elements = elements
        self.uuid = uuid4()

    def run(self):
        ThreadDelete.lock.acquire()
        if self.operation_uuid not in ThreadDelete.cursors:
            ThreadDelete.cursors[self.operation_uuid] = 0
        i = ThreadDelete.cursors[self.operation_uuid]
        ThreadDelete.cursors[self.operation_uuid] += 1
        ThreadDelete.lock.release()
        items = self.elements
        while i < len(items):
            object_ = items[i][1]
            self.instance.delete(object_)
            ThreadDelete.lock.acquire()
            ThreadDelete.cursors[self.operation_uuid] += 1
            i = ThreadDelete.cursors[self.operation_uuid]
            ThreadDelete.lock.release()


class ThreadedDeletion(Action):
    """
    Utility to launch deletetions with threads
    """
    nb_threads = 4

    def __init__(self, class_name, **kwargs):
        super().__init__(class_name, **kwargs)
        query = self.oql()
        self.instance = SafeDelete(class_name, query)
        self.uuid = uuid4()
        self.query = query
        self.threads = []

    def run(self):
        """
        Launches threads
        :return: None
        """
        elements = find(self.class_name, self.oql())
        if elements is None:
            raise RuntimeError("Nothing to do !")
        for _ in range(0, ThreadedDeletion.nb_threads):
            self.threads.append(ThreadDelete(self.class_name, elements, self.uuid))
        for thread in self.threads:
            thread.start()
        for thread in self.threads:
            thread.join()
        SafeDelete.elements = []


def delete(class_name, **kwargs):
    """
    Deletes objects
    :param class_name: class of the object(s) to delete
    :param query: objects filter
    :return: None
    """
    ThreadedDeletion(class_name, **kwargs).run()
