java-bitpay-client
==================

This is the Java client library for the BitPay Payment Gateway.  This library implements BitPay's [Cryptographically Secure RESTful API](https://bitpay.com/api).

## Quick Start Guide

To get up and running with our Java library quickly, see the GUIDE here: https://github.com/bitpay/java-bitpay-client/blob/master/GUIDE.md

## Eclipse Project Setup

1. Import the project from git repository.

```
From Project Explorer > Import > Projects from Git ...
```

2. Convert project to a Java project - locate and edit the .project file in your Eclipse workspace directory to include the following.

```xml
<buildSpec>
    <buildCommand>
        <name>org.eclipse.jdt.core.javabuilder</name>
        <arguments>
        </arguments>
    </buildCommand>
</buildSpec>
<natures>
    <nature>org.eclipse.jdt.core.javanature</nature>
</natures>
```

3. Download project dependencies using maven.

```
cd <root directory of project>
mvn dependency:copy-dependencies -DoutputDirectory=lib
```

4. Add dependencies - add external JAR files downloaded by maven to the Eclipse project.

```
Project > Properties > Java Build Path > Libraries > Add External JARs > (choose all JARs in lib directory)
```

5. Add JUnit Library to the project.

```
Project > Properties > Java Build Path > Libraries > Add Library > Unit > Unit 4
```

6. Run tests.

```
src/test/BitPayTest.java > Run As > JUnit Test
```

## Support

* https://github.com/bitpay/java-bitpay-client/issues
* https://help.bitpay.com/

## Contribute

To contribute to this project, please fork and submit a pull request.

## License

The MIT License (MIT)

Copyright (c) 2014-2018 BitPay, Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
