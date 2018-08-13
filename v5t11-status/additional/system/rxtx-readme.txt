Dieser Adapter nutzt die Comm-I/O-Library RXTX (http://mfizz.com/oss/rxtx-for-java, derzeitige Version 2.2-20081207).

Der Native-Anteil (DLL, Shared Lib) muss in den Library Path kopiert werden, z.B. für Windows: rxtxParallel.dll und rxtxSerial.dll --> c:\windows\system32.

Der Java-Anteil muss als Modul im JBoss bereit gestellt werden, s. additional/jboss/modules.