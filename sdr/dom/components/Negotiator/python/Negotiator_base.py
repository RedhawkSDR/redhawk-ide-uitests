#!/usr/bin/env python
#
# AUTO-GENERATED CODE.  DO NOT MODIFY!
#
# Source: Negotiator.spd.xml
from ossie.cf import CF
from ossie.cf import CF__POA
from ossie.utils import uuid

from ossie.component import Component
from ossie.threadedcomponent import *
from ossie.properties import simple_property

import Queue, copy, time, threading
from ossie.resource import usesport, providesport, PortCallError
from ossie.cf import ExtendedCF__POA
from ossie.cf import ExtendedCF

class Negotiator_base(CF__POA.Resource, Component, ThreadedComponent):
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
            self.port_negotiable_in = PortExtendedCFNegotiableProvidesPortIn_i(self, "negotiable_in")
            self.port_negotiable_in._portLog = self._baseLog.getChildLogger('negotiable_in', 'ports')
            self.port_negotiable_out = PortExtendedCFNegotiableProvidesPortOut_i(self, "negotiable_out")
            self.port_negotiable_out._portLog = self._baseLog.getChildLogger('negotiable_out', 'ports')

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
                self._baseLog.exception("Error stopping")
            Component.releaseObject(self)

        ######################################################################
        # PORTS
        # 
        # DO NOT ADD NEW PORTS HERE.  You can add ports in your derived class, in the SCD xml file, 
        # or via the IDE.

        # 'ExtendedCF/NegotiableProvidesPort' port
        class PortExtendedCFNegotiableProvidesPortIn(ExtendedCF__POA.NegotiableProvidesPort):
            """This class is a port template for the PortExtendedCFNegotiableProvidesPortIn_i port and
            should not be instantiated nor modified.
            
            The expectation is that the specific port implementation will extend
            from this class instead of the base CORBA class ExtendedCF__POA.NegotiableProvidesPort.
            """
            pass

        # 'ExtendedCF/NegotiableProvidesPort' port
        class PortExtendedCFNegotiableProvidesPortOut(ExtendedCF__POA.QueryablePort):
            """This class is a port template for the PortExtendedCFNegotiableProvidesPortOut_i port and
            should not be instantiated nor modified.
            
            The expectation is that the specific port implementation will extend
            from this class instead of the base CORBA class ExtendedCF__POA.QueryablePort.
            """
            pass

        port_negotiable_in = providesport(name="negotiable_in",
                                          repid="IDL:ExtendedCF/NegotiableProvidesPort:1.0",
                                          type_="control")

        port_negotiable_out = usesport(name="negotiable_out",
                                       repid="IDL:ExtendedCF/NegotiableProvidesPort:1.0",
                                       type_="control")

        ######################################################################
        # PROPERTIES
        # 
        # DO NOT ADD NEW PROPERTIES HERE.  You can add properties in your derived class, in the PRF xml file
        # or by using the IDE.
        transport = simple_property(id_="transport",
                                    name="transport",
                                    type_="string",
                                    defvalue="transport_1",
                                    mode="readwrite",
                                    action="external",
                                    kinds=("property",))



'''provides port(s). Send logging to _portLog '''

class PortExtendedCFNegotiableProvidesPortIn_i(Negotiator_base.PortExtendedCFNegotiableProvidesPortIn):
    def __init__(self, parent, name):
        self.parent = parent
        self.name = name
        self.sri = None
        self.queue = Queue.Queue()
        self.port_lock = threading.Lock()

    def negotiateTransport(self, transportType, transportProperties):
        # TODO:
        pass

    def disconnectTransport(self, transportId):
        # TODO:
        pass

    def _get_supportedTransports(self):
        # TODO:
        pass

'''uses port(s). Send logging to _portLog '''

class PortExtendedCFNegotiableProvidesPortOut_i(Negotiator_base.PortExtendedCFNegotiableProvidesPortOut):
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
            port = connection._narrow(ExtendedCF.NegotiableProvidesPort)
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

    def negotiateTransport(self, transportType, transportProperties, __connection_id__ = ""):
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
                        retVal = port.negotiateTransport(transportType, transportProperties)
                    except Exception:
                        self.parent._baseLog.exception("The call to negotiateTransport failed on port %s connection %s instance %s", self.name, connId, port)
                        raise
                if not found_connection:
                    raise PortCallError("Connection id "+__connection_id__+" not found.", self.getConnectionIds())
            else:
                for connId, port in self.outConnections.items():
                    try:
                        retVal = port.negotiateTransport(transportType, transportProperties)
                    except Exception:
                        self.parent._baseLog.exception("The call to negotiateTransport failed on port %s connection %s instance %s", self.name, connId, port)
                        raise
        finally:
            self.port_lock.release()

        return retVal

    def disconnectTransport(self, transportId, __connection_id__ = ""):
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
                        port.disconnectTransport(transportId)
                    except Exception:
                        self.parent._baseLog.exception("The call to disconnectTransport failed on port %s connection %s instance %s", self.name, connId, port)
                        raise
                if not found_connection:
                    raise PortCallError("Connection id "+__connection_id__+" not found.", self.getConnectionIds())
            else:
                for connId, port in self.outConnections.items():
                    try:
                        port.disconnectTransport(transportId)
                    except Exception:
                        self.parent._baseLog.exception("The call to disconnectTransport failed on port %s connection %s instance %s", self.name, connId, port)
                        raise
        finally:
            self.port_lock.release()

    def supportedTransports(self, __connection_id__ = ""):
        return self._get_supportedTransports(__connection_id__)

    def _get_supportedTransports(self, __connection_id__ = ""):
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
                        retVal = port._get_supportedTransports()
                    except Exception:
                        self.parent._baseLog.exception("The call to _get_supportedTransports failed on port %s connection %s instance %s", self.name, connId, port)
                        raise
                if not found_connection:
                    raise PortCallError("Connection id "+__connection_id__+" not found.", self.getConnectionIds())
            else:
                for connId, port in self.outConnections.items():
                    try:
                        retVal = port._get_supportedTransports()
                    except Exception:
                        self.parent._baseLog.exception("The call to _get_supportedTransports failed on port %s connection %s instance %s", self.name, connId, port)
                        raise
        finally:
            self.port_lock.release()

        return retVal


