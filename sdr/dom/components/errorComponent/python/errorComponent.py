#!/usr/bin/env python 
#
# AUTO-GENERATED
#
# Source: errorComponent.spd.xml
# Generated on: Tue Apr 19 11:05:31 EDT 2016
# REDHAWK IDE
# Version: @version@
# Build id: @buildId@
from ossie.resource import Resource, start_component
from ossie.cf import CF
import logging

from errorComponent_base import * 

class errorComponent_i(errorComponent_base):
    """Generates errors for common CF operations"""
    def constructor(self):
        if (self.enableErrors):
            raise Exception("Error from constructor method")

    def initializeProperties(self, props):
        self._log.info("initializeProperties called with props " + str([prop.id for prop in props]))
        if not self.enableErrors:
            errorComponent_base.initializeProperties(self, props)
            return

        for prop in props:
            if prop.id == 'partialConfigProp':
                raise CF.PropertySet.PartialConfiguration([prop])
            elif prop.id == 'invalidConfigProp':
                raise CF.PropertySet.InvalidConfiguration("Error from initializeProperties method", [prop])
            elif prop.id == 'alreadyInitProp':
                raise CF.PropertyEmitter.AlreadyInitialized()

        errorComponent_base.configure(self, props)

    def configure(self, props):
        self._log.info("configure called with props " + str([prop.id for prop in props]))
        if not self.enableErrors:
            errorComponent_base.configure(self, props)
            return

        for prop in props:
            if prop.id in ('partialConfigConfig', 'partialConfigProp'):
                raise CF.PropertySet.PartialConfiguration([prop])
            elif prop.id in ('invalidConfigConfig', 'invalidConfigProp'):
                raise CF.PropertySet.InvalidConfiguration("Error from configure method", [prop])

        errorComponent_base.configure(self, props)

    def start(self):
        if self.enableErrors:
            raise CF.Resource.StartError(CF.CF_EINVAL, "Error from start method")
        errorComponent_base.start(self)

    def stop(self):
        if self.enableErrors:
            raise CF.Resource.StopError(CF.CF_EBUSY, "Error from stop method")
        errorComponent_base.stop(self)

    def process(self):
        return NOOP
  
if __name__ == '__main__':
    logging.getLogger().setLevel(logging.WARN)
    logging.debug("Starting Component")
    start_component(errorComponent_i)
