#!/usr/bin/env python
#
#
# AUTO-GENERATED
#
# Source: scanner.spd.xml
from ossie.device import start_device
import logging

from scanner_base import *

class scanner_i(scanner_base):
    """<DESCRIPTION GOES HERE>"""
    def constructor(self):
        self.addChannels(1, "RX_SCANNER_DIGITIZER");
        self.last_scan_strategy = {}
        self.last_scan_start_time = {}

    def process(self):
        return NOOP

    '''
    *************************************************************
    Functions supporting tuning allocation
    *************************************************************'''
    def deviceEnable(self, fts, tuner_id):
        fts.enabled = True
        return

    def deviceDisable(self,fts, tuner_id):
        fts.enabled = False
        return

    def deviceSetTuningScan(self,request, scan_request, fts, tuner_id):
        if request.tuner_type != "RX_SCANNER_DIGITIZER":
            return False
        fts.bandwidth = request.bandwidth
        fts.center_frequency = request.center_frequency
        fts.sample_rate = request.sample_rate
        fts.rf_flow_id = request.rf_flow_id
        fts.group_id = request.group_id
        fts.scan_mode_enabled = True
        fts.supports_scan = True
        self.matchAllocationIdToStreamId(request.allocation_id, request.allocation_id, "dataDouble_out")
        return True

    def deviceSetTuning(self,request, fts, tuner_id):
        return False

    def deviceDeleteTuning(self, fts, tuner_id):
        self.removeAllocationIdRouting(tuner_id)
        return True

    '''
    *************************************************************
    Functions servicing the tuner control port
    *************************************************************'''
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
        if freq<0: raise FRONTEND.BadParameterException("Center frequency cannot be less than 0")
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
        if bw<0: raise FRONTEND.BadParameterException("Bandwidth cannot be less than 0")
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


    def setTunerOutputSampleRate(self,allocation_id, sr):
        idx = self.getTunerMapping(allocation_id)
        if idx < 0: raise FRONTEND.FrontendException("Invalid allocation id")
        if allocation_id != self.getControlAllocationId(idx):
            raise FRONTEND.FrontendException(("ID "+str(allocation_id)+" does not have authorization to modify the tuner"))
        if sr<0: raise FRONTEND.BadParameterException("Sample rate cannot be less than 0")
        # set hardware to new value. Raise an exception if it's not possible
        self.frontend_tuner_status[idx].sample_rate = sr

    def getTunerOutputSampleRate(self,allocation_id):
        idx = self.getTunerMapping(allocation_id)
        if idx < 0: raise FRONTEND.FrontendException("Invalid allocation id")
        return self.frontend_tuner_status[idx].sample_rate



    def getScanStatus(self, allocation_id):
        idx = self.getTunerMapping(allocation_id)
        if idx < 0: raise FRONTEND.FrontendException("Invalid allocation id")
        if not self.last_scan_strategy.has_key(allocation_id) or not self.last_scan_start_time.has_key(allocation_id):
            # set hardware to new value. Raise an exception if it's not possible
            _scan_strategy=FRONTEND.ScanningTuner.ScanStrategy(
                FRONTEND.ScanningTuner.MANUAL_SCAN, 
                FRONTEND.ScanningTuner.ScanModeDefinition(center_frequency=1.0), 
                FRONTEND.ScanningTuner.TIME_BASED, 
                0.0)
            _scan_status=FRONTEND.ScanningTuner.ScanStatus(_scan_strategy,
                                               start_time=bulkio.timestamp.now(),
                                               center_tune_frequencies=[],
                                               started=False)
        else:
            # set hardware to new value. Raise an exception if it's not possible
            strat = self.last_scan_strategy[allocation_id]
            start_time = self.last_scan_start_time[allocation_id]
            _scan_status=FRONTEND.ScanningTuner.ScanStatus(strat,
                                               start_time=start_time,
                                               center_tune_frequencies=[],
                                               started=False)
            
        return _scan_status

    def setScanStartTime(self, allocation_id, start_time):
        logging.info("setScanStartTime for ID %s to UTC %s", allocation_id, str(start_time))
        idx = self.getTunerMapping(allocation_id)
        if idx < 0: raise FRONTEND.FrontendException("Invalid allocation id")
        self.last_scan_start_time[allocation_id] = start_time
        if allocation_id != self.getControlAllocationId(idx):
            raise FRONTEND.FrontendException(("ID "+str(allocation_id)+" does not have authorization to modify the tuner"))
        self.lastScanStartTimeSec = start_time.twsec

    def setScanStrategy(self, allocation_id, scan_strategy):
        logging.info("setScanStrategy for ID %s to %s", allocation_id, str(scan_strategy))
        idx = self.getTunerMapping(allocation_id)
        if idx < 0: raise FRONTEND.FrontendException("Invalid allocation id")
        self.last_scan_strategy[allocation_id] = scan_strategy
        if allocation_id != self.getControlAllocationId(idx):
            raise FRONTEND.FrontendException(("ID "+str(allocation_id)+" does not have authorization to modify the tuner"))
        self.lastScanStrategy = "%s = %s" % (allocation_id, str(scan_strategy))

    '''
    *************************************************************
    Functions servicing the RFInfo port(s)
    - port_name is the port over which the call was received
    *************************************************************'''
    def get_rf_flow_id(self,port_name):
        return ""

    def set_rf_flow_id(self,port_name, _id):
        pass

    def get_rfinfo_pkt(self,port_name):
        _antennainfo=FRONTEND.AntennaInfo('','','','')
        _freqrange=FRONTEND.FreqRange(0,0,[])
        _feedinfo=FRONTEND.FeedInfo('','',_freqrange)
        _sensorinfo=FRONTEND.SensorInfo('','','',_antennainfo,_feedinfo)
        _rfcapabilities=FRONTEND.RFCapabilities(_freqrange,_freqrange)
        _rfinfopkt=FRONTEND.RFInfoPkt('',0.0,0.0,0.0,False,_sensorinfo,[],_rfcapabilities,[])
        return _rfinfopkt

    def set_rfinfo_pkt(self,port_name, pkt):
        pass
  
if __name__ == '__main__':
    logging.getLogger().setLevel(logging.INFO)
    logging.debug("Starting Device")
    start_device(scanner_i)

