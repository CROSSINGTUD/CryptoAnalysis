[![Build Status](https://soot-build.cs.uni-paderborn.de/jenkins/buildStatus/icon?job=boomerang%2FCryptoAnalysis-Multibranch%2Fdevelop)](https://soot-build.cs.uni-paderborn.de/jenkins/job/boomerang/job/CryptoAnalysis-Multibranch/job/develop/)

# CogniCrypt<sub>SAST</sub>

This repository contains CogniCrypt<sub>SAST</sub>, the static analysis component for [CogniCrypt](https://www.cognicrypt.org). 
The static analysis CogniCrypt<sub>SAST</sub> takes rules written in the specification language CrySL as input, 
and performs a static analysis based on the specification of the rules. CrySL is a domain-specific language (DSL) designed to encode usage specifications for cryptographic 
libaries (e.g., the [JCA](https://docs.oracle.com/en/java/javase/14/security/java-cryptography-architecture-jca-reference-guide.html) in particular). More information on CrySL and the static analysis may be found in [this paper](http://drops.dagstuhl.de/opus/volltexte/2018/9215/).

## Releases

You can checkout a pre-compiled version of CogniCrypt<sub>SAST</sub> [here](https://github.com/CROSSINGTUD/CryptoAnalysis/releases). 

Download the two files:
* CryptoAnalysis-x.y.z-jar-with-dependencies.jar
* JCA-CrySL-rules.zip

## Checkout and Build

CogniCrypt<sub>SAST</sub> uses Maven as build tool. You can compile and build this project via

```mvn package -DskipTests=true```.

A packaged  `jar` artifact including all dependency is found in `CryptoAnalysis/build/CryptoAnalysis-x.y.z-jar-with-dependencies.jar` 

## Usage

CogniCrypt<sub>SAST</sub> can be started in headless mode (i.e., detached from Eclipse) via the class `crypto.HeadlessCryptoScanner`. It requires two arguments: 
* The absolute path to the directory of the CrySL (source code format) rule files. The source code for the rules which contains spesification for the JCA is found [here](https://github.com/CROSSINGTUD/Crypto-API-Rules).
* The absolute path of the application to be analyzed (.jar file or the root compilation output folder which contains the .class files in subdirectories)

```
java -cp <path-to-analysis-jar> crypto.HeadlessCryptoScanner 
      --rulesDir <absolute-path-to-crysl-source-code-format-rules> 
      --appPath <absolute-application-path>
```

For an easy start we prepared a .jar containing classes with crypto misuses. The source code for these misuses is found [here](https://github.com/CROSSINGTUD/CryptoAnalysis/tree/develop/CryptoAnalysisTargets/CogniCryptDemoExample/src/main/java/example). To run CogniCrypt<sub>SAST</sub> on these classes, simply execute the following command (on a linux based system).

```
java -cp CryptoAnalysis/build/CryptoAnalysis-2.6-jar-with-dependencies.jar crypto.HeadlessCryptoScanner 
  --rulesDir $(pwd)/CryptoAnalysis/src/main/resources/JavaCryptographicArchitecture 
  --appPath $(pwd)/CryptoAnalysisTargets/CogniCryptDemoExample/Examples.jar
```

Other additional arguments that can be used are as follows:

```
--cg <selection_of_call_graph_for_analysis> (possible values are CHA, SPARK, SPARKLIB)
--sootPath <absolute_path_of_whole_project>
--identifier <identifier_for_labelling_output_files>
--reportPath <directory_location_for_cognicrypt_report>
--reportFormat <format of cognicrypt_report> (possible values are TXT, SARIF, CSV)
--preanalysis (enables pre-analysis)
--visualization (enables the visualization, but also requires --reportPath option to be set)
--providerDetection (enables provider detection analysis)
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

When the option `--reportPath <directory_location_for_cognicrypt_report>` is chosen, CogniCrypt<sub>SAST</sub> writes the report to the file `CogniCrypt-Report.txt` and additionally outputs the .jimple files of the classes where misuses where found in. Jimple is an intermediate representation close to the syntax of Java. 

## Updating CrySL Rules

The tool takes CrySL rules in their source code formats (crysl). You can adapt the rules in any text editor.
Additionaly, the [Eclipse plugin CogniCrypt](https://github.com/CROSSINGTUD/CogniCrypt) ships with a CrySL editor to modify the rules with IDE support (e.g., content assist, auto completion, etc.). A step-by-step-explanation on how edit CrySL rules is avialable at the tool's website [cognicrypt.org](https://www.eclipse.org/cognicrypt/documentation/crysl/). 


## CogniCrypt<sub>SAST</sub> for Android Applications

CogniCrypt<sub>SAST</sub> can also be run on Android Applications using the Android version for CogniCrypt<sub>SAST</sub> in `CryptoAnalysis-Android`. Its usage does not deviate much from regular CogniCrypt<sub>SAST</sub>'s. CogniCrypt_SAST for Android can be started via the class `de.fraunhofer.iem.crypto.CogniCryptAndroid`. It requires three arguments in this order: 
* The absolute path to the .apk file
* The absolute path to the android SDK platforms. The platforms are obtainable via [Android Studio](https://developer.android.com/studio/releases/platforms). Under the Android SDK location you find a folder `platforms`. Supply CogniCrypt<sub>SAST</sub> with the path to this folder.
* The absolute path to the directory of the CrySL rules.

```
java -cp <path-to-analysis-jar> -Xmx8g -Xss60m de.fraunhofer.iem.crypto.CogniCryptAndroid \
      <path-to-apk> <path-to-android-platforms> <path-to-crysl-rules>
```
As an optional fourth parameter one can specify an output folder: 
```
java -cp <path-to-analysis-jar> -Xmx8g -Xss60m de.fraunhofer.iem.crypto.CogniCryptAndroid \
      <path-to-apk> <path-to-android-platforms> <path-to-crysl-rules> <output-dir>
```

If specified, the analysis generates a report file `CogniCrypt-Report.txt` along with the `.jimple` output of the classes the analysis found misuses in. The format of the report file follows that described above.

Note, depending on the analyzed application, the analysis may require a lot of memory and a large stack size. Remember to set the necessary heap size (e.g. -Xmx8g) and stack size (e.g. -Xss60m).
