# CogniCrypt_SAST

This repository contains CogniCrypt_SAST, the static analysis component for [CogniCrypt](www.cognicrypt.de). 
The static analysis CogniCrypt_SAST takes rules written in the specification language CrySL as input, 
and performs a static analysis based on the specification of the rules.CrySL is a domain-specific language (DSL) designed to encode usage specifications for cryptographic 
libaries (the [JCA](https://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/CryptoSpec.html) in particular). More information on CrySL and the static analysis is found in [this paper](https://arxiv.org/abs/1710.00564)

## Checkout and Build

This repository uses git submodules, to checkout this repository use the following command for git

```git clone --recurse-submodules git@github.com:CROSSINGTUD/CryptoAnalysis.git```

CogniCrypt_SAST uses maven as build tool. To compile this project `cd` into the newly checked out folder and run

```mvn package -DskipTests=true```

Once build, a packaged  `jar` artifact including all dependency is found in `CryptoAnalysis/build/CryptoAnalysis-1.0.0-jar-with-dependencies.jar` 

## Usage

CogniCrypt_SAST can be started in headless mode (i.e., detached from Eclipse) via the class `crypto.HeadlessCryptoScanner`. It requires two arguments: 
* The absolute path to the CrySL (binary) rule files
* The absolute path of the application to be analyzed (.jar file or a folder with .class files)

```
java -cp CryptoAnalysis/build/CryptoAnalysis-1.0.0-jar-with-dependencies.jar crypto.HeadlessCryptoScanner \
      --rulesDir=<absolute-path-to-crysl-rules> \
      --applicationCp=<absolute-application-path>
```

For an easy start we prepared a .jar containing classes with crypto misuses. . To run CogniCrypt_SAST on these classes, simply execute the following command (on a linux based system).

```
java -cp CryptoAnalysis/build/CryptoAnalysis-1.0.0-jar-with-dependencies.jar crypto.HeadlessCryptoScanner \
  --rulesDir=$(pwd)/CryptoAnalysis/src/test/resources/ \
  --applicationCp=$(pwd)/CryptoAnalysisTargets/CogniCryptDemoExample/Examples.jar
```
The source code is found [here](https://github.com/CROSSINGTUD/CryptoAnalysis/tree/master/CryptoAnalysisTargets/CogniCryptDemoExample/src/example).


## CogniCrypt_SAST for Android Applications

CogniCrypt_SAST can also be run on Android Applications, checkout the repository [here](https://github.com/CROSSINGTUD/CryptoAnalysis-Android).
