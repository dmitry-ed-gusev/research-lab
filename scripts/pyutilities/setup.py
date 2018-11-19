#!/usr/bin/env python
# coding=utf-8

"""
    Setup script for [pyutilities] library.
    Created:  Gusev Dmitrii, 25.09.2018
    Modified: Gusev Dmitrii, 18.11.2018
"""

from setuptools import setup, find_packages

# read long description from README.md file
with open("README.md", "r") as fh:
    long_description = fh.read()

setup(name='pyutilities',
      version='0.3.1',
      description='Useful python 2.x utilities library.',
      url='https://pypi.org/project/pyutilities',
      author='Dmitrii Gusev',
      author_email='dmitry.ed.gusev@gmail.com',
      license='MIT',
      # packages=['pyutilities'], # <- we can explicitly state packages here
      packages=find_packages(),
      zip_safe=False,
      long_description=long_description,
      long_description_content_type="text/markdown",
      # todo: list of all classifiers: https://pypi.org/classifiers/
      classifiers=[
        "Programming Language :: Python :: 2.7",
        "License :: OSI Approved :: MIT License",
        "Operating System :: OS Independent"
      ])
