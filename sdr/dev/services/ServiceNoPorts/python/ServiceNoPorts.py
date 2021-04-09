#!/usr/bin/env python
#
# AUTO-GENERATED
#
# Source: ServiceNoPorts.spd.xml

import sys, signal, copy, os
import logging

from ossie.cf import CF, CF__POA #@UnusedImport
from ossie.service import start_service
from omniORB import CORBA, URI, PortableServer

from ossie.cf import CF
from ossie.cf import CF__POA

class ServiceNoPorts(CF__POA.LifeCycle):

    def __init__(self, name="ServiceNoPorts", execparams={}):
        self.name = name
        self._log = logging.getLogger(self.name)

    def terminateService(self):
        pass

    def initialize(self):
        # TODO
        pass

    def releaseObject(self):
        # TODO
        pass


if __name__ == '__main__':
    start_service(ServiceNoPorts, thread_policy=PortableServer.SINGLE_THREAD_MODEL)
