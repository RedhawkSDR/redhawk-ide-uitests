#!/usr/bin/env python
#
# AUTO-GENERATED CODE.  DO NOT MODIFY!
#
# Source: errorComponent.spd.xml
from ossie.cf import CF
from ossie.cf import CF__POA
from ossie.utils import uuid

from ossie.component import Component
from ossie.threadedcomponent import *
from ossie.properties import simple_property

import Queue, copy, time, threading

class errorComponent_base(CF__POA.Resource, Component, ThreadedComponent):
        # These values can be altered in the __init__ of your derived class

        PAUSE = 0.0125 # The amount of time to sleep if process return NOOP
        TIMEOUT = 5.0 # The amount of time to wait for the process thread to die when stop() is called
        DEFAULT_QUEUE_SIZE = 100 # The number of BulkIO packets that can be in the queue before pushPacket will block

        def __init__(self, identifier, execparams):
            loggerName = (execparams['NAME_BINDING'].replace('/', '.')).rsplit("_", 1)[0]
            Component.__init__(self, identifier, execparams, loggerName=loggerName)
            ThreadedComponent.__init__(self)

            # self.auto_start is deprecated and is only kept for API compatibility
            # with 1.7.X and 1.8.0 components.  This variable may be removed
            # in future releases
            self.auto_start = False
            # Instantiate the default implementations for all ports on this component

        def start(self):
            Component.start(self)
            ThreadedComponent.startThread(self, pause=self.PAUSE)

        def stop(self):
            Component.stop(self)
            if not ThreadedComponent.stopThread(self, self.TIMEOUT):
                raise CF.Resource.StopError(CF.CF_NOTSET, "Processing thread did not die")

        def releaseObject(self):
            try:
                self.stop()
            except Exception:
                self._log.exception("Error stopping")
            Component.releaseObject(self)

        ######################################################################
        # PORTS
        # 
        # DO NOT ADD NEW PORTS HERE.  You can add ports in your derived class, in the SCD xml file, 
        # or via the IDE.

        ######################################################################
        # PROPERTIES
        # 
        # DO NOT ADD NEW PROPERTIES HERE.  You can add properties in your derived class, in the PRF xml file
        # or by using the IDE.
        errorOnConfigure = simple_property(id_="errorOnConfigure",
                                           type_="string",
                                           defvalue="abc",
                                           mode="readwrite",
                                           action="external",
                                           kinds=("property",),
                                           description="""Triggers an InvalidConfiguration or PartialConfiguration exception (depending on if combined with other properties)""")


        partialConfigError = simple_property(id_="partialConfigError",
                                             type_="string",
                                             mode="readwrite",
                                             action="external",
                                             kinds=("property",),
                                             description="""If a configure call includes this property, you'll get a PartialConfiguration exception for ONLY this property.""")


        okToConfigure = simple_property(id_="okToConfigure",
                                        type_="string",
                                        defvalue="def",
                                        mode="readwrite",
                                        action="external",
                                        kinds=("property",))


        enableErrors = simple_property(id_="enableErrors",
                                       type_="boolean",
                                       defvalue=False,
                                       mode="readwrite",
                                       action="external",
                                       kinds=("property",),
                                       description="""Enable/disable reporting errors""")




