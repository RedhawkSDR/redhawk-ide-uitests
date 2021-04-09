#!/usr/bin/env python
#
# AUTO-GENERATED CODE.  DO NOT MODIFY!
#
# Source: AllPropertyTypesDevice.spd.xml
from ossie.cf import CF
from ossie.cf import CF__POA
from ossie.utils import uuid

from ossie.device import Device
from ossie.threadedcomponent import *
from ossie.properties import simple_property
from ossie.properties import simpleseq_property
from ossie.properties import struct_property
from ossie.properties import structseq_property

import Queue, copy, time, threading

class AllPropertyTypesDevice_base(CF__POA.Device, Device, ThreadedComponent):
        # These values can be altered in the __init__ of your derived class

        PAUSE = 0.0125 # The amount of time to sleep if process return NOOP
        TIMEOUT = 5.0 # The amount of time to wait for the process thread to die when stop() is called
        DEFAULT_QUEUE_SIZE = 100 # The number of BulkIO packets that can be in the queue before pushPacket will block

        def __init__(self, devmgr, uuid, label, softwareProfile, compositeDevice, execparams):
            Device.__init__(self, devmgr, uuid, label, softwareProfile, compositeDevice, execparams)
            ThreadedComponent.__init__(self)

            # self.auto_start is deprecated and is only kept for API compatibility
            # with 1.7.X and 1.8.0 devices.  This variable may be removed
            # in future releases
            self.auto_start = False
            # Instantiate the default implementations for all ports on this device

        def start(self):
            Device.start(self)
            ThreadedComponent.startThread(self, pause=self.PAUSE)

        def stop(self):
            Device.stop(self)
            if not ThreadedComponent.stopThread(self, self.TIMEOUT):
                raise CF.Resource.StopError(CF.CF_NOTSET, "Processing thread did not die")

        def releaseObject(self):
            try:
                self.stop()
            except Exception:
                self._log.exception("Error stopping")
            Device.releaseObject(self)

        ######################################################################
        # PORTS
        # 
        # DO NOT ADD NEW PORTS HERE.  You can add ports in your derived class, in the SCD xml file, 
        # or via the IDE.

        ######################################################################
        # PROPERTIES
        # 
        # DO NOT ADD NEW PROPERTIES HERE.  You can add properties in your derived class, in the PRF xml file
        # or by using the IDE.
        device_kind = simple_property(id_="device_kind",
                                      name="device_kind",
                                      type_="string",
                                      defvalue="TestDevice",
                                      mode="readonly",
                                      action="eq",
                                      kinds=("allocation",),
                                      description="""This specifies the device kind""")
        
        device_model = simple_property(id_="device_model",
                                       name="device_model",
                                       type_="string",
                                       defvalue="NotARealDevice",
                                       mode="readonly",
                                       action="eq",
                                       kinds=("allocation",),
                                       description=""" This specifies the specific device""")
        
        simpleString = simple_property(id_="simpleString",
                                       name="simpleString",
                                       type_="string",
                                       defvalue="simpleStringTest",
                                       mode="readwrite",
                                       action="external",
                                       kinds=("property",))
        
        simpleBool = simple_property(id_="simpleBool",
                                     name="simpleBool",
                                     type_="boolean",
                                     defvalue=True,
                                     mode="readwrite",
                                     action="external",
                                     kinds=("property",))
        
        simpleDouble = simple_property(id_="simpleDouble",
                                       name="simpleDouble",
                                       type_="double",
                                       defvalue=123.456,
                                       mode="readwrite",
                                       action="external",
                                       kinds=("property",))
        
        simpleShort = simple_property(id_="simpleShort",
                                      name="simpleShort",
                                      type_="short",
                                      defvalue=1,
                                      mode="readwrite",
                                      action="external",
                                      kinds=("property",))
        
        simpleSeqString = simpleseq_property(id_="simpleSeqString",
                                             name="simpleSeqString",
                                             type_="string",
                                             defvalue=["simpleSeqStringTest1", "simpleSeqStringTest2", "simpleSeqStringTest3"                                             ],
                                             mode="readwrite",
                                             action="external",
                                             kinds=("property",))
        
        simpleSeqBool = simpleseq_property(id_="simpleSeqBool",
                                           name="simpleSeqBool",
                                           type_="boolean",
                                           defvalue=[True, False, True                                             ],
                                           mode="readwrite",
                                           action="external",
                                           kinds=("property",))
        
        simpleSeqDouble = simpleseq_property(id_="simpleSeqDouble",
                                             name="simpleSeqDouble",
                                             type_="double",
                                             defvalue=[234.56700000000001, 345.678, 456.78899999999999                                             ],
                                             mode="readwrite",
                                             action="external",
                                             kinds=("property",))
        
        simpleSeqShort = simpleseq_property(id_="simpleSeqShort",
                                            name="simpleSeqShort",
                                            type_="short",
                                            defvalue=[2, 3, 4                                             ],
                                            mode="readwrite",
                                            action="external",
                                            kinds=("property",))
        
        class StructString(object):
            structSimpleString = simple_property(
                                                 id_="structSimpleString",
                                                 name="structSimpleString",
                                                 type_="string",
                                                 defvalue="structSimpleStringTest"
                                                 )
        
            structSimpleSeqString = simpleseq_property(
                                                       id_="structSimpleSeqString",
                                                       name="structSimpleSeqString",
                                                       type_="string",
                                                       defvalue=["structSeqSimpleStringTest1", "structSeqSimpleStringTest2", "structSeqSimpleStringTest3"]
                                                       )
        
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
                d["structSimpleString"] = self.structSimpleString
                d["structSimpleSeqString"] = self.structSimpleSeqString
                return str(d)
        
            @classmethod
            def getId(cls):
                return "structString"
        
            @classmethod
            def isStruct(cls):
                return True
        
            def getMembers(self):
                return [("structSimpleString",self.structSimpleString),("structSimpleSeqString",self.structSimpleSeqString)]
        
        structString = struct_property(id_="structString",
                                       name="structString",
                                       structdef=StructString,
                                       configurationkind=("property",),
                                       mode="readwrite")
        
        class StructBool(object):
            structSimpleBool = simple_property(
                                               id_="structSimpleBool",
                                               name="structSimpleBool",
                                               type_="boolean",
                                               defvalue=False
                                               )
        
            structSimpleSeqBool = simpleseq_property(
                                                     id_="structSimpleSeqBool",
                                                     name="structSimpleSeqBool",
                                                     type_="boolean",
                                                     defvalue=[False, True, False]
                                                     )
        
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
                d["structSimpleBool"] = self.structSimpleBool
                d["structSimpleSeqBool"] = self.structSimpleSeqBool
                return str(d)
        
            @classmethod
            def getId(cls):
                return "structBool"
        
            @classmethod
            def isStruct(cls):
                return True
        
            def getMembers(self):
                return [("structSimpleBool",self.structSimpleBool),("structSimpleSeqBool",self.structSimpleSeqBool)]
        
        structBool = struct_property(id_="structBool",
                                     name="structBool",
                                     structdef=StructBool,
                                     configurationkind=("property",),
                                     mode="readwrite")
        
        class StructDouble(object):
            structSimpleDouble = simple_property(
                                                 id_="structSimpleDouble",
                                                 name="structSimpleDouble",
                                                 type_="double",
                                                 defvalue=987.654
                                                 )
        
            structSimpleSeqDouble = simpleseq_property(
                                                       id_="structSimpleSeqDouble",
                                                       name="structSimpleSeqDouble",
                                                       type_="double",
                                                       defvalue=[876.54300000000001, 765.43200000000002, 654.32100000000003]
                                                       )
        
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
                d["structSimpleDouble"] = self.structSimpleDouble
                d["structSimpleSeqDouble"] = self.structSimpleSeqDouble
                return str(d)
        
            @classmethod
            def getId(cls):
                return "structDouble"
        
            @classmethod
            def isStruct(cls):
                return True
        
            def getMembers(self):
                return [("structSimpleDouble",self.structSimpleDouble),("structSimpleSeqDouble",self.structSimpleSeqDouble)]
        
        structDouble = struct_property(id_="structDouble",
                                       name="structDouble",
                                       structdef=StructDouble,
                                       configurationkind=("property",),
                                       mode="readwrite")
        
        class StructShort(object):
            structSimpleShort = simple_property(
                                                id_="structSimpleShort",
                                                name="structSimpleShort",
                                                type_="short",
                                                defvalue=9
                                                )
        
            structSimpleSeqShort = simpleseq_property(
                                                      id_="structSimpleSeqShort",
                                                      name="structSimpleSeqShort",
                                                      type_="short",
                                                      defvalue=[8, 7, 6]
                                                      )
        
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
                d["structSimpleShort"] = self.structSimpleShort
                d["structSimpleSeqShort"] = self.structSimpleSeqShort
                return str(d)
        
            @classmethod
            def getId(cls):
                return "structShort"
        
            @classmethod
            def isStruct(cls):
                return True
        
            def getMembers(self):
                return [("structSimpleShort",self.structSimpleShort),("structSimpleSeqShort",self.structSimpleSeqShort)]
        
        structShort = struct_property(id_="structShort",
                                      name="structShort",
                                      structdef=StructShort,
                                      configurationkind=("property",),
                                      mode="readwrite")
        
        class StructSeqStructString(object):
            structSeqStructSimpleString = simple_property(
                                                          id_="structSeqStructSimpleString",
                                                          name="structSeqStructSimpleString",
                                                          type_="string",
                                                          defvalue="structSeqStructSimpleStringTest"
                                                          )
        
            structSeqStructSimpleSeqString = simpleseq_property(
                                                                id_="structSeqStructSimpleSeqString",
                                                                name="structSeqStructSimpleSeqString",
                                                                type_="string",
                                                                defvalue=["structSeqStructSimpleStringTest1", "structSeqStructSimpleStringTest2", "structSeqStructSimpleStringTest3"]
                                                                )
        
            def __init__(self, structSeqStructSimpleString="structSeqStructSimpleStringTest", structSeqStructSimpleSeqString=["structSeqStructSimpleStringTest1", "structSeqStructSimpleStringTest2", "structSeqStructSimpleStringTest3"]):
                self.structSeqStructSimpleString = structSeqStructSimpleString
                self.structSeqStructSimpleSeqString = structSeqStructSimpleSeqString
        
            def __str__(self):
                """Return a string representation of this structure"""
                d = {}
                d["structSeqStructSimpleString"] = self.structSeqStructSimpleString
                d["structSeqStructSimpleSeqString"] = self.structSeqStructSimpleSeqString
                return str(d)
        
            @classmethod
            def getId(cls):
                return "structSeqStructString"
        
            @classmethod
            def isStruct(cls):
                return True
        
            def getMembers(self):
                return [("structSeqStructSimpleString",self.structSeqStructSimpleString),("structSeqStructSimpleSeqString",self.structSeqStructSimpleSeqString)]
        
        structSeqString = structseq_property(id_="structSeqString",
                                             name="structSeqString",
                                             structdef=StructSeqStructString,
                                             defvalue=[StructSeqStructString(structSeqStructSimpleString="structSeqStructSimpleStringTest",structSeqStructSimpleSeqString=["structSeqStructSimpleStringTest1","structSeqStructSimpleStringTest2","structSeqStructSimpleStringTest3"])],
                                             configurationkind=("property",),
                                             mode="readwrite")
        
        class StructSeqStructBool(object):
            structSeqStructSimpleBool = simple_property(
                                                        id_="structSeqStructSimpleBool",
                                                        name="structSeqStructSimpleBool",
                                                        type_="boolean",
                                                        defvalue=True
                                                        )
        
            structSeqStructSimpleSeqBool = simpleseq_property(
                                                              id_="structSeqStructSimpleSeqBool",
                                                              name="structSeqStructSimpleSeqBool",
                                                              type_="boolean",
                                                              defvalue=[True, True, False]
                                                              )
        
            def __init__(self, structSeqStructSimpleBool=True, structSeqStructSimpleSeqBool=[True, True, False]):
                self.structSeqStructSimpleBool = structSeqStructSimpleBool
                self.structSeqStructSimpleSeqBool = structSeqStructSimpleSeqBool
        
            def __str__(self):
                """Return a string representation of this structure"""
                d = {}
                d["structSeqStructSimpleBool"] = self.structSeqStructSimpleBool
                d["structSeqStructSimpleSeqBool"] = self.structSeqStructSimpleSeqBool
                return str(d)
        
            @classmethod
            def getId(cls):
                return "structSeqStructBool"
        
            @classmethod
            def isStruct(cls):
                return True
        
            def getMembers(self):
                return [("structSeqStructSimpleBool",self.structSeqStructSimpleBool),("structSeqStructSimpleSeqBool",self.structSeqStructSimpleSeqBool)]
        
        structSeqBool = structseq_property(id_="structSeqBool",
                                           name="structSeqBool",
                                           structdef=StructSeqStructBool,
                                           defvalue=[StructSeqStructBool(structSeqStructSimpleBool=False,structSeqStructSimpleSeqBool=[True,True,False])],
                                           configurationkind=("property",),
                                           mode="readwrite")
        
        class StructSeqStructDouble(object):
            structSeqStructSimpleDouble = simple_property(
                                                          id_="structSeqStructSimpleDouble",
                                                          name="structSeqStructSimpleDouble",
                                                          type_="double",
                                                          defvalue=12.34
                                                          )
        
            structSeqStructSimpleSeqDouble = simpleseq_property(
                                                                id_="structSeqStructSimpleSeqDouble",
                                                                name="structSeqStructSimpleSeqDouble",
                                                                type_="double",
                                                                defvalue=[23.449999999999999, 34.560000000000002, 45.670000000000002]
                                                                )
        
            def __init__(self, structSeqStructSimpleDouble=12.34, structSeqStructSimpleSeqDouble=[23.449999999999999, 34.560000000000002, 45.670000000000002]):
                self.structSeqStructSimpleDouble = structSeqStructSimpleDouble
                self.structSeqStructSimpleSeqDouble = structSeqStructSimpleSeqDouble
        
            def __str__(self):
                """Return a string representation of this structure"""
                d = {}
                d["structSeqStructSimpleDouble"] = self.structSeqStructSimpleDouble
                d["structSeqStructSimpleSeqDouble"] = self.structSeqStructSimpleSeqDouble
                return str(d)
        
            @classmethod
            def getId(cls):
                return "structSeqStructDouble"
        
            @classmethod
            def isStruct(cls):
                return True
        
            def getMembers(self):
                return [("structSeqStructSimpleDouble",self.structSeqStructSimpleDouble),("structSeqStructSimpleSeqDouble",self.structSeqStructSimpleSeqDouble)]
        
        structSeqDouble = structseq_property(id_="structSeqDouble",
                                             name="structSeqDouble",
                                             structdef=StructSeqStructDouble,
                                             defvalue=[StructSeqStructDouble(structSeqStructSimpleDouble=12.34,structSeqStructSimpleSeqDouble=[23.449999999999999,34.560000000000002,45.670000000000002])],
                                             configurationkind=("property",),
                                             mode="readwrite")
        
        class StructSeqStructShort(object):
            structSeqStructSimpleShort = simple_property(
                                                         id_="structSeqStructSimpleShort",
                                                         name="structSeqStructSimpleShort",
                                                         type_="short",
                                                         defvalue=22
                                                         )
        
            structSeqStructSimpleSeqShort = simpleseq_property(
                                                               id_="structSeqStructSimpleSeqShort",
                                                               name="structSeqStructSimpleSeqShort",
                                                               type_="short",
                                                               defvalue=[23, 24, 25]
                                                               )
        
            def __init__(self, structSeqStructSimpleShort=22, structSeqStructSimpleSeqShort=[23, 24, 25]):
                self.structSeqStructSimpleShort = structSeqStructSimpleShort
                self.structSeqStructSimpleSeqShort = structSeqStructSimpleSeqShort
        
            def __str__(self):
                """Return a string representation of this structure"""
                d = {}
                d["structSeqStructSimpleShort"] = self.structSeqStructSimpleShort
                d["structSeqStructSimpleSeqShort"] = self.structSeqStructSimpleSeqShort
                return str(d)
        
            @classmethod
            def getId(cls):
                return "structSeqStructShort"
        
            @classmethod
            def isStruct(cls):
                return True
        
            def getMembers(self):
                return [("structSeqStructSimpleShort",self.structSeqStructSimpleShort),("structSeqStructSimpleSeqShort",self.structSeqStructSimpleSeqShort)]
        
        structSeqShort = structseq_property(id_="structSeqShort",
                                            name="structSeqShort",
                                            structdef=StructSeqStructShort,
                                            defvalue=[StructSeqStructShort(structSeqStructSimpleShort=12,structSeqStructSimpleSeqShort=[23,34,45])],
                                            configurationkind=("property",),
                                            mode="readwrite")
        


