#!/usr/bin/env python
#
#
# AUTO-GENERATED
#
# Source: Negotiator.spd.xml
from ossie.resource import start_component
import logging

from Negotiator_base import *
from ossie.cf.ExtendedCF__POA import NegotiableUsesPort, NegotiableProvidesPort
from ossie.cf.ExtendedCF import TransportInfo, ConnectionStatus, UsesConnection
from ossie.properties import props_from_dict

class TransportFactory1(object):
    def name(self):
        return 'transport_1'
    
    def properties(self):
        return props_from_dict({'first':1, 'second':'two'})

class TransportFactory2(object):
    def name(self):
        return 'transport_2'

    def properties(self):
        return []

class TransportFactory3(object):
    def name(self):
        return 'transport_3'
    
    def properties(self):
        return props_from_dict({'configured': True})

TRANSPORTS = [
    TransportFactory1(),
    TransportFactory2(),
    TransportFactory3()
]

class InNegotiablePort(NegotiableProvidesPort):
    def _get_supportedTransports(self):
        return [TransportInfo(t.name(), t.properties()) for t in TRANSPORTS]

class OutNegotiablePort(NegotiableUsesPort):
    def __init__(self, parent):
        self._parent = parent
        self._lock = threading.Lock()
        self._connections = {}

    def connectPort(self, port, connectionId):
        with self._lock:
            selected = self._parent.transport
            for transport in TRANSPORTS:
                if transport.name() == selected:
                    properties = transport.properties()
                    self._connections[connectionId] = ConnectionStatus(connectionId, port, True, selected, properties)
                    return
        raise CF.PortSupplier.UnknownPort('bad transport ' + selected)
    
    def disconnectPort(self, connectionId):
        with self._lock:
            self._connections.pop(connectionId)
    
    def _get_supportedTransports(self):
        return [TransportInfo(t.name(), t.properties()) for t in TRANSPORTS]
    
    def _get_connectionStatus(self):
        with self._lock:
            return self._connections.values()

    def _toConnectionStatus(self, connectionId, connection):
        return ConnectionStatus(connectionId, connection.port, True, connection.transport, connection.properties)
    
    def _get_connections(self):
        with self._lock:
            return [UsesConnection(k, v) for k,v in self._connections.iteritems()]

class Negotiator_i(Negotiator_base):

    def constructor(self):
        self.port_negotiable_in = InNegotiablePort()
        self.port_negotiable_out = OutNegotiablePort(self)
        
    def process(self):
        self._baseLog.debug("process() example log message")
        return NOOP

  
if __name__ == '__main__':
    logging.getLogger().setLevel(logging.INFO)
    logging.debug("Starting Component")
    start_component(Negotiator_i)

