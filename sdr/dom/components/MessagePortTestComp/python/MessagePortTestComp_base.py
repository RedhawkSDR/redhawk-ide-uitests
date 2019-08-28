#!/usr/bin/env python
#
# AUTO-GENERATED CODE.  DO NOT MODIFY!
#
# Source: MessagePortTestComp.spd.xml
from ossie.cf import CF
from ossie.cf import CF__POA
from ossie.utils import uuid

from ossie.component import Component
from ossie.threadedcomponent import *
from ossie.properties import simple_property
from ossie.properties import simpleseq_property
from ossie.properties import struct_property

import Queue, copy, time, threading
from ossie.resource import usesport, providesport, PortCallError
from ossie.events import MessageSupplierPort
from ossie.cf import ExtendedCF
from ossie.cf import ExtendedCF__POA
import CosEventChannelAdmin

class MessagePortTestComp_base(CF__POA.Resource, Component, ThreadedComponent):
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
            self.port_message_out = MessageSupplierPort()
            self.port_eventChannel_out = PortCosEventChannelAdminEventChannelOut_i(self, "eventChannel_out")

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

        # 'omg.org/CosEventChannelAdmin/EventChannel' port
        class PortCosEventChannelAdminEventChannelOut(ExtendedCF__POA.QueryablePort):
            """This class is a port template for the PortCosEventChannelAdminEventChannelOut_i port and
            should not be instantiated nor modified.
            
            The expectation is that the specific port implementation will extend
            from this class instead of the base CORBA class ExtendedCF__POA.QueryablePort.
            """
            pass

        port_message_out = usesport(name="message_out",
                                    repid="IDL:ExtendedEvent/MessageEvent:1.0",
                                    type_="control")

        port_eventChannel_out = usesport(name="eventChannel_out",
                                         repid="IDL:omg.org/CosEventChannelAdmin/EventChannel:1.0",
                                         type_="control")

        ######################################################################
        # PROPERTIES
        # 
        # DO NOT ADD NEW PROPERTIES HERE.  You can add properties in your derived class, in the PRF xml file
        # or by using the IDE.
        class Mymessage(object):
            EnterMessageHere = simple_property(
                                               id_="mymessage::EnterMessageHere",
                                               
                                               name="EnterMessageHere",
                                               type_="string")
        
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
                d["EnterMessageHere"] = self.EnterMessageHere
                return str(d)
        
            @classmethod
            def getId(cls):
                return "mymessage"
        
            @classmethod
            def isStruct(cls):
                return True
        
            def getMembers(self):
                return [("EnterMessageHere",self.EnterMessageHere)]

        mymessage = struct_property(id_="mymessage",
                                    name="mymessage",
                                    structdef=Mymessage,
                                    configurationkind=("property",),
                                    mode="readwrite")



'''uses port(s)'''

class PortCosEventChannelAdminEventChannelOut_i(MessagePortTestComp_base.PortCosEventChannelAdminEventChannelOut):
    def __init__(self, parent, name):
        self.parent = parent
        self.name = name
        self.outConnections = {}
        self.port_lock = threading.Lock()

    def getConnectionIds(self):
        return self.outConnections.keys()

    def __evaluateRequestBasedOnConnections(self, __connection_id__, returnValue, inOut, out):
        if __connection_id__=='' and len(self.outConnections) > 1:
            if (out or inOut or returnValue):
                raise PortCallError("Returned parameters require either a single connection or a populated __connection_id__ to disambiguate the call.", self.getConnectionIds())
        if len(self.outConnections) == 0:
            raise PortCallError("No connections available.",[])

    def connectPort(self, connection, connectionId):
        self.port_lock.acquire()
        try:
            port = connection._narrow(CosEventChannelAdmin.EventChannel)
            self.outConnections[str(connectionId)] = port
        finally:
            self.port_lock.release()

    def disconnectPort(self, connectionId):
        self.port_lock.acquire()
        try:
            self.outConnections.pop(str(connectionId), None)
        finally:
            self.port_lock.release()

    def _get_connections(self):
        self.port_lock.acquire()
        try:
            return [ExtendedCF.UsesConnection(name, port) for name, port in self.outConnections.iteritems()]
        finally:
            self.port_lock.release()

    def for_consumers(self, __connection_id__ = ""):
        retVal = None
        self.port_lock.acquire()


        try:
            self.__evaluateRequestBasedOnConnections(__connection_id__, True, False, False)
            if __connection_id__:
                found_connection = False
                for connId, port in self.outConnections.items():
                    if __connection_id__ != connId:
                        continue
                    found_connection = True
                    try:
                        retVal = port.for_consumers()
                    except Exception:
                        self.parent._log.exception("The call to for_consumers failed on port %s connection %s instance %s", self.name, connId, port)
                        raise
                if not found_connection:
                    raise PortCallError("Connection id "+__connection_id__+" not found.", self.getConnectionIds())
            else:
                for connId, port in self.outConnections.items():
                    try:
                        retVal = port.for_consumers()
                    except Exception:
                        self.parent._log.exception("The call to for_consumers failed on port %s connection %s instance %s", self.name, connId, port)
                        raise
        finally:
            self.port_lock.release()

        return retVal

    def for_suppliers(self, __connection_id__ = ""):
        retVal = None
        self.port_lock.acquire()


        try:
            self.__evaluateRequestBasedOnConnections(__connection_id__, True, False, False)
            if __connection_id__:
                found_connection = False
                for connId, port in self.outConnections.items():
                    if __connection_id__ != connId:
                        continue
                    found_connection = True
                    try:
                        retVal = port.for_suppliers()
                    except Exception:
                        self.parent._log.exception("The call to for_suppliers failed on port %s connection %s instance %s", self.name, connId, port)
                        raise
                if not found_connection:
                    raise PortCallError("Connection id "+__connection_id__+" not found.", self.getConnectionIds())
            else:
                for connId, port in self.outConnections.items():
                    try:
                        retVal = port.for_suppliers()
                    except Exception:
                        self.parent._log.exception("The call to for_suppliers failed on port %s connection %s instance %s", self.name, connId, port)
                        raise
        finally:
            self.port_lock.release()

        return retVal

    def destroy(self, __connection_id__ = ""):
        self.port_lock.acquire()


        try:
            self.__evaluateRequestBasedOnConnections(__connection_id__, False, False, False)
            if __connection_id__:
                found_connection = False
                for connId, port in self.outConnections.items():
                    if __connection_id__ != connId:
                        continue
                    found_connection = True
                    try:
                        port.destroy()
                    except Exception:
                        self.parent._log.exception("The call to destroy failed on port %s connection %s instance %s", self.name, connId, port)
                        raise
                if not found_connection:
                    raise PortCallError("Connection id "+__connection_id__+" not found.", self.getConnectionIds())
            else:
                for connId, port in self.outConnections.items():
                    try:
                        port.destroy()
                    except Exception:
                        self.parent._log.exception("The call to destroy failed on port %s connection %s instance %s", self.name, connId, port)
                        raise
        finally:
            self.port_lock.release()


