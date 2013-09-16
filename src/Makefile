JFLAGS =
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	MigratableProcess.java \
	MasterServiceException.java \
	SlaveServiceException.java \
	TransactionalFileInputStream.java \
	TransactionalFileOutputStream.java \
	SlaveInfo.java \
	ProcessStatus.java \
	MasterServer.java \
	ProcessInfo.java \
	SlaveServer.java \
	ProcessManager.java \
	TestMigratableProcess.java \
    CaesarCipher.java \
    FileCompression.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
