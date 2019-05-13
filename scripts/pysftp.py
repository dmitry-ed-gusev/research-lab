#!/usr/bin/env python
# coding=utf-8

"""

    Some useful/convenient functions related to SFTP working.

    Created:  Dmitrii Gusev, 25.04.2019
    Modified: Dmitrii Gusev, 13.05.2019

"""

# todo: https://habr.com/ru/company/jetinfosystems/blog/336608/
# todo: https://github.com/ktbyers/netmiko

import ftplib
from pyutilities.pylog import init_logger, setup_logging
from ftplib import FTP, FTP_TLS

# init module logger
# log = init_logger(__name__)


def ftp_list_files(ftp_object, dir):
    log.info('Trying to get list of files for dir [{}].'.format(dir))
    pass


def ftp_download(ftp_object, file):
    log.info('Downloading file [{}] from FTP server.'.format(file))
    pass


def ftp_upload(ftp_obj, path, ftype='TXT'):
    """
    Функция для загрузки файлов на FTP-сервер
    @param ftp_obj: Объект протокола передачи файлов
    @param path: Путь к файлу для загрузки
    """
    log.info('Uploading file to FTP server...')
    if ftype == 'TXT':
        with open(path) as fobj:
            ftp.storlines('STOR ' + path, fobj)
    else:
        with open(path, 'rb') as fobj:
            ftp.storbinary('STOR ' + path, fobj, 1024)


if __name__ == '__main__':
    print("!!!")
    log = setup_logging(logger_name='sftp_client')
    log.info("Starting FTP client...")

    # ftp = FTP(host="92.53.96.211")
    # ftp.connect(host="92.53.96.211", port=21)
    # ftp_login_response = ftp.login(user="92.53.96.211", passwd="vEbmw7mT")
    # log.info('FTP server response:\n{}'.format(ftp_login_response))
    # ftp.quit()

    ftp = FTP_TLS("92.53.96.211")
    ftp.set_debuglevel(2)
    print(ftp.sendcmd('USER myusername'))  # '331 Please specify the password.'
    ftp.sendcmd('PASS mypassword')

    #ftp.set_debuglevel(2)
    #ftp.connect("92.53.96.211", 21)
    #ftp.login("cp23965_pult", "vEbmw7mT")
    #ftp.dir()
    #ftp.close()


# ====
#ftp = FTP(HOST)
#ftp.login()
#ftp.cwd('debian')
# Путь на нашем компьютере где сохранить файл.
#out = 'C:\\files\\README.html'
#with open(out, 'wb') as f:
#    ftp.retrbinary('RETR ' + 'README.html', f.write)

# ====
# ftp.cwd('debian')
# filenames = ftp.nlst()
#
# for filename in filenames:
#     host_file = os.path.join(
#         'C:\\files\\ftp_test', filename
#     )
#
#     try:
#         with open(host_file, 'wb') as local_file:
#             ftp.retrbinary('RETR ' + filename, local_file.write)
#     except ftplib.error_perm:
#         pass
#
# ftp.quit()

# ====
# ftp = ftplib.FTP('host', 'username', 'password')
# ftp.login()
#
# path = '/path/to/something.txt'
# ftp_upload(ftp, path)
#
# pdf_path = '/path/to/something.pdf'
# ftp_upload(ftp, pdf_path, ftype='PDF')
#
# ftp.quit()
