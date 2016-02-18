#!/usr/bin/env python
#
# AUTO-GENERATED CODE.  DO NOT MODIFY!
#
# Source: AllPortTypesTestComponent.spd.xml
from ossie.cf import CF
from ossie.cf import CF__POA
from ossie.utils import uuid

from ossie.component import Component
from ossie.threadedcomponent import *

import Queue, copy, time, threading
from ossie.resource import usesport, providesport
from ossie.cf import ExtendedCF
from ossie.cf import ExtendedCF__POA
from omniORB.COS import CosEventChannelAdmin
import bulkio

class AllPortTypesTestComponent_base(CF__POA.Resource, Component, ThreadedComponent):
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
            self.port_dommgr_out = PortCFDomainManagerOut_i(self, "dommgr_out")
            self.port_filemgr_out = PortCFFileManagerOut_i(self, "filemgr_out")
            self.port_eventchannel_out = PortCosEventChannelAdminEventChannelOut_i(self, "eventchannel_out")
            self.port_resource_out = PortCFResourceOut_i(self, "resource_out")
            self.port_device_out = PortCFDeviceOut_i(self, "device_out")
            self.port_aggdev_out = PortCFAggregateExecutableDeviceOut_i(self, "aggdev_out")
            self.port_execdev_out = PortCFExecutableDeviceOut_i(self, "execdev_out")
            self.port_dataFloatBIO_out = bulkio.OutFloatPort("dataFloatBIO_out")

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

        # 'CF/DomainManager' port
        class PortCFDomainManagerOut(ExtendedCF__POA.QueryablePort):
            """This class is a port template for the PortCFDomainManagerOut_i port and
            should not be instantiated nor modified.
            
            The expectation is that the specific port implementation will extend
            from this class instead of the base CORBA class ExtendedCF__POA.QueryablePort.
            """
            pass

        # 'CF/FileManager' port
        class PortCFFileManagerOut(ExtendedCF__POA.QueryablePort):
            """This class is a port template for the PortCFFileManagerOut_i port and
            should not be instantiated nor modified.
            
            The expectation is that the specific port implementation will extend
            from this class instead of the base CORBA class ExtendedCF__POA.QueryablePort.
            """
            pass

        # 'omg.org/CosEventChannelAdmin/EventChannel' port
        class PortCosEventChannelAdminEventChannelOut(ExtendedCF__POA.QueryablePort):
            """This class is a port template for the PortCosEventChannelAdminEventChannelOut_i port and
            should not be instantiated nor modified.
            
            The expectation is that the specific port implementation will extend
            from this class instead of the base CORBA class ExtendedCF__POA.QueryablePort.
            """
            pass

        # 'CF/Resource' port
        class PortCFResourceOut(ExtendedCF__POA.QueryablePort):
            """This class is a port template for the PortCFResourceOut_i port and
            should not be instantiated nor modified.
            
            The expectation is that the specific port implementation will extend
            from this class instead of the base CORBA class ExtendedCF__POA.QueryablePort.
            """
            pass

        # 'CF/Device' port
        class PortCFDeviceOut(ExtendedCF__POA.QueryablePort):
            """This class is a port template for the PortCFDeviceOut_i port and
            should not be instantiated nor modified.
            
            The expectation is that the specific port implementation will extend
            from this class instead of the base CORBA class ExtendedCF__POA.QueryablePort.
            """
            pass

        # 'CF/AggregateExecutableDevice' port
        class PortCFAggregateExecutableDeviceOut(ExtendedCF__POA.QueryablePort):
            """This class is a port template for the PortCFAggregateExecutableDeviceOut_i port and
            should not be instantiated nor modified.
            
            The expectation is that the specific port implementation will extend
            from this class instead of the base CORBA class ExtendedCF__POA.QueryablePort.
            """
            pass

        # 'CF/ExecutableDevice' port
        class PortCFExecutableDeviceOut(ExtendedCF__POA.QueryablePort):
            """This class is a port template for the PortCFExecutableDeviceOut_i port and
            should not be instantiated nor modified.
            
            The expectation is that the specific port implementation will extend
            from this class instead of the base CORBA class ExtendedCF__POA.QueryablePort.
            """
            pass

        port_dommgr_out = usesport(name="dommgr_out",
                                   repid="IDL:CF/DomainManager:1.0",
                                   type_="control")

        port_filemgr_out = usesport(name="filemgr_out",
                                    repid="IDL:CF/FileManager:1.0",
                                    type_="control")

        port_eventchannel_out = usesport(name="eventchannel_out",
                                         repid="IDL:omg.org/CosEventChannelAdmin/EventChannel:1.0",
                                         type_="control")

        port_resource_out = usesport(name="resource_out",
                                     repid="IDL:CF/Resource:1.0",
                                     type_="control")

        port_device_out = usesport(name="device_out",
                                   repid="IDL:CF/Device:1.0",
                                   type_="control")

        port_aggdev_out = usesport(name="aggdev_out",
                                   repid="IDL:CF/AggregateExecutableDevice:1.0",
                                   type_="control")

        port_execdev_out = usesport(name="execdev_out",
                                    repid="IDL:CF/ExecutableDevice:1.0",
                                    type_="control")

        port_dataFloatBIO_out = usesport(name="dataFloatBIO_out",
                                         repid="IDL:BULKIO/dataFloat:1.0",
                                         type_="control")

        ######################################################################
        # PROPERTIES
        # 
        # DO NOT ADD NEW PROPERTIES HERE.  You can add properties in your derived class, in the PRF xml file
        # or by using the IDE.

'''uses port(s)'''

class PortCFDomainManagerOut_i(AllPortTypesTestComponent_base.PortCFDomainManagerOut):
    def __init__(self, parent, name):
        self.parent = parent
        self.name = name
        self.outConnections = {}
        self.port_lock = threading.Lock()

    def connectPort(self, connection, connectionId):
        self.port_lock.acquire()
        try:
            port = connection._narrow(CF.DomainManager)
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

    def configure(self, configProperties):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.configure(configProperties)
                    except Exception:
                        self.parent._log.exception("The call to configure failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def query(self, configProperties):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.query(configProperties)
                    except Exception:
                        self.parent._log.exception("The call to query failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def initializeProperties(self, initialProperties):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.initializeProperties(initialProperties)
                    except Exception:
                        self.parent._log.exception("The call to initializeProperties failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def registerPropertyListener(self, obj, prop_ids, interval):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.registerPropertyListener(obj, prop_ids, interval)
                    except Exception:
                        self.parent._log.exception("The call to registerPropertyListener failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def unregisterPropertyListener(self, id):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.unregisterPropertyListener(id)
                    except Exception:
                        self.parent._log.exception("The call to unregisterPropertyListener failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def registerDevice(self, registeringDevice, registeredDeviceMgr):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.registerDevice(registeringDevice, registeredDeviceMgr)
                    except Exception:
                        self.parent._log.exception("The call to registerDevice failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def registerDeviceManager(self, deviceMgr):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.registerDeviceManager(deviceMgr)
                    except Exception:
                        self.parent._log.exception("The call to registerDeviceManager failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def unregisterDeviceManager(self, deviceMgr):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.unregisterDeviceManager(deviceMgr)
                    except Exception:
                        self.parent._log.exception("The call to unregisterDeviceManager failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def unregisterDevice(self, unregisteringDevice):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.unregisterDevice(unregisteringDevice)
                    except Exception:
                        self.parent._log.exception("The call to unregisterDevice failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def createApplication(self, profileFileName, name, initConfiguration, deviceAssignments):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.createApplication(profileFileName, name, initConfiguration, deviceAssignments)
                    except Exception:
                        self.parent._log.exception("The call to createApplication failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def installApplication(self, profileFileName):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.installApplication(profileFileName)
                    except Exception:
                        self.parent._log.exception("The call to installApplication failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def uninstallApplication(self, applicationId):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.uninstallApplication(applicationId)
                    except Exception:
                        self.parent._log.exception("The call to uninstallApplication failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def registerService(self, registeringService, registeredDeviceMgr, name):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.registerService(registeringService, registeredDeviceMgr, name)
                    except Exception:
                        self.parent._log.exception("The call to registerService failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def unregisterService(self, unregisteringService, name):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.unregisterService(unregisteringService, name)
                    except Exception:
                        self.parent._log.exception("The call to unregisterService failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def registerWithEventChannel(self, registeringObject, registeringId, eventChannelName):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.registerWithEventChannel(registeringObject, registeringId, eventChannelName)
                    except Exception:
                        self.parent._log.exception("The call to registerWithEventChannel failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def unregisterFromEventChannel(self, unregisteringId, eventChannelName):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.unregisterFromEventChannel(unregisteringId, eventChannelName)
                    except Exception:
                        self.parent._log.exception("The call to unregisterFromEventChannel failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def registerRemoteDomainManager(self, registeringDomainManager):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.registerRemoteDomainManager(registeringDomainManager)
                    except Exception:
                        self.parent._log.exception("The call to registerRemoteDomainManager failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def unregisterRemoteDomainManager(self, unregisteringDomainManager):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.unregisterRemoteDomainManager(unregisteringDomainManager)
                    except Exception:
                        self.parent._log.exception("The call to unregisterRemoteDomainManager failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def _get_domainManagerProfile(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_domainManagerProfile()
                    except Exception:
                        self.parent._log.exception("The call to _get_domainManagerProfile failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_deviceManagers(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_deviceManagers()
                    except Exception:
                        self.parent._log.exception("The call to _get_deviceManagers failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_applications(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_applications()
                    except Exception:
                        self.parent._log.exception("The call to _get_applications failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_applicationFactories(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_applicationFactories()
                    except Exception:
                        self.parent._log.exception("The call to _get_applicationFactories failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_fileMgr(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_fileMgr()
                    except Exception:
                        self.parent._log.exception("The call to _get_fileMgr failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_allocationMgr(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_allocationMgr()
                    except Exception:
                        self.parent._log.exception("The call to _get_allocationMgr failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_connectionMgr(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_connectionMgr()
                    except Exception:
                        self.parent._log.exception("The call to _get_connectionMgr failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_eventChannelMgr(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_eventChannelMgr()
                    except Exception:
                        self.parent._log.exception("The call to _get_eventChannelMgr failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_identifier(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_identifier()
                    except Exception:
                        self.parent._log.exception("The call to _get_identifier failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_name(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_name()
                    except Exception:
                        self.parent._log.exception("The call to _get_name failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_remoteDomainManagers(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_remoteDomainManagers()
                    except Exception:
                        self.parent._log.exception("The call to _get_remoteDomainManagers failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

class PortCFFileManagerOut_i(AllPortTypesTestComponent_base.PortCFFileManagerOut):
    def __init__(self, parent, name):
        self.parent = parent
        self.name = name
        self.outConnections = {}
        self.port_lock = threading.Lock()

    def connectPort(self, connection, connectionId):
        self.port_lock.acquire()
        try:
            port = connection._narrow(CF.FileManager)
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

    def remove(self, fileName):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.remove(fileName)
                    except Exception:
                        self.parent._log.exception("The call to remove failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def copy(self, sourceFileName, destinationFileName):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.copy(sourceFileName, destinationFileName)
                    except Exception:
                        self.parent._log.exception("The call to copy failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def move(self, sourceFileName, destinationFileName):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.move(sourceFileName, destinationFileName)
                    except Exception:
                        self.parent._log.exception("The call to move failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def exists(self, fileName):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.exists(fileName)
                    except Exception:
                        self.parent._log.exception("The call to exists failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def list(self, pattern):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.list(pattern)
                    except Exception:
                        self.parent._log.exception("The call to list failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def create(self, fileName):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.create(fileName)
                    except Exception:
                        self.parent._log.exception("The call to create failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def open(self, fileName, read_Only):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.open(fileName, read_Only)
                    except Exception:
                        self.parent._log.exception("The call to open failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def mkdir(self, directoryName):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.mkdir(directoryName)
                    except Exception:
                        self.parent._log.exception("The call to mkdir failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def rmdir(self, directoryName):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.rmdir(directoryName)
                    except Exception:
                        self.parent._log.exception("The call to rmdir failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def query(self, fileSystemProperties):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.query(fileSystemProperties)
                    except Exception:
                        self.parent._log.exception("The call to query failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def mount(self, mountPoint, file_System):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.mount(mountPoint, file_System)
                    except Exception:
                        self.parent._log.exception("The call to mount failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def unmount(self, mountPoint):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.unmount(mountPoint)
                    except Exception:
                        self.parent._log.exception("The call to unmount failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def getMounts(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.getMounts()
                    except Exception:
                        self.parent._log.exception("The call to getMounts failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

class PortCosEventChannelAdminEventChannelOut_i(AllPortTypesTestComponent_base.PortCosEventChannelAdminEventChannelOut):
    def __init__(self, parent, name):
        self.parent = parent
        self.name = name
        self.outConnections = {}
        self.port_lock = threading.Lock()

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

    def for_consumers(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.for_consumers()
                    except Exception:
                        self.parent._log.exception("The call to for_consumers failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def for_suppliers(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.for_suppliers()
                    except Exception:
                        self.parent._log.exception("The call to for_suppliers failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def destroy(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.destroy()
                    except Exception:
                        self.parent._log.exception("The call to destroy failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

class PortCFResourceOut_i(AllPortTypesTestComponent_base.PortCFResourceOut):
    def __init__(self, parent, name):
        self.parent = parent
        self.name = name
        self.outConnections = {}
        self.port_lock = threading.Lock()

    def connectPort(self, connection, connectionId):
        self.port_lock.acquire()
        try:
            port = connection._narrow(CF.Resource)
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

    def initialize(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.initialize()
                    except Exception:
                        self.parent._log.exception("The call to initialize failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def releaseObject(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.releaseObject()
                    except Exception:
                        self.parent._log.exception("The call to releaseObject failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def runTest(self, testid, testValues):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.runTest(testid, testValues)
                    except Exception:
                        self.parent._log.exception("The call to runTest failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def configure(self, configProperties):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.configure(configProperties)
                    except Exception:
                        self.parent._log.exception("The call to configure failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def query(self, configProperties):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.query(configProperties)
                    except Exception:
                        self.parent._log.exception("The call to query failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def initializeProperties(self, initialProperties):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.initializeProperties(initialProperties)
                    except Exception:
                        self.parent._log.exception("The call to initializeProperties failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def registerPropertyListener(self, obj, prop_ids, interval):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.registerPropertyListener(obj, prop_ids, interval)
                    except Exception:
                        self.parent._log.exception("The call to registerPropertyListener failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def unregisterPropertyListener(self, id):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.unregisterPropertyListener(id)
                    except Exception:
                        self.parent._log.exception("The call to unregisterPropertyListener failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def getPort(self, name):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.getPort(name)
                    except Exception:
                        self.parent._log.exception("The call to getPort failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def getPortSet(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.getPortSet()
                    except Exception:
                        self.parent._log.exception("The call to getPortSet failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def retrieve_records(self, howMany, startingRecord):
        retVal = []
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.retrieve_records(howMany, startingRecord)
                    except Exception:
                        self.parent._log.exception("The call to retrieve_records failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def retrieve_records_by_date(self, howMany, to_timeStamp):
        retVal = []
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.retrieve_records_by_date(howMany, to_timeStamp)
                    except Exception:
                        self.parent._log.exception("The call to retrieve_records_by_date failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def retrieve_records_from_date(self, howMany, from_timeStamp):
        retVal = []
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.retrieve_records_from_date(howMany, from_timeStamp)
                    except Exception:
                        self.parent._log.exception("The call to retrieve_records_from_date failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def setLogLevel(self, logger_id, newLevel):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.setLogLevel(logger_id, newLevel)
                    except Exception:
                        self.parent._log.exception("The call to setLogLevel failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def getLogConfig(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.getLogConfig()
                    except Exception:
                        self.parent._log.exception("The call to getLogConfig failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def setLogConfig(self, config_contents):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.setLogConfig(config_contents)
                    except Exception:
                        self.parent._log.exception("The call to setLogConfig failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def setLogConfigURL(self, config_url):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.setLogConfigURL(config_url)
                    except Exception:
                        self.parent._log.exception("The call to setLogConfigURL failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def start(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.start()
                    except Exception:
                        self.parent._log.exception("The call to start failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def stop(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.stop()
                    except Exception:
                        self.parent._log.exception("The call to stop failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def _get_log_level(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_log_level()
                    except Exception:
                        self.parent._log.exception("The call to _get_log_level failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _set_log_level(self, data):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port._set_log_level(data)
                    except Exception:
                        self.parent._log.exception("The call to _set_log_level failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def _get_identifier(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_identifier()
                    except Exception:
                        self.parent._log.exception("The call to _get_identifier failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_started(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_started()
                    except Exception:
                        self.parent._log.exception("The call to _get_started failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_softwareProfile(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_softwareProfile()
                    except Exception:
                        self.parent._log.exception("The call to _get_softwareProfile failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

class PortCFDeviceOut_i(AllPortTypesTestComponent_base.PortCFDeviceOut):
    def __init__(self, parent, name):
        self.parent = parent
        self.name = name
        self.outConnections = {}
        self.port_lock = threading.Lock()

    def connectPort(self, connection, connectionId):
        self.port_lock.acquire()
        try:
            port = connection._narrow(CF.Device)
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

    def initialize(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.initialize()
                    except Exception:
                        self.parent._log.exception("The call to initialize failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def releaseObject(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.releaseObject()
                    except Exception:
                        self.parent._log.exception("The call to releaseObject failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def runTest(self, testid, testValues):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.runTest(testid, testValues)
                    except Exception:
                        self.parent._log.exception("The call to runTest failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def configure(self, configProperties):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.configure(configProperties)
                    except Exception:
                        self.parent._log.exception("The call to configure failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def query(self, configProperties):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.query(configProperties)
                    except Exception:
                        self.parent._log.exception("The call to query failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def initializeProperties(self, initialProperties):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.initializeProperties(initialProperties)
                    except Exception:
                        self.parent._log.exception("The call to initializeProperties failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def registerPropertyListener(self, obj, prop_ids, interval):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.registerPropertyListener(obj, prop_ids, interval)
                    except Exception:
                        self.parent._log.exception("The call to registerPropertyListener failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def unregisterPropertyListener(self, id):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.unregisterPropertyListener(id)
                    except Exception:
                        self.parent._log.exception("The call to unregisterPropertyListener failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def getPort(self, name):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.getPort(name)
                    except Exception:
                        self.parent._log.exception("The call to getPort failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def getPortSet(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.getPortSet()
                    except Exception:
                        self.parent._log.exception("The call to getPortSet failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def retrieve_records(self, howMany, startingRecord):
        retVal = []
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.retrieve_records(howMany, startingRecord)
                    except Exception:
                        self.parent._log.exception("The call to retrieve_records failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def retrieve_records_by_date(self, howMany, to_timeStamp):
        retVal = []
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.retrieve_records_by_date(howMany, to_timeStamp)
                    except Exception:
                        self.parent._log.exception("The call to retrieve_records_by_date failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def retrieve_records_from_date(self, howMany, from_timeStamp):
        retVal = []
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.retrieve_records_from_date(howMany, from_timeStamp)
                    except Exception:
                        self.parent._log.exception("The call to retrieve_records_from_date failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def setLogLevel(self, logger_id, newLevel):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.setLogLevel(logger_id, newLevel)
                    except Exception:
                        self.parent._log.exception("The call to setLogLevel failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def getLogConfig(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.getLogConfig()
                    except Exception:
                        self.parent._log.exception("The call to getLogConfig failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def setLogConfig(self, config_contents):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.setLogConfig(config_contents)
                    except Exception:
                        self.parent._log.exception("The call to setLogConfig failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def setLogConfigURL(self, config_url):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.setLogConfigURL(config_url)
                    except Exception:
                        self.parent._log.exception("The call to setLogConfigURL failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def start(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.start()
                    except Exception:
                        self.parent._log.exception("The call to start failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def stop(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.stop()
                    except Exception:
                        self.parent._log.exception("The call to stop failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def allocateCapacity(self, capacities):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.allocateCapacity(capacities)
                    except Exception:
                        self.parent._log.exception("The call to allocateCapacity failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def deallocateCapacity(self, capacities):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.deallocateCapacity(capacities)
                    except Exception:
                        self.parent._log.exception("The call to deallocateCapacity failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def _get_log_level(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_log_level()
                    except Exception:
                        self.parent._log.exception("The call to _get_log_level failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _set_log_level(self, data):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port._set_log_level(data)
                    except Exception:
                        self.parent._log.exception("The call to _set_log_level failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def _get_identifier(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_identifier()
                    except Exception:
                        self.parent._log.exception("The call to _get_identifier failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_started(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_started()
                    except Exception:
                        self.parent._log.exception("The call to _get_started failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_softwareProfile(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_softwareProfile()
                    except Exception:
                        self.parent._log.exception("The call to _get_softwareProfile failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_usageState(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_usageState()
                    except Exception:
                        self.parent._log.exception("The call to _get_usageState failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_adminState(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_adminState()
                    except Exception:
                        self.parent._log.exception("The call to _get_adminState failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _set_adminState(self, data):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port._set_adminState(data)
                    except Exception:
                        self.parent._log.exception("The call to _set_adminState failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def _get_operationalState(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_operationalState()
                    except Exception:
                        self.parent._log.exception("The call to _get_operationalState failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_label(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_label()
                    except Exception:
                        self.parent._log.exception("The call to _get_label failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_compositeDevice(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_compositeDevice()
                    except Exception:
                        self.parent._log.exception("The call to _get_compositeDevice failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

class PortCFAggregateExecutableDeviceOut_i(AllPortTypesTestComponent_base.PortCFAggregateExecutableDeviceOut):
    def __init__(self, parent, name):
        self.parent = parent
        self.name = name
        self.outConnections = {}
        self.port_lock = threading.Lock()

    def connectPort(self, connection, connectionId):
        self.port_lock.acquire()
        try:
            port = connection._narrow(CF.AggregateExecutableDevice)
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

    def initialize(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.initialize()
                    except Exception:
                        self.parent._log.exception("The call to initialize failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def releaseObject(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.releaseObject()
                    except Exception:
                        self.parent._log.exception("The call to releaseObject failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def runTest(self, testid, testValues):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.runTest(testid, testValues)
                    except Exception:
                        self.parent._log.exception("The call to runTest failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def configure(self, configProperties):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.configure(configProperties)
                    except Exception:
                        self.parent._log.exception("The call to configure failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def query(self, configProperties):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.query(configProperties)
                    except Exception:
                        self.parent._log.exception("The call to query failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def initializeProperties(self, initialProperties):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.initializeProperties(initialProperties)
                    except Exception:
                        self.parent._log.exception("The call to initializeProperties failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def registerPropertyListener(self, obj, prop_ids, interval):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.registerPropertyListener(obj, prop_ids, interval)
                    except Exception:
                        self.parent._log.exception("The call to registerPropertyListener failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def unregisterPropertyListener(self, id):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.unregisterPropertyListener(id)
                    except Exception:
                        self.parent._log.exception("The call to unregisterPropertyListener failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def getPort(self, name):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.getPort(name)
                    except Exception:
                        self.parent._log.exception("The call to getPort failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def getPortSet(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.getPortSet()
                    except Exception:
                        self.parent._log.exception("The call to getPortSet failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def retrieve_records(self, howMany, startingRecord):
        retVal = []
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.retrieve_records(howMany, startingRecord)
                    except Exception:
                        self.parent._log.exception("The call to retrieve_records failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def retrieve_records_by_date(self, howMany, to_timeStamp):
        retVal = []
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.retrieve_records_by_date(howMany, to_timeStamp)
                    except Exception:
                        self.parent._log.exception("The call to retrieve_records_by_date failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def retrieve_records_from_date(self, howMany, from_timeStamp):
        retVal = []
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.retrieve_records_from_date(howMany, from_timeStamp)
                    except Exception:
                        self.parent._log.exception("The call to retrieve_records_from_date failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def setLogLevel(self, logger_id, newLevel):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.setLogLevel(logger_id, newLevel)
                    except Exception:
                        self.parent._log.exception("The call to setLogLevel failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def getLogConfig(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.getLogConfig()
                    except Exception:
                        self.parent._log.exception("The call to getLogConfig failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def setLogConfig(self, config_contents):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.setLogConfig(config_contents)
                    except Exception:
                        self.parent._log.exception("The call to setLogConfig failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def setLogConfigURL(self, config_url):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.setLogConfigURL(config_url)
                    except Exception:
                        self.parent._log.exception("The call to setLogConfigURL failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def start(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.start()
                    except Exception:
                        self.parent._log.exception("The call to start failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def stop(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.stop()
                    except Exception:
                        self.parent._log.exception("The call to stop failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def allocateCapacity(self, capacities):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.allocateCapacity(capacities)
                    except Exception:
                        self.parent._log.exception("The call to allocateCapacity failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def deallocateCapacity(self, capacities):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.deallocateCapacity(capacities)
                    except Exception:
                        self.parent._log.exception("The call to deallocateCapacity failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def load(self, fs, fileName, loadKind):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.load(fs, fileName, loadKind)
                    except Exception:
                        self.parent._log.exception("The call to load failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def unload(self, fileName):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.unload(fileName)
                    except Exception:
                        self.parent._log.exception("The call to unload failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def terminate(self, processId):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.terminate(processId)
                    except Exception:
                        self.parent._log.exception("The call to terminate failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def execute(self, name, options, parameters):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.execute(name, options, parameters)
                    except Exception:
                        self.parent._log.exception("The call to execute failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def executeLinked(self, name, options, parameters, deps):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.executeLinked(name, options, parameters, deps)
                    except Exception:
                        self.parent._log.exception("The call to executeLinked failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def addDevice(self, associatedDevice):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.addDevice(associatedDevice)
                    except Exception:
                        self.parent._log.exception("The call to addDevice failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def removeDevice(self, associatedDevice):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.removeDevice(associatedDevice)
                    except Exception:
                        self.parent._log.exception("The call to removeDevice failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def _get_log_level(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_log_level()
                    except Exception:
                        self.parent._log.exception("The call to _get_log_level failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _set_log_level(self, data):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port._set_log_level(data)
                    except Exception:
                        self.parent._log.exception("The call to _set_log_level failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def _get_identifier(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_identifier()
                    except Exception:
                        self.parent._log.exception("The call to _get_identifier failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_started(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_started()
                    except Exception:
                        self.parent._log.exception("The call to _get_started failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_softwareProfile(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_softwareProfile()
                    except Exception:
                        self.parent._log.exception("The call to _get_softwareProfile failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_usageState(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_usageState()
                    except Exception:
                        self.parent._log.exception("The call to _get_usageState failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_adminState(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_adminState()
                    except Exception:
                        self.parent._log.exception("The call to _get_adminState failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _set_adminState(self, data):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port._set_adminState(data)
                    except Exception:
                        self.parent._log.exception("The call to _set_adminState failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def _get_operationalState(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_operationalState()
                    except Exception:
                        self.parent._log.exception("The call to _get_operationalState failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_label(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_label()
                    except Exception:
                        self.parent._log.exception("The call to _get_label failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_compositeDevice(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_compositeDevice()
                    except Exception:
                        self.parent._log.exception("The call to _get_compositeDevice failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_devices(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_devices()
                    except Exception:
                        self.parent._log.exception("The call to _get_devices failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

class PortCFExecutableDeviceOut_i(AllPortTypesTestComponent_base.PortCFExecutableDeviceOut):
    def __init__(self, parent, name):
        self.parent = parent
        self.name = name
        self.outConnections = {}
        self.port_lock = threading.Lock()

    def connectPort(self, connection, connectionId):
        self.port_lock.acquire()
        try:
            port = connection._narrow(CF.ExecutableDevice)
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

    def initialize(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.initialize()
                    except Exception:
                        self.parent._log.exception("The call to initialize failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def releaseObject(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.releaseObject()
                    except Exception:
                        self.parent._log.exception("The call to releaseObject failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def runTest(self, testid, testValues):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.runTest(testid, testValues)
                    except Exception:
                        self.parent._log.exception("The call to runTest failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def configure(self, configProperties):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.configure(configProperties)
                    except Exception:
                        self.parent._log.exception("The call to configure failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def query(self, configProperties):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.query(configProperties)
                    except Exception:
                        self.parent._log.exception("The call to query failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def initializeProperties(self, initialProperties):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.initializeProperties(initialProperties)
                    except Exception:
                        self.parent._log.exception("The call to initializeProperties failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def registerPropertyListener(self, obj, prop_ids, interval):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.registerPropertyListener(obj, prop_ids, interval)
                    except Exception:
                        self.parent._log.exception("The call to registerPropertyListener failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def unregisterPropertyListener(self, id):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.unregisterPropertyListener(id)
                    except Exception:
                        self.parent._log.exception("The call to unregisterPropertyListener failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def getPort(self, name):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.getPort(name)
                    except Exception:
                        self.parent._log.exception("The call to getPort failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def getPortSet(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.getPortSet()
                    except Exception:
                        self.parent._log.exception("The call to getPortSet failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def retrieve_records(self, howMany, startingRecord):
        retVal = []
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.retrieve_records(howMany, startingRecord)
                    except Exception:
                        self.parent._log.exception("The call to retrieve_records failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def retrieve_records_by_date(self, howMany, to_timeStamp):
        retVal = []
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.retrieve_records_by_date(howMany, to_timeStamp)
                    except Exception:
                        self.parent._log.exception("The call to retrieve_records_by_date failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def retrieve_records_from_date(self, howMany, from_timeStamp):
        retVal = []
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.retrieve_records_from_date(howMany, from_timeStamp)
                    except Exception:
                        self.parent._log.exception("The call to retrieve_records_from_date failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def setLogLevel(self, logger_id, newLevel):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.setLogLevel(logger_id, newLevel)
                    except Exception:
                        self.parent._log.exception("The call to setLogLevel failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def getLogConfig(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.getLogConfig()
                    except Exception:
                        self.parent._log.exception("The call to getLogConfig failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def setLogConfig(self, config_contents):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.setLogConfig(config_contents)
                    except Exception:
                        self.parent._log.exception("The call to setLogConfig failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def setLogConfigURL(self, config_url):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.setLogConfigURL(config_url)
                    except Exception:
                        self.parent._log.exception("The call to setLogConfigURL failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def start(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.start()
                    except Exception:
                        self.parent._log.exception("The call to start failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def stop(self):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.stop()
                    except Exception:
                        self.parent._log.exception("The call to stop failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def allocateCapacity(self, capacities):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.allocateCapacity(capacities)
                    except Exception:
                        self.parent._log.exception("The call to allocateCapacity failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def deallocateCapacity(self, capacities):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.deallocateCapacity(capacities)
                    except Exception:
                        self.parent._log.exception("The call to deallocateCapacity failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def load(self, fs, fileName, loadKind):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.load(fs, fileName, loadKind)
                    except Exception:
                        self.parent._log.exception("The call to load failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def unload(self, fileName):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.unload(fileName)
                    except Exception:
                        self.parent._log.exception("The call to unload failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def terminate(self, processId):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port.terminate(processId)
                    except Exception:
                        self.parent._log.exception("The call to terminate failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def execute(self, name, options, parameters):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.execute(name, options, parameters)
                    except Exception:
                        self.parent._log.exception("The call to execute failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def executeLinked(self, name, options, parameters, deps):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port.executeLinked(name, options, parameters, deps)
                    except Exception:
                        self.parent._log.exception("The call to executeLinked failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_log_level(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_log_level()
                    except Exception:
                        self.parent._log.exception("The call to _get_log_level failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _set_log_level(self, data):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port._set_log_level(data)
                    except Exception:
                        self.parent._log.exception("The call to _set_log_level failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def _get_identifier(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_identifier()
                    except Exception:
                        self.parent._log.exception("The call to _get_identifier failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_started(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_started()
                    except Exception:
                        self.parent._log.exception("The call to _get_started failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_softwareProfile(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_softwareProfile()
                    except Exception:
                        self.parent._log.exception("The call to _get_softwareProfile failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_usageState(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_usageState()
                    except Exception:
                        self.parent._log.exception("The call to _get_usageState failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_adminState(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_adminState()
                    except Exception:
                        self.parent._log.exception("The call to _get_adminState failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _set_adminState(self, data):
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        port._set_adminState(data)
                    except Exception:
                        self.parent._log.exception("The call to _set_adminState failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

    def _get_operationalState(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_operationalState()
                    except Exception:
                        self.parent._log.exception("The call to _get_operationalState failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_label(self):
        retVal = ""
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_label()
                    except Exception:
                        self.parent._log.exception("The call to _get_label failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal

    def _get_compositeDevice(self):
        retVal = None
        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                if port != None:
                    try:
                        retVal = port._get_compositeDevice()
                    except Exception:
                        self.parent._log.exception("The call to _get_compositeDevice failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.port_lock.release()

        return retVal


