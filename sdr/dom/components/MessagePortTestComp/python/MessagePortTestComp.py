#!/usr/bin/env python
#
#
# AUTO-GENERATED
#
# Source: MessagePortTestComp.spd.xml
from ossie.resource import start_component
import logging

from MessagePortTestComp_base import *

from ossie import properties
import CosEventComm__POA
import time


class MessagePortTestComp_i(MessagePortTestComp_base):
    """Pushes a message out its ports every two seconds"""

    class Supplier_i(CosEventComm__POA.PushSupplier):

        def disconnect_push_supplier(self):
            pass

    def constructor(self):
        self.consumer = None

    def process(self):
        self.mymessage.EnterMessageHere = "Congrats on your new message!"
        self.port_message_out.sendMessage(self.mymessage)

        # This code only handles 1 connection to the port
        if len(self.port_eventChannel_out.getConnectionIds()) != 0:
            connectionID = self.port_eventChannel_out.getConnectionIds()[0]
            if self.consumer is None:
                self.consumer = self.port_eventChannel_out.for_suppliers(connectionID).obtain_push_consumer()
                self.consumer.connect_push_supplier(self.Supplier_i()._this())

            dt = CF.DataType(id=self.mymessage.getId(), value=properties.struct_to_any(self.mymessage))
            anyProps = properties.props_to_any([dt])
            try:
                self.consumer.push(anyProps)
            except:
                try:
                    self.consumer.disconnect_push_consumer()
                except:
                    pass
                self.consumer = None

        time.sleep(2)
        return NOOP


if __name__ == '__main__':
    logging.getLogger().setLevel(logging.INFO)
    logging.debug("Starting Component")
    start_component(MessagePortTestComp_i)
