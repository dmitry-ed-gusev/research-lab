#!/usr/bin/python
#  -*- coding: utf-8 -*-
__author__ = 'gusevd'

import hashlib
import pprint
import argparse
import os
import shutil


# noinspection PyPep8Naming
def generateMd5CheckSum(path):
    """ Method generates MD5 hash and returns tuple {"path":<path>, "checksum":<sum>} """
    with open(path.strip(), 'r') as file:
        return {"path": path.strip(), "checksum": hashlib.md5(file.read()).hexdigest()}


def createBackupCopy(backupPath, pathToFile):
    fullPath = os.path.normpath(backupPath)+"/"+os.path.normpath(pathToFile)
    createPathIfNotExist(os.path.dirname(fullPath))
    os.system("cp -p "+pathToFile+" "+fullPath)
    #shutil.copy2(pathToFile, fullPath)

def createBackupCopys(path, list):
    for file in list:
        createBackupCopy(path, file["path"])

def createPathIfNotExist(path):
    if not os.path.exists(path):
        os.makedirs(path)

def getFilesPathsAndChecksumsFromFile(filename):
    with open(filename, 'r') as file:
        return map(generateMd5CheckSum, file)


def writeChecksumsInFile(path, checksumsList):
    with open(path, 'w+') as file:
        print >> file, "#!/usr/bin/python\n#  -*- coding: utf-8 -*-\nlist=\\"
        pprint.pprint(checksumsList, file)


def generateFile(args):
    filesList = getFilesPathsAndChecksumsFromFile(args.listFile)
    writeChecksumsInFile(args.checkSumFile, filesList)
    createBackupCopys(args.backupPath, filesList)



def main():
    parser = argparse.ArgumentParser(
        description=u'Генерирует список файлов с контрольными суммами для дальнейшего контроля')
    parser.add_argument('-l', '--lpath', dest='listFile', required=True, action='store',
                        help=u'Путь к файлу, хранящему список путей файлов для контроля целостности')
    parser.add_argument('-c', '--cpath', dest='checkSumFile', required=True, action='store',
                        help=u'Путь к файлу, в который будет сохранен список файлов с контрольными суммами')
    parser.add_argument('-b', '--bpath', dest='backupPath', required=True, action='store',
                        help=u'Путь, относительно которого будут сохраняться резервные копии файлов')
    args = parser.parse_args()
    generateFile(args)

# if started as standalone module - call function main()
if __name__ == "__main__":
    main()