README for DeRP Dumb Enjoyable Readable Programming

Written by Parker Jones


Dependencies:
Java. Written on Java 11, can possibly run on earlier versions. To check your version run $java -version
make. depending on your package manager this could be different. commonly obtained with $apt-get install make -y

-----DERP------
##Running a Program in DERP

to run a program in DERP use the command
run <file to run>.derp

To Print the file back to you use
run -r <file to print>.derp


## Hello World
all programs in DERP must have a main function which will
be called to begin execution after the program has been parsed.
To print, use the `print()` command.
```
function main using () {
	print("hello world")
}
```

## Comments
to write a comment in DERP, precede the comment with a  //
this will comment out the rest of the line.
example: foo() //this is a comment

## Types
DERP is dynamically typed so you dont need to type your variables when defining them.

The types that are available are:
- Integer
- String
- Boolean
- Real

## Arrays
Creating an array:

list arr[100]
will create an array with 100 indeces to fill.

Assigning to an array:

arr[3] = "test assignment"
 will assign "test assignment" to index 3 in arr

you can assign multiple types into the same array such as

```
arr[3] = "test assignment"
arr[2] = 5
```


## Conditionals
if, otherwise if, and while expect a conditional list after their keyword,
surrounded by parenthesis and followed by a body in curly braces.

A valid combination of `if`, `otherwise if`, and `otherwise`  would be
```
if (x > 5) {
  foo1()
}
otherwise if (x < 1){
  foo2()
}
otherwise {
  foo3()
}
```

An example of a `while` loop would be
```
loop while(x > 0){
  z = z + 1
}
```


## Operators
The operators accepted in DERP include `+`, `-`, `/`, `*`, `==`, `>`, `<`, `>=`, `<=`, and `%`.

## Lambdas
Lambdas are not fully implemented, but they are recognized in the syntax and will be printed by the pretty printer