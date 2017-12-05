#!/usr/bin/env python
#
# AUTO-GENERATED CODE.  DO NOT MODIFY!
#
# Source: RX_Digitizer_Sim.spd.xml
from ossie.cf import CF
from ossie.cf import CF__POA
from ossie.utils import uuid

from frontend import FrontendTunerDevice
from frontend import digital_tuner_delegation
from frontend import rfinfo_delegation
from ossie.threadedcomponent import *
from ossie.properties import simple_property
from ossie.properties import simpleseq_property
from ossie.properties import struct_property
from ossie.properties import structseq_property
from ossie.properties import struct_to_props

import Queue, copy, time, threading
from ossie.resource import usesport, providesport
import bulkio
import frontend
import inspect
from frontend import FRONTEND
BOOLEAN_VALUE_HERE=False

class RX_Digitizer_Sim_base(CF__POA.Device, FrontendTunerDevice, digital_tuner_delegation, rfinfo_delegation, ThreadedComponent):
        # These values can be altered in the __init__ of your derived class

        PAUSE = 0.0125 # The amount of time to sleep if process return NOOP
        TIMEOUT = 5.0 # The amount of time to wait for the process thread to die when stop() is called
        DEFAULT_QUEUE_SIZE = 100 # The number of BulkIO packets that can be in the queue before pushPacket will block

        def __init__(self, devmgr, uuid, label, softwareProfile, compositeDevice, execparams):
            FrontendTunerDevice.__init__(self, devmgr, uuid, label, softwareProfile, compositeDevice, execparams)
            ThreadedComponent.__init__(self)

            self.listeners={}
            # self.auto_start is deprecated and is only kept for API compatibility
            # with 1.7.X and 1.8.0 devices.  This variable may be removed
            # in future releases
            self.auto_start = False
            # Instantiate the default implementations for all ports on this device
            self.port_RFInfo_in = frontend.InRFInfoPort("RFInfo_in", self)
            self.port_DigitalTuner_in = frontend.InDigitalTunerPort("DigitalTuner_in", self)
            self.port_dataShort_out = bulkio.OutShortPort("dataShort_out",logger=self._log)
            self.addPropertyChangeListener('connectionTable',self.updated_connectionTable)
            self.device_kind = "FRONTEND::TUNER"
            self.frontend_listener_allocation = frontend.fe_types.frontend_listener_allocation()
            self.frontend_tuner_allocation = frontend.fe_types.frontend_tuner_allocation()

        def start(self):
            FrontendTunerDevice.start(self)
            ThreadedComponent.startThread(self, pause=self.PAUSE)

        def stop(self):
            FrontendTunerDevice.stop(self)
            if not ThreadedComponent.stopThread(self, self.TIMEOUT):
                raise CF.Resource.StopError(CF.CF_NOTSET, "Processing thread did not die")

        def updated_connectionTable(self, id, oldval, newval):
            self.port_dataShort_out.updateConnectionFilter(newval)

        def releaseObject(self):
            try:
                self.stop()
            except Exception:
                self._log.exception("Error stopping")
            FrontendTunerDevice.releaseObject(self)

        ######################################################################
        # PORTS
        # 
        # DO NOT ADD NEW PORTS HERE.  You can add ports in your derived class, in the SCD xml file, 
        # or via the IDE.

        port_RFInfo_in = providesport(name="RFInfo_in",
                                      repid="IDL:FRONTEND/RFInfo:1.0",
                                      type_="data")

        port_DigitalTuner_in = providesport(name="DigitalTuner_in",
                                            repid="IDL:FRONTEND/DigitalTuner:1.0",
                                            type_="control")

        port_dataShort_out = usesport(name="dataShort_out",
                                      repid="IDL:BULKIO/dataShort:1.0",
                                      type_="data")

        ######################################################################
        # PROPERTIES
        # 
        # DO NOT ADD NEW PROPERTIES HERE.  You can add properties in your derived class, in the PRF xml file
        # or by using the IDE.
        class frontend_tuner_status_struct_struct(frontend.default_frontend_tuner_status_struct_struct):
            available_bandwidth = simple_property(
                                                  id_="FRONTEND::tuner_status::available_bandwidth",
                                                  name="available_bandwidth",
                                                  type_="string")
        
            available_frequency = simple_property(
                                                  id_="FRONTEND::tuner_status::available_frequency",
                                                  name="available_frequency",
                                                  type_="string")
        
            available_gain = simple_property(
                                             id_="FRONTEND::tuner_status::available_gain",
                                             name="available_gain",
                                             type_="string")
        
            available_sample_rate = simple_property(
                                                    id_="FRONTEND::tuner_status::available_sample_rate",
                                                    name="available_sample_rate",
                                                    type_="string")
        
            decimation = simple_property(
                                         id_="FRONTEND::tuner_status::decimation",
                                         name="decimation",
                                         type_="long")
        
            gain = simple_property(
                                   id_="FRONTEND::tuner_status::gain",
                                   name="gain",
                                   type_="double")
        
            tuner_number = simple_property(
                                           id_="FRONTEND::tuner_status::tuner_number",
                                           name="tuner_number",
                                           type_="short")
        
            waveform_type = simple_property(
                                            id_="FRONTEND::tuner_status::waveform_type",
                                            name="waveform_type",
                                            type_="string")
        
            def __init__(self, allocation_id_csv="", available_bandwidth="", available_frequency="", available_gain="", available_sample_rate="", bandwidth=0.0, center_frequency=0.0, decimation=0, enabled=False, gain=0.0, group_id="", rf_flow_id="", sample_rate=0.0, tuner_number=0, tuner_type="", waveform_type=""):
                frontend.default_frontend_tuner_status_struct_struct.__init__(self, allocation_id_csv=allocation_id_csv, bandwidth=bandwidth, center_frequency=center_frequency, enabled=enabled, group_id=group_id, rf_flow_id=rf_flow_id, sample_rate=sample_rate, tuner_type=tuner_type)
                self.available_bandwidth = available_bandwidth
                self.available_frequency = available_frequency
                self.available_gain = available_gain
                self.available_sample_rate = available_sample_rate
                self.decimation = decimation
                self.gain = gain
                self.tuner_number = tuner_number
                self.waveform_type = waveform_type
        
            def __str__(self):
                """Return a string representation of this structure"""
                d = {}
                d["allocation_id_csv"] = self.allocation_id_csv
                d["available_bandwidth"] = self.available_bandwidth
                d["available_frequency"] = self.available_frequency
                d["available_gain"] = self.available_gain
                d["available_sample_rate"] = self.available_sample_rate
                d["bandwidth"] = self.bandwidth
                d["center_frequency"] = self.center_frequency
                d["decimation"] = self.decimation
                d["enabled"] = self.enabled
                d["gain"] = self.gain
                d["group_id"] = self.group_id
                d["rf_flow_id"] = self.rf_flow_id
                d["sample_rate"] = self.sample_rate
                d["tuner_number"] = self.tuner_number
                d["tuner_type"] = self.tuner_type
                d["waveform_type"] = self.waveform_type
                return str(d)
        
            @classmethod
            def getId(cls):
                return "FRONTEND::tuner_status_struct"
        
            @classmethod
            def isStruct(cls):
                return True
        
            def getMembers(self):
                return frontend.default_frontend_tuner_status_struct_struct.getMembers(self) + [("available_bandwidth",self.available_bandwidth),("available_frequency",self.available_frequency),("available_gain",self.available_gain),("available_sample_rate",self.available_sample_rate),("decimation",self.decimation),("gain",self.gain),("tuner_number",self.tuner_number),("waveform_type",self.waveform_type)]

        connectionTable = structseq_property(id_="connectionTable",
                                             structdef=bulkio.connection_descriptor_struct,
                                             defvalue=[],
                                             configurationkind=("property",),
                                             mode="readonly")



        # Rebind tuner status property with custom struct definition
        frontend_tuner_status = FrontendTunerDevice.frontend_tuner_status.rebind()
        frontend_tuner_status.structdef = frontend_tuner_status_struct_struct

        def frontendTunerStatusChanged(self,oldValue, newValue):
            pass

        def getTunerStatus(self,allocation_id):
            tuner_id = self.getTunerMapping(allocation_id)
            if tuner_id < 0:
                raise FRONTEND.FrontendException(("ERROR: ID: " + str(allocation_id) + " IS NOT ASSOCIATED WITH ANY TUNER!"))

            tuner_status = self.frontend_tuner_status[tuner_id]

            props = struct_to_props(self.frontend_tuner_status[tuner_id])

            return struct_to_props(self.frontend_tuner_status[tuner_id])
            #return [CF.DataType(id=self.frontend_tuner_status[tuner_id].getId(),value=self.frontend_tuner_status[tuner_id]._toAny())]

        def assignListener(self,listen_alloc_id, allocation_id):
            # find control allocation_id
            existing_alloc_id = allocation_id
            if self.listeners.has_key(existing_alloc_id):
                existing_alloc_id = self.listeners[existing_alloc_id]
            self.listeners[listen_alloc_id] = existing_alloc_id

            old_table = self.connectionTable
            new_entries = []
            for entry in self.connectionTable:
                if entry.connection_id == existing_alloc_id:
                    tmp = bulkio.connection_descriptor_struct()
                    tmp.connection_id = listen_alloc_id
                    tmp.stream_id = entry.stream_id
                    tmp.port_name = entry.port_name
                    new_entries.append(tmp)

            for new_entry in new_entries:
                foundEntry = False
                for entry in self.connectionTable:
                    if entry.connection_id == new_entry.connection_id and \
                       entry.stream_id == entry.stream_id and \
                       entry.port_name == entry.port_name:
                        foundEntry = True
                        break

                if not foundEntry:
                    self.connectionTable.append(new_entry)

            self.connectionTableChanged(old_table, self.connectionTable)

        def connectionTableChanged(self, oldValue, newValue):
            self.port_dataShort_out.updateConnectionFilter(newValue)

        def removeListener(self,listen_alloc_id):
            if self.listeners.has_key(listen_alloc_id):
                del self.listeners[listen_alloc_id]

            old_table = self.connectionTable
            for entry in list(self.connectionTable):
                if entry.connection_id == listen_alloc_id:
                    self.connectionTable.remove(entry)

            # Check to see if port "port_dataShort_out" has a connection for this listener
            tmp = self.port_dataShort_out._get_connections()
            for i in range(len(self.port_dataShort_out._get_connections())):
                connection_id = tmp[i].connectionId
                if connection_id == listen_alloc_id:
                    self.port_dataShort_out.disconnectPort(connection_id)
            self.connectionTableChanged(old_table, self.connectionTable)


        def removeAllocationIdRouting(self,tuner_id):
            allocation_id = self.getControlAllocationId(tuner_id)
            old_table = self.connectionTable
            for entry in list(self.connectionTable):
                if entry.connection_id == allocation_id:
                    self.connectionTable.remove(entry)

            for key,value in self.listeners.items():
                if (value == allocation_id):
                    for entry in list(self.connectionTable):
                        if entry.connection_id == key:
                            self.connectionTable.remove(entry)

            self.connectionTableChanged(old_table, self.connectionTable)

        def removeStreamIdRouting(self,stream_id, allocation_id):
            old_table = self.connectionTable
            for entry in list(self.connectionTable):
                if allocation_id == "":
                    if entry.stream_id == stream_id:
                        self.connectionTable.remove(entry)
                else:
                    if entry.stream_id == stream_id and entry.connection_id == allocation_id:
                        self.connectionTable.remove(entry)

            for key,value in self.listeners.items():
                if (value == allocation_id):
                    for entry in list(self.connectionTable):
                        if entry.connection_id == key and entry.stream_id == stream_id:
                            self.connectionTable.remove(entry)

            self.connectionTableChanged(old_table, self.connectionTable)

        def matchAllocationIdToStreamId(self,allocation_id, stream_id, port_name):
            if port_name != "":
                for entry in list(self.connectionTable):
                    if entry.port_name != port_name:
                        continue
                    if entry.stream_id != stream_id:
                        continue
                    if entry.connection_id != allocation_id:
                        continue
                    # all three match. This is a repeat
                    return

                old_table = self.connectionTable;
                tmp = bulkio.connection_descriptor_struct()
                tmp.connection_id = allocation_id
                tmp.port_name = port_name
                tmp.stream_id = stream_id
                self.connectionTable.append(tmp)
                self.connectionTableChanged(old_table, self.connectionTable)
                return

            old_table = self.connectionTable;
            tmp = bulkio.connection_descriptor_struct()
            tmp.connection_id = allocation_id
            tmp.port_name = "port_dataShort_out"
            tmp.stream_id = stream_id
            self.connectionTable.append(tmp)
            self.connectionTableChanged(old_table, self.connectionTable)

