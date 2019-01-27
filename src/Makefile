JFLAGS = -g
JC = javac
JVM = java Scanner
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Types.java \
	Lexeme.java \
	Lexer.java \
	Scanner.java \

MAIN = Scanner

default: classes

classes: $(CLASSES:.java=.class)

scanner : Scanner.class
	$(JVM) program.derp

test1 : Scanner.class
	$(JVM) test1.derp

test2: Scanner.class
	$(JVM) test2.derp

test3: Scanner.class
	$(JVM) test3.derp

test4: Scanner.class
	$(JVM) test4.derp

test5: Scanner.class
	$(JVM) test5.derp

run : $(MAIN).class
	$(JVM) $(MAIN) test1.derp

clean:
	$(RM) *.class

