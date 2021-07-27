# MicroProfile generated Application

## Introduction

MicroProfile Starter has generated this MicroProfile application for you.

The generation of the executable jar file can be performed by issuing the following command

    mvn clean compile package

This will create a jar file **chapter4-runner.jar** within the _target_ maven folder. This can be started by executing
the following command

    java -jar target/chapter4-runner.jar

You can also start the project in development mode where it automatically updates code on the fly as you save your
files:

    mvn clean compile quarkus:dev

Last but not least, you can build the whole application into a one statically linked executable that does not require
JVM:

    mvn clean compile package -Pnative

Native executable build might take a minute. Then you can execute it on a compatible architecture without JVM:

    ./target/chapter4-runner

To launch the test page, open your browser at the following URL

    http://localhost:8080/index.html

## Note on Native image

* You need GraalVM installed from the GraalVM web site. Using the community edition is enough. Version 19.1.1+ is
  required.
* The GRAALVM_HOME environment variable configured appropriately
* The native-image tool must be installed; this can be done by running ```gu install native-image``` from your GraalVM
  directory
* To read more about Quarkus and Native image, see https://quarkus.io/guides/building-native-image-guide

## Specification examples

By default, there is always the creation of a JAX-RS application class to define the path on which the JAX-RS endpoints
are available.

Also, a simple Hello world endpoint is created, have a look at the class **HelloController**.

More information on MicroProfile can be found [here](https://microprofile.io/)















