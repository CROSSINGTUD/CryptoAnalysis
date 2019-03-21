[![Build Status](https://soot-build.cs.uni-paderborn.de/jenkins/buildStatus/icon?job=boomerang%2FCryptoAnalysis-Multibranch%2Fmaster)](https://soot-build.cs.uni-paderborn.de/jenkins/job/boomerang/job/CryptoAnalysis-Multibranch/job/master/)

# CogniCrypt<sub>SAST</sub>

This repository contains CogniCrypt<sub>SAST</sub>, the static analysis component for [CogniCrypt](www.cognicrypt.de). 
The static analysis CogniCrypt<sub>SAST</sub> takes rules written in the specification language CrySL as input, 
and performs a static analysis based on the specification of the rules. CrySL is a domain-specific language (DSL) designed to encode usage specifications for cryptographic 
libaries (the [JCA](https://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/CryptoSpec.html) in particular). More information on CrySL and the static analysis is found in [this paper](http://drops.dagstuhl.de/opus/volltexte/2018/9215/).

## Releases

You can checkout a pre-compiled version of CogniCrypt<sub>SAST</sub> [here](https://github.com/CROSSINGTUD/CryptoAnalysis/releases). 

Download the two files:
* CryptoAnalysis-2.0-jar-with-dependencies.jar
* JCA-CrySL-rules.zip

## Checkout and Build

CogniCrypt<sub>SAST</sub> uses maven as build tool. You can compile and build this project via

```mvn package -DskipTests=true```.

A packaged  `jar` artifact including all dependency is found in `CryptoAnalysis/build/CryptoAnalysis-2.0-jar-with-dependencies.jar` 

## Usage

CogniCrypt<sub>SAST</sub> can be started in headless mode (i.e., detached from Eclipse) via the class `crypto.HeadlessCryptoScanner`. It requires two arguments: 
* The absolute path to the directory of the CrySL (binary) rule files contained in [JCA-CrySL-rules.zip](https://github.com/CROSSINGTUD/CryptoAnalysis/releases/tag/v1.0.0). This CrySL rule set contains specification for the JCA. The source code for the rules is found [here](https://github.com/CROSSINGTUD/Crypto-API-Rules).
* The absolute path of the application to be analyzed (.jar file or the root compilation output folder which contains the .class files in subdirectories)

```
java -cp CryptoAnalysis/build/CryptoAnalysis-2.0-jar-with-dependencies.jar crypto.HeadlessCryptoScanner \
      --rulesDir=<absolute-path-to-crysl-rules> \
      --applicationCp=<absolute-application-path>
```

For an easy start we prepared a .jar containing classes with crypto misuses. The source code for these misuses is found [here](https://github.com/CROSSINGTUD/CryptoAnalysis/tree/master/CryptoAnalysisTargets/CogniCryptDemoExample/src/example). To run CogniCrypt<sub>SAST</sub> on these classes, simply execute the following command (on a linux based system).

```
java -cp CryptoAnalysis/build/CryptoAnalysis-2.0-jar-with-dependencies.jar crypto.HeadlessCryptoScanner \
  --rulesDir=$(pwd)/CryptoAnalysis/src/test/resources/ \
  --applicationCp=$(pwd)/CryptoAnalysisTargets/CogniCryptDemoExample/Examples.jar
```

Note, depending on the analyzed application, the analysis may require a lot of memory and a large stack size. Remember to set the necessary heap size (e.g. -Xmx8g) and stack size (e.g. -Xss60m).

## Report and Error Types

In the standard option, CogniCrypt<sub>SAST</sub> outputs a report to the console. CogniCrypt<sub>SAST</sub> reporst misuses when the code is not compliant with the CrySL rules. For each misuse CogniCrypt<sub>SAST</sub> reports the class and the method the misuse is contained in. There are multiple misuse types:

* **ConstraintError**: A constraint of a CrySL rule is violated, e.g., a key is generated with the wrong key size.
* **NeverTypeOfError**: Reported when a value was found to be of a certain reference type: For example, a character array containing a password should never be converted from a `String`. (see `KeyStore` rule [here](https://github.com/CROSSINGTUD/Crypto-API-Rules/blob/master/src/de/darmstadt/tu/crossing/KeyStore.cryptsl)).
* **ForbiddenMethodError**: A method that is forbidden (CrySL block FORBIDDEN) to be called under some circumstances was found.
* **ImpreciseValueExtractionError**: The static analysis was not able to extract all information required within the CrySL CONSTRAINT block. For example the key size could be supplied as a value listed in a configuration file. The static analysis does not model the file's content and may not constraint on the value.
* **TypestateError**: The ORDER block of CrySL is violated, i.e., the expected method sequence call to be made is incorrect. For example, a `Signature` object expects a call to `initSign(key)` prior to `update(data)`. 

* **RequiredPredicateError**: An object A expects an object B to have been used correctly (CrySL blocks REQUIRES and ENSURES). For example a `Cipher` object requires a `SecretKey` object to be correctly and securely generated. 
* **IncompleteOperationError**: The usage of an object may be incomplete: For example a `Cipher`object may be initialized but never used for en- or decryption, this may render the code dead. This error heavily depends on the computed call graph (CHA by default).

When the option `--reportDir=<folder>` is chosen, CogniCrypt<sub>SAST</sub> writes the report to the file `CogniCrypt-Report.txt` and additionally outputs the .jimple files of the classes where misuses where found in. Jimple is an intermediate representation close to the syntax of Java. 

## Visualization

When the `--reportDir` options is set, using the flag `--visualization` outputs visualizations for the data-flows. In the subfolder `viz`  of the `reportDir` Json files will be generated for each individual analyzed object. Download the folder [visualization](https://github.com/CROSSINGTUD/WPDS/tree/master/boomerangPDS/visualization) from the WPDS project, open the `index.html` in some browser (tested on Chrome) and drop any of the Json files in the lower right corner. This allows you to browse the generated data-flow graphs as shown below:

![Visualization](https://github.com/CROSSINGTUD/WPDS/blob/master/boomerangPDS/visualization/example2.png)

## Changing the CrySL Rules

The current version of the tool takes CrySL rules in their binary formats (cryptslbin). When you want to adopt the rules please use
the [Eclipse plugin CogniCrypt](https://github.com/CROSSINGTUD/CogniCrypt). CogniCrypt ships with a CrySL editor to modify the rules, upon changes to the rules the editor produces the cryptslbin files. We [plan](https://github.com/CROSSINGTUD/CryptoAnalysis/issues/42) to change CogniCrypt<sub>SAST</sub> to take CrySL rules in source code format in the future.  

## CogniCrypt<sub>SAST</sub> for Android Applications

CogniCrypt<sub>SAST</sub> can also be run on Android Applications, checkout the repository [here](https://github.com/CROSSINGTUD/CryptoAnalysis-Android).

## Contact

If you have any questions regarding this project, feel free to contact [Johannes Spaeth](mailto:johannes.spaeth@iem.fraunhofer.de).
