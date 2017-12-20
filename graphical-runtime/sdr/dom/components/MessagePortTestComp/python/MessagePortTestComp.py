#!/usr/bin/env python
#
#
# AUTO-GENERATED
#
# Source: MessagePortTestComp.spd.xml
from ossie.resource import start_component
import logging
import time

from MessagePortTestComp_base import *

class MessagePortTestComp_i(MessagePortTestComp_base):
    """<DESCRIPTION GOES HERE>"""
    def constructor(self):
        """
        This is called by the framework immediately after your component registers with the system.
        """
        
    def process(self):
        msg = MessagePortTestComp_base.Mymessage()
        msg.EnterMessageHere = "Congrats on your new message!"
        self.port_message_out.sendMessage(msg)
        time.sleep(2)
        return NOOP

  
if __name__ == '__main__':
    logging.getLogger().setLevel(logging.INFO)
    logging.debug("Starting Component")
    start_component(MessagePortTestComp_i)

