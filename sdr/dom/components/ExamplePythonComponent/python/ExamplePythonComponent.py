#!/usr/bin/env python 
#
# AUTO-GENERATED
#
# Source: ExamplePythonComponent.spd.xml
# Generated on: Mon Jun 03 10:51:21 EDT 2013
# REDHAWK IDE
# Version: 1.8.4
# Build id: R201305151907
from ossie.resource import Resource, start_component
import logging

from ExamplePythonComponent_base import * 

class ExamplePythonComponent_i(ExamplePythonComponent_base):
    """<DESCRIPTION GOES HERE>"""
    def initialize(self):
        ExamplePythonComponent_base.initialize(self)

    def process(self):
        self._log.debug("process() example log message")
        return NOOP
        
  
if __name__ == '__main__':
    logging.getLogger().setLevel(logging.WARN)
    logging.debug("Starting Component")
    start_component(ExamplePythonComponent_i)
