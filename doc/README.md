# Closure Code Generation for Java
This repository hosts references to Closure/GAPS components for Java Code Generation. 

## Contents
- [Dependencies](#dependencies)
- [Build](#build)
- [Run](#run)

## Dependencies
See [Third Party Libaries](../../servers/README.md) for third party libraries that are needed to build the app.

## Build
To generate partitioned Java Code, run the class com.peratonlabs.closure.codegen.CodeGen, with test/cut.json as the sole argument.
The partitioned code will be left in z:/tmp/xdcc (as specified in cut.json, referred to as $XDCC below).

### Build Partitioned Code
        $ cd $XDCC/Purple/testprog
        $ ant                       # build the original code
        $ ant -f build-closure.xml  # aspectJ weaving
        
        $ cd $XDCC/Orange/testprog
        $ ant                       # build the original code
        $ ant -f build-closure.xml  # aspectJ weaving

## Run
After successfully built, the enclaves can be started using the following commands.

        $ cd $XDCC/Purple/testprog
        $ export CLASSPATH=dist/weaved-TESTPROGRAM.jar:dist/closure-aspect.jar:aspect/lib/aspectjrt.jar:aspect/lib/gson-2.8.0.jar:aspect/lib/codeGen.jar
        $ java -Djava.library.path=/usr/local/lib com.peratonlabs.closure.testprog.example1.Example1
        
        $ cd $XDCC/Orange/testprog
        $ export CLASSPATH=dist/weaved-TESTPROGRAM.jar:dist/closure-aspect.jar:aspect/lib/aspectjrt.jar:aspect/lib/gson-2.8.0.jar:aspect/lib/codeGen.jar
        $ java -Djava.library.path=/usr/local/lib com.peratonlabs.closure.testprog.example1.Example1

