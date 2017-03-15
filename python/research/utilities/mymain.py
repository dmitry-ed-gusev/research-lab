"""
 Some work with Python classes and other utilities.

"""

import configuration as conf

print "Working with config is starting..."

# path to config
config_path = '.'

# create config object and load config
config = conf.Configuration()
config.load(config_path)

print config.get('name')
