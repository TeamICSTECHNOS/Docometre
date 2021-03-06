MENU_TITLE = Read Byte from serial port
DESCRIPTION = Read Byte (eight bits) from selected serial port
MENU_TITLE_FR = Lire un octet du port série
DESCRIPTION_FR = Lire un octet du port série sélectionné
# Several segments are available in ADBascic code :
# HEADER, INCLUDE, DECLARATION, INITIALIZATION, ACQUISITION, RECOVERY, TRANSFER, EVENT, GENERATION and FINISH
# The ADBasic function code you need to add in any segments INITIALIZATION, EVENT or FINISH may require contributions to any other segment.
# If you need to contribute to any of these segments, you must create a property key which prefix is the name of that segment
# and an optional suffix which is the concatenation of system type (PRO or GOLD) and CPU type (I or II) separated by "_" char, when
# ADBasic code depends on sytem and cpu types. 
# Finally the property keys FUNCTION_CODE (and optionally FUNCTION_CODE_SYSTEMTYPE_CPUTYPE) contain the effective ADBasic function code which will be added to
# any of segments INITIALIZATION, EVENT or FINISH depending on the actual function position.
# Take care of the fact that each key must be upper case !

FUNCTION_CODE_PRO_I = byteVariableName = READ_FIFO(moduleNumber, portNumber)

FUNCTION_CODE_PRO_II = byteVariableName = READ_FIFO(moduleNumber, portNumber)

FUNCTION_CODE_GOLD_I = byteVariableName = READ_FIFO(portNumber)

FUNCTION_CODE_GOLD_II = byteVariableName = READ_FIFO(portNumber)