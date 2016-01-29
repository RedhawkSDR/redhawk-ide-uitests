#!/usr/bin/env python
#
# AUTO-GENERATED
#
# Source: AllPropertyTypesService.spd.xml

import sys, signal, copy, os
import logging

from ossie.cf import CF, CF__POA #@UnusedImport
from ossie.service import start_service
from omniORB import CORBA, URI, PortableServer

from ossie.cf import CF
from ossie.cf import CF__POA

class AllPropertyTypesService(CF__POA.Resource):

    def __init__(self, name="AllPropertyTypesService", execparams={}):
        self.name = name
        self._log = logging.getLogger(self.name)
        logging.getLogger().setLevel(logging.DEBUG)

    def terminateService(self):
        pass

    def initialize(self):
        # TODO
        pass

    def releaseObject(self):
        # TODO
        pass

    def runTest(self, testid, testValues):
        # TODO
        pass

    def configure(self, configProperties):
        # TODO
        pass

    def query(self, configProperties):
        # TODO
        pass

    def initializeProperties(self, initialProperties):
        # TODO
        pass

    def registerPropertyListener(self, obj, prop_ids, interval):
        # TODO
        pass

    def unregisterPropertyListener(self, id):
        # TODO
        pass

    def getPort(self, name):
        # TODO
        pass

    def getPortSet(self):
        # TODO
        pass

    def retrieve_records(self, howMany, startingRecord):
        # TODO
        pass

    def retrieve_records_by_date(self, howMany, to_timeStamp):
        # TODO
        pass

    def retrieve_records_from_date(self, howMany, from_timeStamp):
        # TODO
        pass

    def setLogLevel(self, logger_id, newLevel):
        # TODO
        pass

    def getLogConfig(self):
        # TODO
        pass

    def setLogConfig(self, config_contents):
        # TODO
        pass

    def setLogConfigURL(self, config_url):
        # TODO
        pass

    def start(self):
        # TODO
        pass

    def stop(self):
        # TODO
        pass

    def _get_log_level(self):
        # TODO
        pass

    def _set_log_level(self, data):
        # TODO
        pass

    def _get_identifier(self):
        # TODO
        pass

    def _get_started(self):
        # TODO
        pass

    def _get_softwareProfile(self):
        # TODO
        pass


if __name__ == '__main__':
    start_service(AllPropertyTypesService, thread_policy=PortableServer.SINGLE_THREAD_MODEL)
