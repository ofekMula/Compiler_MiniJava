# Compiler-MiniJava
An Implementation of a compiler for an object-oriented language called MiniJava into LLVM assembly/IR.
The project is adapted from materials kindly provided by the staff of the compiler classes at the University of Athens and the University of Washington.

 <img src="https://github.com/rickben/CompilerMiniJava/blob/master/%E2%80%8Fcompiler.JPG" width="600" /> 
## MiniJava Programming language
MiniJava is (almost exactly) a subset of Java, defined in the Appendix of Appel and Palsberg's Modern Compiler Implementation in Java, 2nd edition and described on the MiniJava web site http://www.cambridge.org/resources/052182060X/ .

## LLVM assembly/IR

The LLVM language is documented in the LLVM Language Reference Manual, although you will use only a subset of the instructions, which is described below.


## Project Structure
Our project Consists of 3 stages of the compilation process :

### 1. Lexing & Parsing.
Generates ASTs from textual representations of MiniJava programs.

Usage: java -jar mjavac.jar parse marshal inputProg.java out.xml
will write to out.xml a valid XML representation (per the schema) of the AST that corresponds to the input MiniJava program.

### 2. Semantic Checking.
In this part of the project we will validate a MiniJava AST to ensure that it conforms to the MiniJava specification, and in particular satisfies all the assumptions we have utilized for code generation. Nevertheless, these checks are part of the language, and they must be enforced even if your compiler can generate code for programs that don't satisfy some of the restrictions.

Usage: java -jar mjavac.jar unmarshal semantic inputProg.xml out.txt.
will write to out.txt either "OK\n" if the input program passes all the semantic checks, or "ERROR\n" if any of the rules is violated.

### 3. Code Generation into LLVM assembly/IR:
In this part of the project we will translate a MiniJava AST into equivalent code in the intermediate representation used by the LLVM compiler project.

Usage: java -jar mjavac.jar unmarshal compile inputProg.xml out.ll
will write to out.ll a translation of program represented by inputProg.xml into the textual format of LLVM IR.

## Other Tools:
=== Compiling the project === ant

=== Cleaning === ant clean

=== From AST XML to Java program === java -jar mjavac.jar unmarshal print examples/BinaryTree.xml res.java

=== From AST XML to... AST XML === java -jar mjavac.jar unmarshal marshal examples/BinaryTree.xml res.xm
