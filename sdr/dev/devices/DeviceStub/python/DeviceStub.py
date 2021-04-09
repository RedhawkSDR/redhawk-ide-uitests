#!/usr/bin/env python
#
#
# AUTO-GENERATED
#
# Source: DeviceStub.spd.xml
from ossie.device import start_device
import logging

from DeviceStub_base import *

class DeviceStub_i(DeviceStub_base):
    """<DESCRIPTION GOES HERE>"""
    def initialize(self):
        DeviceStub_base.initialize(self)
        self.sampleSRIPushed = False

    def updateUsageState(self):
        return NOOP

    def process(self):
        packet = self.port_dataFloat_in.getPacket()
        if packet.dataBuffer is None:
            # Slow pushes of small sample data
            if not self.sampleSRIPushed:
                self.sampleSRIPushed = True
                self.port_dataFloat_out.pushSRI(bulkio.sri.create("DeviceStub_sample"))
            outData = [ 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0 ]
            self.port_dataFloat_out.pushPacket(outData, bulkio.timestamp.now(), False, "DeviceStub_sample")
            return NOOP

        # Feed through existing data
        if packet.sriChanged:
            self.port_dataFloat_out.pushSRI(packet.SRI)
        self.port_dataFloat_out.pushPacket(packet.dataBuffer, packet.T, packet.EOS, packet.streamID)
        return NORMAL

  
if __name__ == '__main__':
    logging.getLogger().setLevel(logging.INFO)
    logging.debug("Starting Device")
    start_device(DeviceStub_i)

