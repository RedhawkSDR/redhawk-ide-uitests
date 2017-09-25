#!/usr/bin/env python
#
# AUTO-GENERATED CODE.  DO NOT MODIFY!
#
# Source: PropertyFilteringComp.spd.xml
from ossie.cf import CF
from ossie.cf import CF__POA
from ossie.utils import uuid

from ossie.component import Component
from ossie.threadedcomponent import *
from ossie.properties import simple_property
from ossie.properties import simpleseq_property
from ossie.properties import struct_property

import Queue, copy, time, threading

class PropertyFilteringComp_base(CF__POA.Resource, Component, ThreadedComponent):
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
        prop_ro = simple_property(id_="prop_ro",
                                  type_="string",
                                  defvalue="a",
                                  mode="readonly",
                                  action="external",
                                  kinds=("property",))


        prop_rw = simple_property(id_="prop_rw",
                                  type_="string",
                                  defvalue="b",
                                  mode="readwrite",
                                  action="external",
                                  kinds=("property",))


        prop_wo = simple_property(id_="prop_wo",
                                  type_="string",
                                  defvalue="c",
                                  mode="writeonly",
                                  action="external",
                                  kinds=("property",))


        exec_ro = simple_property(id_="exec_ro",
                                  type_="string",
                                  defvalue="g",
                                  mode="readonly",
                                  action="external",
                                  kinds=("execparam",))


        exec_rw = simple_property(id_="exec_rw",
                                  type_="string",
                                  defvalue="h",
                                  mode="readwrite",
                                  action="external",
                                  kinds=("execparam",))


        exec_wo = simple_property(id_="exec_wo",
                                  type_="string",
                                  defvalue="i",
                                  mode="writeonly",
                                  action="external",
                                  kinds=("execparam",))


        config_ro = simple_property(id_="config_ro",
                                    type_="string",
                                    defvalue="j",
                                    mode="readonly",
                                    action="external",
                                    kinds=("configure",))


        config_rw = simple_property(id_="config_rw",
                                    type_="string",
                                    defvalue="k",
                                    mode="readwrite",
                                    action="external",
                                    kinds=("configure",))


        config_wo = simple_property(id_="config_wo",
                                    type_="string",
                                    defvalue="l",
                                    mode="writeonly",
                                    action="external",
                                    kinds=("configure",))


        commandline_ro = simple_property(id_="commandline_ro",
                                         type_="string",
                                         defvalue="p",
                                         mode="readonly",
                                         action="external",
                                         kinds=("property",))


        commandline_rw = simple_property(id_="commandline_rw",
                                         type_="string",
                                         defvalue="q",
                                         mode="readwrite",
                                         action="external",
                                         kinds=("property",))


        commandline_wo = simple_property(id_="commandline_wo",
                                         type_="string",
                                         defvalue="r",
                                         mode="writeonly",
                                         action="external",
                                         kinds=("property",))


        class MessageRo(object):
            simple1 = simple_property(
                                      id_="simple1",
                                      
                                      type_="string",
                                      defvalue="m"
                                      )
        
            def __init__(self, **kw):
                """Construct an initialized instance of this struct definition"""
                for classattr in type(self).__dict__.itervalues():
                    if isinstance(classattr, (simple_property, simpleseq_property)):
                        classattr.initialize(self)
                for k,v in kw.items():
                    setattr(self,k,v)
        
            def __str__(self):
                """Return a string representation of this structure"""
                d = {}
                d["simple1"] = self.simple1
                return str(d)
        
            @classmethod
            def getId(cls):
                return "message_ro"
        
            @classmethod
            def isStruct(cls):
                return True
        
            def getMembers(self):
                return [("simple1",self.simple1)]

        message_ro = struct_property(id_="message_ro",
                                     structdef=MessageRo,
                                     configurationkind=("message",),
                                     mode="readonly")


        class MessageRw(object):
            simple2 = simple_property(
                                      id_="simple2",
                                      
                                      type_="string",
                                      defvalue="n"
                                      )
        
            def __init__(self, **kw):
                """Construct an initialized instance of this struct definition"""
                for classattr in type(self).__dict__.itervalues():
                    if isinstance(classattr, (simple_property, simpleseq_property)):
                        classattr.initialize(self)
                for k,v in kw.items():
                    setattr(self,k,v)
        
            def __str__(self):
                """Return a string representation of this structure"""
                d = {}
                d["simple2"] = self.simple2
                return str(d)
        
            @classmethod
            def getId(cls):
                return "message_rw"
        
            @classmethod
            def isStruct(cls):
                return True
        
            def getMembers(self):
                return [("simple2",self.simple2)]

        message_rw = struct_property(id_="message_rw",
                                     structdef=MessageRw,
                                     configurationkind=("message",),
                                     mode="readwrite")


        class MessageWo(object):
            simple3 = simple_property(
                                      id_="simple3",
                                      
                                      type_="string",
                                      defvalue="o"
                                      )
        
            def __init__(self, **kw):
                """Construct an initialized instance of this struct definition"""
                for classattr in type(self).__dict__.itervalues():
                    if isinstance(classattr, (simple_property, simpleseq_property)):
                        classattr.initialize(self)
                for k,v in kw.items():
                    setattr(self,k,v)
        
            def __str__(self):
                """Return a string representation of this structure"""
                d = {}
                d["simple3"] = self.simple3
                return str(d)
        
            @classmethod
            def getId(cls):
                return "message_wo"
        
            @classmethod
            def isStruct(cls):
                return True
        
            def getMembers(self):
                return [("simple3",self.simple3)]

        message_wo = struct_property(id_="message_wo",
                                     structdef=MessageWo,
                                     configurationkind=("message",),
                                     mode="writeonly")




