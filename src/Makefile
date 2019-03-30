MAIN = Evaluator
JC = javac -g
JVM = java $(MAIN)
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Types.class \
	Lexeme.class \
	Lexer.class \
	Parser.class \
	Environment.class \
	PrettyPrinter.class \
	Evaluator.class \

SRC = \
	Types.java \
	Lexeme.java \
	Lexer.java \
	Parser.java \
	Environment.java \
	PrettyPrinter.java \
	Evaluator.java \


default: classes
	$(JC) $(SRC)

classes: $(CLASSES:.java=.class)

evaluator : Evaluator.class
	$(JVM) test1.derp

arrays : classes
	java Parser arrays.derp

arraysx : classes
	java Evaluator arrays.derp

conditionals : classes
	java Parser conditionals.derp

conditionalsx : classes
	java Evaluator conditionals.derp

recursion : classes
	java Parser recursion.derp

recursionx : classes
	java Evaluator recursion.derp

clean:
	rm -f *.class

