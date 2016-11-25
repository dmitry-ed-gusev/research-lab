__author__ = 'alex'
import httplib

class HTTPProvider(object):
    def __init__(self, host, port):
        self.host = host
        self.port = port

    def get(self, path, headers={}):
        self.connection = httplib.HTTPConnection(self.host+":"+str(self.port))
        req = self.connection.request("GET", path, headers=headers)
        response = self.connection.getresponse()
        return response

    def post(self, path, params=None, headers={}):
        self.connection = httplib.HTTPConnection(self.host+":"+str(self.port))
        self.connection.request("POST", path, params, headers)
        return self.connection.getresponse()
