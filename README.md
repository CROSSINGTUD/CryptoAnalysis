# CogniCrypt<sub>SAST</sub>

This repository contains CogniCrypt<sub>SAST</sub>, the static analysis component for [CogniCrypt](https://www.cognicrypt.org). 
The static analysis CogniCrypt<sub>SAST</sub> takes rules written in the specification language CrySL as input, 
and performs a static analysis based on the specification of the rules. CrySL is a domain-specific language (DSL) designed to encode usage specifications for cryptographic 
libaries (e.g., the [JCA](https://docs.oracle.com/en/java/javase/14/security/java-cryptography-architecture-jca-reference-guide.html) in particular). More information on CrySL and the static analysis may be found in [this paper](http://drops.dagstuhl.de/opus/volltexte/2018/9215/).

## Releases

You can checkout a pre-compiled version of CogniCrypt<sub>SAST</sub> [here](https://github.com/CROSSINGTUD/CryptoAnalysis/releases). We recommend using the latest version.

Download the two files:
* CryptoAnalysis-x.y.z-jar-with-dependencies.jar
* JCA-CrySL-rules.zip

You can find CogniCrypt<sub>SAST</sub> also on [Maven Central](https://central.sonatype.com/artifact/de.fraunhofer.iem/CryptoAnalysis).

## Checkout and Build

CogniCrypt<sub>SAST</sub> uses Maven as build tool. You can compile and build this project via

```mvn package -DskipTests=true```.

A packaged  `jar` artifact including all dependency is found in `CryptoAnalysis/build/CryptoAnalysis-x.y.z-jar-with-dependencies.jar`. Building requires at least Java 11.

## Usage

CogniCrypt<sub>SAST</sub> can be started in headless mode (i.e., detached from Eclipse) via the class `crypto.HeadlessCryptoScanner`. It requires two arguments: 
* The path to the directory of the CrySL (source code format) rule files. The source code for the rules which contain specification for the JCA is found [here](https://github.com/CROSSINGTUD/Crypto-API-Rules).
* The path of the application to be analyzed (.jar file or the root compilation output folder which contains the .class files in subdirectories)

```
java -cp <path-to-analysis-jar> crypto.HeadlessCryptoScanner 
      --rulesDir <path-to-crysl-source-code-format-rules> 
      --appPath <application-path>
```

For an easy start we prepared a .jar containing classes with crypto misuses. The source code for these misuses is found [here](https://github.com/CROSSINGTUD/CryptoAnalysis/tree/develop/CryptoAnalysisTargets/CogniCryptDemoExample/src/main/java/example). To run CogniCrypt<sub>SAST</sub> on these classes, simply execute the following command (on a linux based system).

```
java -cp CryptoAnalysis/build/CryptoAnalysis-x.y.z-jar-with-dependencies.jar crypto.HeadlessCryptoScanner 
  --rulesDir $(pwd)/CryptoAnalysis/src/main/resources/JavaCryptographicArchitecture 
  --appPath $(pwd)/CryptoAnalysisTargets/CogniCryptDemoExample/Examples.jar
```

Other additional arguments that can be used are as follows:

```
--cg <selection_of_call_graph_for_analysis> (possible values are CHA, SPARK, SPARKLIB)
--sootPath <absolute_path_of_whole_project>
--identifier <identifier_for_labeling_output_files>
--reportPath <directory_location_for_cryptoanalysis_report>
--reportFormat <format of cryptoanalysis_report> (possible values are CMD, TXT, SARIF, CSV, CSV_SUMMARY)
--preanalysis (enables pre-analysis. @Deprecated since 3.2.0)
--visualization (enables the visualization, but also requires --reportPath option to be set)
--providerDetection (enables provider detection analysis. @Deprecated since 3.2.0)
--dstats (disables the output of the analysis statistics in the reports)
--ignoreSections (Text file with packages (e.g. `de.example.*`), classes (e.g. `de.example.exmapleClass`) or methods (e.g. `de.example.exampleClass.exampleMethod`), one per line. Those packages, classes and methods are ignored during the analysis)
--timeout <timeout in milliseconds> (Timeout for seeds in milliseconds. If a seed exceeds this value, CryptoAnalysis aborts the typestate and extract parameter analysis and continues with the results computed so far. (default: 10000))
--help (show more information for the CLI arguments)
```

Note, depending on the analyzed application, the analysis may require a lot of memory and a large stack size. Remember to set the necessary heap size (e.g. -Xmx8g) and stack size (e.g. -Xss60m).

### Use as a GitHub Action

CogniCrypt<sub>SAST</sub> can be used as a GitHub action.

```yaml
- name: Run CogniCrypt
  uses: CROSSINGTUD/CryptoAnalysis@version
  with:
    appPath: "CryptoAnalysisTargets/HelloWorld/HelloWorld.jar"
    basePath: "CryptoAnalysisTargets/HelloWorld"
```

The `appPath` needs to be configured to point to a compiled version of your application.

The `basePath` is used to relate paths in the analyzed jar and the source tree.
Class `com.example` is searched for at `basePath/com/example`.

See [`action.yml`](action.yml) for all input options.

## Report and Error Types

CogniCrypt<sub>SAST</sub> reports misuses when the code is not compliant with the CrySL rules. For each misuse, CogniCrypt<sub>SAST</sub> reports the class and the method the misuse is contained in. There are multiple misuse types:

* **ConstraintError**: A constraint of a CrySL rule is violated, e.g., a key is generated with the wrong key size.
* **NeverTypeOfError**: Reported when a value was found to be of a certain reference type: For example, a character array containing a password should never be converted from a `String`. (see `KeyStore` rule [here](https://github.com/CROSSINGTUD/Crypto-API-Rules/blob/master/src/de/darmstadt/tu/crossing/KeyStore.cryptsl)).
* **ForbiddenMethodError**: A method that is forbidden (CrySL block FORBIDDEN) to be called under some circumstances was found.
* **ImpreciseValueExtractionError**: The static analysis was not able to extract all information required within the CrySL CONSTRAINT block. For example the key size could be supplied as a value listed in a configuration file. The static analysis does not model the file's content and may not constraint on the value.
* **TypestateError**: The ORDER block of CrySL is violated, i.e., the expected method sequence call to be made is incorrect. For example, a `Signature` object expects a call to `initSign(key)` prior to `update(data)`. 

* **RequiredPredicateError**: An object A expects an object B to have been used correctly (CrySL blocks REQUIRES and ENSURES). For example a `Cipher` object requires a `SecretKey` object to be correctly and securely generated. 
* **IncompleteOperationError**: The usage of an object may be incomplete: For example a `Cipher`object may be initialized but never used for en- or decryption, this may render the code dead. This error heavily depends on the computed call graph (CHA by default).
* **UncaughtExceptionError**: A method may throw an exception, but the exception is not caught in the program. For example, the method call is not surrounded by a try/catch block.

CogniCrypt<sub>SAST</sub> supports different report formats, which can be set by using `--reportformat` option. The supported formats are:
- `CMD`: The report is printed to the command line. The content is equivalent to the format from the `TXT` option.
- `TXT`: The report is written to the text file `CryptoAnalysis-Report.txt`. The content is equivalent to the format from the `CMD` option.  Additionally, the .jimple files of the classes, where misuses were found in, are output. Jimple is an intermediate representation close to the syntax of Java.
- `SARIF`: The report is written to the JSON file `CryptoAnalysis-Report.json`. The content is formatted in the SARIF format.
- `CSV`: The report is written to the CSV file `CryptoAnalysis-Report.csv`. The content is formatted in the CSV format.
- `CSV_SUMMARY`: The report is written to the file `CryptoAnalysis-Report-Summary.csv` and contains a summary of the analysis results. Compared to the `CSV` format, this format does not provide concrete information about the errors, it only lists the amount of each misuse type. This option was previously implemented by the `CSV` option, which has been changed to provide more detailed information about the errors in the CSV format.
- `GITHUB_ANNOTATION`: Works like `CMD` but also outputs all violations as annotations when running inside as a GitHub Action.

If the `--reportformat` option is not specified, CogniCrypt<sub>SAST</sub> defaults to the `CMD` option. It also allows the usage of multiple different formats for the same analysis (e.g. `--reportformat CMD,TXT,CSV` creates a report, which is printed to the command line and is written to a text and CSV file). If the option `--reportPath <directory_location_for_cryptoanalysis_report>` is set, the reports are created in the specified directory.

## Updating CrySL Rules

The tool takes CrySL rules in their source code formats (crysl). You can adapt the rules in any text editor.
Additionaly, the [Eclipse plugin CogniCrypt](https://github.com/CROSSINGTUD/CogniCrypt) ships with a CrySL editor to modify the rules with IDE support (e.g., content assist, auto completion, etc.). A step-by-step-explanation on how edit CrySL rules is avialable at the tool's website [cognicrypt.org](https://www.eclipse.org/cognicrypt/documentation/crysl/). 


## CogniCrypt<sub>SAST</sub> for Android Applications

CogniCrypt<sub>SAST</sub> can also be run on Android Applications using the Android version for CogniCrypt<sub>SAST</sub> in `CryptoAnalysis-Android`. Its usage does not deviate much from regular CogniCrypt<sub>SAST</sub>'s. CogniCrypt_SAST for Android can be started via the class `de.fraunhofer.iem.crypto.CogniCryptAndroidAnalysis`. It requires three arguments in this order: 
* The absolute path to the .apk file
* The absolute path to the android SDK platforms. The platforms are obtainable via [Android Studio](https://developer.android.com/studio/releases/platforms). Under the Android SDK location you find a folder `platforms`. Supply CogniCrypt<sub>SAST</sub> with the path to this folder.
* The absolute path to the directory of the CrySL rules.

```
java -cp <path-to-analysis-jar> -Xmx8g -Xss60m de.fraunhofer.iem.crypto.CogniCryptAndroidAnalysis \
      <path-to-apk> <path-to-android-platforms> <path-to-crysl-rules>
```
As an optional fourth parameter one can specify an output folder: 
```
java -cp <path-to-analysis-jar> -Xmx8g -Xss60m de.fraunhofer.iem.crypto.CogniCryptAndroidAnalysis \
      <path-to-apk> <path-to-android-platforms> <path-to-crysl-rules> <output-dir>
```

If specified, the analysis generates a report file `CogniCrypt-Report.txt` along with the `.jimple` output of the classes the analysis found misuses in. The format of the report file follows that described above.

Note, depending on the analyzed application, the analysis may require a lot of memory and a large stack size. Remember to set the necessary heap size (e.g. -Xmx8g) and stack size (e.g. -Xss60m).

## How can I contribute?
We hare happy for every contribution from the community!

* [Contributing](CONTRIBUTING.md) for details on issues and merge requests.
* [Coding Guidles](CODING.md) for this project.
