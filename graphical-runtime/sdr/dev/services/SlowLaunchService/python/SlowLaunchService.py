#!/usr/bin/env python
#
# AUTO-GENERATED
#
# Source: SlowLaunchService.spd.xml

import sys, signal, copy, os
import logging

from ossie.cf import CF, CF__POA #@UnusedImport
from ossie.service import start_service
from omniORB import CORBA, URI, PortableServer

from ossie.cf import CF
from ossie.cf import CF__POA
import time

class SlowLaunchService(CF__POA.PortSupplier):

    def __init__(self, name="SlowLaunchService", execparams={}):
        self.name = name
        self._log = logging.getLogger(self.name)

    def terminateService(self):
        pass

    def getPort(self, name):
        # TODO
        pass


if __name__ == '__main__':
    time.sleep(15)
    start_service(SlowLaunchService, thread_policy=PortableServer.SINGLE_THREAD_MODEL)
