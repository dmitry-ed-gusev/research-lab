#!/usr/bin/env python
# coding=utf-8

import os
import logging
from string import Template
from pyutilities import parse_yaml

YAML_EXTENSION_1 = '.yml'
YAML_EXTENSION_2 = '.yaml'


# todo: replace all print() out with logging
class Configuration(object):
    """Tree-like configuration-holding structure, allows loading from YAML and retrieving values
    by using chained hierarchical key with dot-separated levels, e.g. "hdfs.namenode.address".
    Can include environment variables (switch by key), environment usually override internal values.
    :param path_to_config ???
    :param dict_to_merge ???
    :param is_override_config ???
    :param is_merge_env ???
    """

    def __init__(self, path_to_config=None, dict_to_merge=None, is_override_config=True, is_merge_env=True):
        # init logger
        self.log = logging.getLogger(__name__)
        self.log.addHandler(logging.NullHandler())
        self.log.debug("Initializing Configuration instance:" +
                       "\n\tpath -> [{}]\n\tdict -> [{}]\n\toverride config -> [{}]\n\tmerge env -> [{}]"
                       .format(path_to_config, dict_to_merge, is_override_config, is_merge_env))

        # init internal dictionary
        self.config_dict = {}

        # todo: if merge with env = True -> merge with env (call method)?

        # if provided file path - try to load config
        if path_to_config and path_to_config.strip():
            self.log.debug("Loading config from [{}].".format(path_to_config))
            self.load(path_to_config, is_merge_env)

        # merge config from file(s) with dictionary, if any
        if dict_to_merge:
            self.log.debug("Merging with provided dictionary. Override: [{}].".format(is_override_config))
            if is_override_config:  # if override -> just update internal
                self.config_dict.update(dict_to_merge)
            else:  # if not override - check each key and put if not exist
                for key, value in dict_to_merge.items():
                    if key not in self.config_dict.keys():
                        self.set(key, value)

    def load(self, path, is_merge_env=True):
        """Parses YAML file(s) from the given directory/file to add content into this configuration instance
            :param is_merge_env: merge parameters with environment (True) or not (False)
            :param path: directory/file to load files from
            :type path: str
        """
        self.log.debug('load() is working. Path [{}], is_merge_env [{}].'.format(path, is_merge_env))
        # fail-fast checks
        if not path or not path.strip():
            raise ConfigError('Provided empty path for config loading!')
        if not os.path.exists(path):
            raise ConfigError('Provided path [%s] doesn\'t exist!' % path)

        # todo: extract two methods - load from file/load from dir + refactor unit tests
        # if provided path to single file - load it, otherwise - load from directory
        if os.path.isfile(path) and (path.endswith(YAML_EXTENSION_1) or path.endswith(YAML_EXTENSION_2)):
            self.log.debug("Provided path [{}] is a YAML file. Loading.".format(path))
            try:
                self.merge_dict(parse_yaml(path))
            except ConfigError as ex:
                raise ConfigError('ERROR while merging file %s to configuration.\n%s' % (path, ex.message))

        # loading from directory (all YAML files)
        elif os.path.isdir(path):
            self.log.debug("Provided path [{}] is a directory. Loading all YAML files.".format(path))
            for some_file in os.listdir(path):
                file_path = os.path.join(path, some_file)
                if os.path.isfile(file_path) and \
                        (some_file.endswith(YAML_EXTENSION_1) or some_file.endswith(YAML_EXTENSION_2)):
                    self.log.debug("Loading configuration from [{}].".format(some_file))
                    try:
                        self.merge_dict(parse_yaml(file_path))
                    except ConfigError as ex:
                        raise ConfigError('ERROR while merging file %s to configuration.\n%s' % (file_path, ex.message))

        # unknown file/dir type
        else:
            raise ConfigError('Unknown thing [%s], not a file, not a dir!' % path)

        # merge environment variables to internal dictionary
        if is_merge_env:
            print "Merging environment variables is switched ON."
            self.merge_env()

    def merge_dict(self, new_dict):
        """Adds another dictionary (respecting nested sub-dictionaries) to config.
        If there are same keys in both dictionaries, raise ConfigError (no overwrites!)
        :param new_dict: dictionary to be added
        :type new_dict: dict
        """
        self.log.debug("merge_dict() is working. Dictionary to merge [{}].".format(new_dict))
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
        self.log.debug("merge_env() is working.")
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

# def load_config():
#     """ Load configuration from env['CONFIG_LOCATION'] (if specified) or 'config'\n
#         Also initialize logger-required fields (mdc_pid = current pid, other fields = 'ERROR'
#     """
#     if 'CONFIG_LOCATION' in os.environ:
#         config_location = os.environ['CONFIG_LOCATION']
#     else:
#         config_location = os.path.dirname(__file__) + '/../../config'
#     config = Configuration()
#     config.set('step_name', 'ERROR')
#     config.set('source_system', 'ERROR')
#     config.set('source_system_location', 'ERROR')
#     config.set('source_system_env', 'ERROR')
#     config.set('source_table', 'ERROR')
#     config.set('mdc_pid', os.getpid())
#     config.load(config_location)
#     config.set('config_location', os.path.abspath(config_location))
#     return config
