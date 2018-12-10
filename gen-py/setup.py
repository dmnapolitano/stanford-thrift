#!/usr/bin/env python

from setuptools import setup

setup(name='stanford-thrift',
      version='0.1',
      description=(''),
      long_description="Stanford CoreNLP server wrapper using Apache Thrift",
      keywords='natural language processing',
      url='https://github.com/EducationalTestingService/stanford-thrift',
      author='Diane Napolitano',
      author_email='dnapolitano@ets.org',
      classifiers=[
          "License :: OSI Approved :: Apache Software License",
          "Topic :: Text Processing",
      ],
      packages=['corenlp'],
      install_requires=[
        "python==2.7",
        "thrift"])
