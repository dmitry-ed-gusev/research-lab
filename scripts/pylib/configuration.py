#!/usr/bin/env python
# coding=utf-8

import os
from string import Template
import yaml


class Configuration(object):
    """Tree-like configuration-holding structure, allows loading from YAML and retrieving values
        by using chained hierarchical key with dot-separated levels, e.g. "hdfs.namenode.address"
    """

    def __init__(self):
        self.config_dict = {}

    @staticmethod
    def parse_yaml(file_path):
        """Parses single YAML file to create Configuration object
            
            :param file_path: path to YAML file to load settings from
            :rtype: mantis.lib.configuration.Configuration
        """
        with open(file_path, 'r') as cfg_file:
            try:
                cfg_file_content = cfg_file.read()
                if "\t" in cfg_file_content:
                    raise ConfigError("Config file %s contains 'tab' character" % file_path)
                config = yaml.load(cfg_file_content)
            except yaml.YAMLError as err:
                print "Failed to parse config file %s. Error: %s" % (file_path, err)
                raise ConfigError("Failed to parse config file %s. Error: %s" % (file_path, err))
            return config

    def load(self, path):
        """Parses all YAML files from the given directory to add them into this configuration instance
        
            :param path: directory to load files from
            :type path: str
        """
        for file in os.listdir(path):
            if file.endswith(".yml") or file.endswith(".yaml"):
                file_path = os.path.join(path, file)
                yaml_file = Configuration.parse_yaml(file_path)
                try:
                    self.merge_dict(yaml_file)
                except ConfigError as ex:
                    raise ConfigError('ERROR while merging file %s to configuration.\n%s' % (file_path, ex.message))

        self.merge_env()

    def merge_dict(self, new_dict):
        """Adds another dictionary (respecting nested subdictionaries) to config
        
            :param new_dict: dictionary to be added
            :type new_dict: dict
        """
        dict1 = self.config_dict
        if len(dict1) != 0:
            result = self.__add_entity__(dict1, new_dict)
            self.config_dict = result
        else:
            self.config_dict.update(new_dict)
        return

    def __add_entity__(self, dict1, dict2, current_key=''):
        """Adds second dictionary to the first (processing nested dicts recursively)
        No overwriting is accepted.
        
            :param dict1: target dictionary
            :type dict1: dict
            :param dict2: source dictionary (exception raising if it is not dict)
            :type dict2: dict
            :return: the first parameter (i.e. target dictionary)
            :rtype: dict
        """
        if isinstance(dict2, dict) and isinstance(dict1, dict):
            for key in dict2.keys():
                if key in dict1.keys():
                    sep = '.'
                    if current_key == '':
                        sep = ''
                    self.__add_entity__(dict1[key], dict2[key], '%s%s%s' % (current_key, sep, key))
                else:
                    dict1[key] = dict2[key]
        else:
            raise ConfigError("Attempt of overwriting old value %s with new %s to the key %s" % (dict1, dict2, current_key))
        return dict1

    def merge_env(self):
        """Adds environment variables to this config instance"""
        for item in os.environ:
            self.config_dict[item.lower()] = os.environ[item]

    def get(self, key, default=None):
        """Retrieves config value for given key.
            
            :param key: text key, which could be complex, e.g. "key1.key2.key3" to reach nested dictionaries
            :type key: str
            :type default: Any
            :rtype: Any
        """
        try:
            result = self.__get_value(key, self.config_dict)
            return result
        except KeyError as err:
            if default is not None:
                return default
            raise ConfigError("Configuration entry %s not found" % err.message)

    def set(self, key, value):
        """Sets config value, creating all the nested levels if necessary
        
            :type key: str 
            :type value: Any
        """
        keys = key.split(".")
        values = self.config_dict
        while len(keys) > 1:
            cur = keys.pop(0)
            if cur not in values:
                values[cur] = dict()
            values = values[cur]
        values[keys[0]] = value

    def resolve_and_set(self, key, value):
        """Performs template substitution in "value" using mapping from config (only top-level), then sets it in config
        
            :param key: key to assign value to (could be multi-level)
            :type key: str
            :param value: value with substitution patterns e.g. "system-$env" (see string.Template)
            :type value: str 
        """
        template = Template(value)
        resolved = template.substitute(self.config_dict)
        self.set(key, resolved)

    def __get_value(self, key, values):
        """Internal method for using in "get", recursively search for dictionaries by key parts and retrieves value
        
            :type key: str
            :param values: dictionary to search in
            :type values: dict
            :rtype: Any
        """
        keys = key.split(".", 1)
        if len(keys) < 2:
            return values[keys[0]]
        else:
            return self.__get_value(keys[1], values[keys[0]])


class ConfigError(Exception):
    """Invalid configuration error"""


def load_config():
    """ Load configuration from env['CONFIG_LOCATION'] (if specified) or 'config'\n
        Also initialize logger-required fields (mdc_pid = current pid, other fields = 'ERROR'
    """
    if 'CONFIG_LOCATION' in os.environ:
        config_location = os.environ['CONFIG_LOCATION']
    else:
        config_location = os.path.dirname(__file__) + '/../../config'
    config = Configuration()
    config.set('step_name', 'ERROR')
    config.set('source_system', 'ERROR')
    config.set('source_system_location', 'ERROR')
    config.set('source_system_env', 'ERROR')
    config.set('source_table', 'ERROR')
    config.set('mdc_pid', os.getpid())
    config.load(config_location)
    config.set('config_location', os.path.abspath(config_location))
    return config
