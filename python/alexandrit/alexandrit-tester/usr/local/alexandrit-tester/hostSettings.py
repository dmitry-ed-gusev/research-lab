__author__ = 'alex'
from subprocess import check_output

HOST = "localhost"
PORT = 80

def getHost():
    return HOST

def getPort():
    return PORT;

def getPortFromFile():
    port = check_output("cat /etc/apache2/ports.conf | awk '{ if($1 == \"Listen\") {print $2} }' ", shell=True)
    return int(port)
def setPort():
    global PORT
    PORT = getPortFromFile()

setPort()