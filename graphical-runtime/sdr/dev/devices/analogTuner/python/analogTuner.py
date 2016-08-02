#!/usr/bin/env python
#
#
# AUTO-GENERATED
#
# Source: analogTuner.spd.xml
from ossie.device import start_device
import logging

from analogTuner_base import *

class analogTuner_i(analogTuner_base):
    def constructor(self):
        self.setNumChannels(1, "RX_DIGITIZER");
        
    def process(self):
        self._log.debug("process() example log message")
        return NOOP

    def deviceEnable(self, fts, tuner_id):
        fts.enabled = True
        return

    def deviceDisable(self,fts, tuner_id):
        fts.enabled = False
        return

    def deviceSetTuning(self,request, fts, tuner_id):
        return True

    def deviceDeleteTuning(self, fts, tuner_id):
        return True

    def getTunerType(self,allocation_id):
        idx = self.getTunerMapping(allocation_id)
        if idx < 0: raise FRONTEND.FrontendException("Invalid allocation id")
        return self.frontend_tuner_status[idx].tuner_type

    def getTunerDeviceControl(self,allocation_id):
        idx = self.getTunerMapping(allocation_id)
        if idx < 0: raise FRONTEND.FrontendException("Invalid allocation id")
        if self.getControlAllocationId(idx) == allocation_id:
            return True
        return False

    def getTunerGroupId(self,allocation_id):
        idx = self.getTunerMapping(allocation_id)
        if idx < 0: raise FRONTEND.FrontendException("Invalid allocation id")
        return self.frontend_tuner_status[idx].group_id

    def getTunerRfFlowId(self,allocation_id):
        idx = self.getTunerMapping(allocation_id)
        if idx < 0: raise FRONTEND.FrontendException("Invalid allocation id")
        return self.frontend_tuner_status[idx].rf_flow_id


    def setTunerCenterFrequency(self,allocation_id, freq):
        idx = self.getTunerMapping(allocation_id)
        if idx < 0: raise FRONTEND.FrontendException("Invalid allocation id")
        if allocation_id != self.getControlAllocationId(idx):
            raise FRONTEND.FrontendException(("ID "+str(allocation_id)+" does not have authorization to modify the tuner"))
        if freq<0: raise FRONTEND.BadParameterException()
        # set hardware to new value. Raise an exception if it's not possible
        self.frontend_tuner_status[idx].center_frequency = freq

    def getTunerCenterFrequency(self,allocation_id):
        idx = self.getTunerMapping(allocation_id)
        if idx < 0: raise FRONTEND.FrontendException("Invalid allocation id")
        return self.frontend_tuner_status[idx].center_frequency

    def setTunerBandwidth(self,allocation_id, bw):
        idx = self.getTunerMapping(allocation_id)
        if idx < 0: raise FRONTEND.FrontendException("Invalid allocation id")
        if allocation_id != self.getControlAllocationId(idx):
            raise FRONTEND.FrontendException(("ID "+str(allocation_id)+" does not have authorization to modify the tuner"))
        if bw<0: raise FRONTEND.BadParameterException()
        # set hardware to new value. Raise an exception if it's not possible
        self.frontend_tuner_status[idx].bandwidth = bw

    def getTunerBandwidth(self,allocation_id):
        idx = self.getTunerMapping(allocation_id)
        if idx < 0: raise FRONTEND.FrontendException("Invalid allocation id")
        return self.frontend_tuner_status[idx].bandwidth

    def setTunerAgcEnable(self,allocation_id, enable):
        raise FRONTEND.NotSupportedException("setTunerAgcEnable not supported")

    def getTunerAgcEnable(self,allocation_id):
        raise FRONTEND.NotSupportedException("getTunerAgcEnable not supported")

    def setTunerGain(self,allocation_id, gain):
        raise FRONTEND.NotSupportedException("setTunerGain not supported")

    def getTunerGain(self,allocation_id):
        raise FRONTEND.NotSupportedException("getTunerGain not supported")

    def setTunerReferenceSource(self,allocation_id, source):
        raise FRONTEND.NotSupportedException("setTunerReferenceSource not supported")

    def getTunerReferenceSource(self,allocation_id):
        raise FRONTEND.NotSupportedException("getTunerReferenceSource not supported")

    def setTunerEnable(self,allocation_id, enable):
        idx = self.getTunerMapping(allocation_id)
        if idx < 0: raise FRONTEND.FrontendException("Invalid allocation id")
        if allocation_id != self.getControlAllocationId(idx):
            raise FRONTEND.FrontendException(("ID "+str(allocation_id)+" does not have authorization to modify the tuner"))
        # set hardware to new value. Raise an exception if it's not possible
        self.frontend_tuner_status[idx].enabled = enable

    def getTunerEnable(self,allocation_id):
        idx = self.getTunerMapping(allocation_id)
        if idx < 0: raise FRONTEND.FrontendException("Invalid allocation id")
        return self.frontend_tuner_status[idx].enabled

    def get_rf_flow_id(self,port_name):
        return "none"

    def set_rf_flow_id(self,port_name, id):
        pass

    def get_rfinfo_pkt(self,port_name):
        return frontend.RFInfoPkt()

    def set_rfinfo_pkt(self,port_name, pkt):
        pass
  
if __name__ == '__main__':
    logging.getLogger().setLevel(logging.INFO)
    logging.debug("Starting Device")
    start_device(analogTuner_i)

