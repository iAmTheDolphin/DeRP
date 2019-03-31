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

iteration : classes
	java Parser iteration.derp

iterationx : classes
	java Evaluator iteration.derp

error1 : classes
	java Parser error1.derp

error1x : classes
	java Evaluator error1.derp

error2 : classes
	java Parser error2.derp

error2x : classes
	java Evaluator error2.derp

error3 : classes
	java Parser error3.derp

error3x : classes
	java Evaluator error3.derp

error4 : classes
	java Parser error4.derp

error4x : classes
	java Evaluator error4.derp

error5 : classes
	java Parser error5.derp

error5x : classes
	java Evaluator error5.derp

clean:
	rm -f *.class

