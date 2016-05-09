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
    def initialize(self):
        errorComponent_base.initialize(self)

    def configure(self, props):
        if not self.enableErrors:
            errorComponent_base.configure(self, props)
            return

        ok = None
        err = None
        for prop in props:
            if prop.id == 'partialConfigError':
                raise CF.PropertySet.PartialConfiguration([prop])
            elif prop.id == 'errorOnConfigure':
                err = prop
            elif prop.id == 'okToConfigure':
                ok = prop
        if ok:
            if err:
                errorComponent_base.configure(self, [prop for prop in props if prop.id != err.id])
                raise CF.PropertySet.PartialConfiguration(err)
            else:
                errorComponent_base.configure(self, props)
        elif err:
            raise CF.PropertySet.InvalidConfiguration("Test invalid configuration", props)
        else:
            errorComponent_base.configure(self, props)

    def start(self):
        if self.enableErrors:
            raise CF.Resource.StartError(CF.CF_EINVAL, "Test start failure")
        errorComponent_base.start(self)

    def stop(self):
        if self.enableErrors:
            raise CF.Resource.StopError(CF.CF_EBUSY, "Test stop failure")
        errorComponent_base.stop(self)

    def process(self):
        return NOOP
  
if __name__ == '__main__':
    logging.getLogger().setLevel(logging.WARN)
    logging.debug("Starting Component")
    start_component(errorComponent_i)
