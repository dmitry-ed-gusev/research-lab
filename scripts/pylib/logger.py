#!/usr/bin/env python
# coding=utf-8

import argparse
import logging
import os
import sys

from lib.common.configuration import Configuration


class MantisLogger(object):
    """Wrapper for "logging.logger" which allows to set up custom fields and additional settings """

    TRACE = 8
    DATEFORMAT = '%Y-%m-%d %H-%M-%S'
    LOG_BASE_DIR_DEFAULT = "/var/log/mantis"
    FORMAT = "[%(asctime)-11s] - [%(levelname)s] - [%(mdc_pid)s] - [%(source_system)s] - [%(system_location)s] - " \
             "[%(system_env)s] - [%(source_table)s] - [%(step)s] - %(message)s"

    def __init__(self):
        logging.addLevelName(self.TRACE, 'TRACE')
        self.logger = logging.getLogger(__name__)
        self.caption = None

    def configure(self, config):
        """Set up various parameters (source system, env, etc.) fetching them from configuration object
        
            :type config: mantis.lib.configuration.Configuration
        """
        try:
            caption = {'step': config.get("step_name"),
                       'source_system': config.get("source_system"),
                       'system_location': config.get("source_system_location"),
                       'system_env': config.get("source_system_env"),
                       'source_table': config.get("source_table"),
                       'mdc_pid': config.get("mdc_pid")}
        except KeyError as err:
            print("Log configuration failed, extra entry is absent: %s" % err)
            sys.exit(1)

        formatter = logging.Formatter(self.FORMAT, datefmt=self.DATEFORMAT)
        self.logger.setLevel(self.TRACE)
        try:
            log_base_dir = config.get("logging.log_file_location", self.LOG_BASE_DIR_DEFAULT)

            if not os.path.isdir(log_base_dir):
                print("Log files base directory '%s' does not exist" % log_base_dir)
                sys.exit(1)

            log_dir = os.path.join(log_base_dir, '%s-%s-%s' % (config.get("source_system"),
                                                               config.get("source_system_location"),
                                                               config.get("source_system_env")))
            if not os.path.isdir(log_dir):
                os.makedirs(log_dir)

            log_file = os.path.join(log_dir, '%s.log' % config.get("source_table"))
            file_handler = logging.FileHandler(log_file)
            file_handler.setLevel(config.get("logging.file_log_level"))

            console_handler = logging.StreamHandler(stream=sys.stdout)
            console_handler.setLevel(config.get("logging.console_log_level"))

            file_handler.setFormatter(formatter)
            console_handler.setFormatter(formatter)
        except KeyError as err:
            print("Handler configuration failed: %s" % err)
            sys.exit(1)

        self.logger.addHandler(console_handler)
        self.logger.addHandler(file_handler)

        self.caption = caption

    def debug(self, msg):
        """Log the message which should not (need not) be shown in production environment
            
            :type msg: str
        """
        self.logger.debug(msg, extra=self.caption)

    def info(self, msg):
        """Log the message which tells about normal execution flow

            :type msg: str
        """
        self.logger.info(msg, extra=self.caption)

    def warn(self, msg):
        """Log the message about unexpected yet not critical situation

            :type msg: str
        """
        self.logger.warning(msg, extra=self.caption)

    def error(self, msg):
        """Log the message about troubles preventing further normal execution flow

            :type msg: str
        """
        self.logger.error(msg, extra=self.caption)

    def trace(self, msg):
        """Log the message which should not be shown in production environment and may cause excessive logs bloating due
            to enormous size or frequency

            :type msg: str
        """
        self.logger.log(self.TRACE, msg, extra=self.caption)


if __name__ == "__main__":
    #Parsing arguments
    parser = argparse.ArgumentParser()
    parser.add_argument('--level', type=str, default='INFO', required=False)
    args = parser.parse_args()

    config = Configuration()
    config.load(os.environ["CONFIG_LOCATION"])

    logger = MantisLogger()
    logger.configure(config)

    line = sys.stdin.read().rstrip()
    if args.level == "DEBUG":
        logger.debug(line)
    elif args.level == "INFO":
        logger.info(line)
    elif args.level == "WARN":
        logger.warn(line)
    elif args.level == "ERROR":
        logger.error(line)
    elif args.level == "TRACE":
        logger.trace(line)
    else:
        logger.error("LogLevel: %s not found, redirected to WARN" % args.level)
        logger.warn(line)

    sys.exit(0)