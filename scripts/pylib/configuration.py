#!/usr/bin/env python
# coding=utf-8

import os
import yaml


class Configuration(object):

    def __init__(self):
        self.config_dict = {}

    def load(self, path):
        print "Loading configs *.yml/*yaml from [{}].".format(path)
        for file in os.listdir(path):
            if file.endswith(".yml") or file.endswith(".yaml"):
                if path.endswith("\\") or path.endswith("/"):
                    file_path = path + file
                else:
                    file_path = path + "/" + file
                with open(file_path, 'r') as cfg_file:
                    try:
                        yaml_file = yaml.load(cfg_file)
                        self.merge_dict(yaml_file)
                    except yaml.YAMLError as err:
                        print (err)
        # merge with dictionary
        self.merge_env()

    def merge_dict(self, new_dict):
        dict1 = self.config_dict
        if len(dict1) != 0:
            result = self.__add_entity__(dict1, new_dict)
            self.config_dict = result
        else:
            self.config_dict.update(new_dict)
        return

    def __add_entity__(self, dict1, dict2):
        if isinstance(dict2, dict):
            for key in dict2.keys():
                if key in dict1.keys():
                    self.__add_entity__(dict1[key], dict2[key])
                else:
                    dict1[key] = (dict2[key])
            return dict1

    def merge_env(self):
        for item in os.environ:
            self.config_dict[item.lower()] = os.environ[item]

    # Key should be in such format:
    #   key1 - for single key entry
    #   key1.key2.key3 - for complex key entry
    def get(self, key):
        try:
            result = self.__get_value(key, self.config_dict)
            return result
        except KeyError as err:
            raise ConfigError("Configuration entry %s not found" % err.message)

    def __get_value(self, key, values):
        keys = key.split(".", 1)
        if len(keys) == 1:
            result = values[keys[0]]
            return result
        elif len(keys) == 2:
            result = self.__get_value(keys[1], values[keys[0]])
        return result


class ConfigError(Exception):
    """Invalid configuration error"""
