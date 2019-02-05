JFLAGS = -g
JC = javac
JVM = java Recognizer
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Types.java \
	Lexeme.java \
	Lexer.java \
	Recognizer.java \

MAIN = Recognizer

default: classes

classes: $(CLASSES:.java=.class)

recognizer : Recognizer.class
	$(JC) Recognizer.java

test1 : $(MAIN).class
	$(JVM) test1.derp

test2: $(MAIN).class
	$(JVM) test2.derp

test3: $(MAIN).class
	$(JVM) test3.derp

test4: $(MAIN).class
	$(JVM) test4.derp

test5: $(MAIN).class
	$(JVM) test5.derp

run : $(MAIN).class
	$(JVM) $(MAIN) test1.derp

clean:
	$(RM) *.class

