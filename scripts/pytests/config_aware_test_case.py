# coding=utf-8

import os
import unittest

from pylib.configuration import Configuration


class ConfigAwareTestCase(unittest.TestCase):

    longMessage = True
    config_dir = os.path.abspath("pytests/config")

    def _loadConfig(self):
        conf = Configuration()
        #conf.load(self.config_dir)
        conf.set("config_location", self.config_dir)
        conf.set("source_system", "test_system")
        conf.set("source_system_env", "dev")
        conf.set("source_system_location", "global")
        conf.set("source_table", "test_table")
        conf.set("step_name", "test_step")
        conf.set("mdc_pid", "123")
        conf.set("hadoop_client_home", "tests_mantis_hadoop_clients/pyunit/ut-sample-data")
        return conf
