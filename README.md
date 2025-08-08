# CogniCrypt<sub>SAST</sub>

This repository contains **CogniCrypt<sub>SAST</sub>**, the static analysis component for [CogniCrypt](https://www.cognicrypt.org). 
The static analysis **CogniCrypt<sub>SAST</sub>** takes rules written in the specification language CrySL as input 
and performs a static analysis based on the specification of the rules. CrySL is a domain-specific language (DSL) designed to encode usage specifications for cryptographic 
libraries (e.g. the [JCA](https://docs.oracle.com/en/java/javase/14/security/java-cryptography-architecture-jca-reference-guide.html) in particular). More information on CrySL and the static analysis may be found in [this paper](http://drops.dagstuhl.de/opus/volltexte/2018/9215/).

## Features

CogniCrypt<sub>SAST</sub> consists of the following features:

- A context-sensitive, field-sensitive and flow-sensitive typestate and pointer analysis
- A CLI to analyze Java and Android applications
- Support for the static analysis frameworks [Soot](https://github.com/soot-oss/soot), [SootUp](https://github.com/soot-oss/sootup) and [Opal](https://github.com/opalj/opal)

We provide a complete [documentation](https://crossingtud.github.io/CryptoAnalysis/latest/) for all technical details and options.

## Releases

You can check out a pre-compiled version of **CogniCrypt<sub>SAST</sub>** [here](https://github.com/CROSSINGTUD/CryptoAnalysis/releases).
We recommend using the latest version.
You can find **CogniCrypt<sub>SAST</sub>** also on [Maven Central](https://central.sonatype.com/artifact/de.fraunhofer.iem/CryptoAnalysis).

## Checkout and Build

**CogniCrypt<sub>SAST</sub>** uses Maven as build tool. You can compile and build this project via

```mvn clean package -DskipTests```

The packaged `jar` artifacts including all dependencies can be found in `/apps`. Building requires at least Java 17.

## Running CogniCrypt<sub>SAST</sub>

**CogniCrypt<sub>SAST</sub>** analyzes Java and Android apps to detect cryptographic misuses based on [CrySL rules](https://github.com/CROSSINGTUD/Crypto-API-Rules).

### 1. Prepare Your Inputs

- Compile your application to a `.jar` or `.apk` file
- Download the `HeadlessJavaScanner-x.y.z-jar-with-dependencies.jar` for analyzing Java applications or the `HeadlessAndroidScanner-x.y.z-jar-with-dependencies.jar` for analyzing Android applications from the [GitHub releases](https://github.com/CROSSINGTUD/CryptoAnalysis/releases) or build them yourself
- Download CrySL rules (e.g. [JCA rules](https://github.com/CROSSINGTUD/Crypto-API-Rules/tree/master/JavaCryptographicArchitecture/src))

### 2. Run the Analysis

- CogniCrypt<sub>SAST</sub> for Java applications
```bash
java -jar HeadlessJavaScanner-x.y.z-jar-with-dependencies.jar \
  --appPath <YourApp.jar> \
  --rulesDir ./CrySL-Rules/ \
  --reportFormat CMD,SARIF \
  --reportPath ./output/
```

- CogniCrypt<sub>SAST</sub> for Android applications
```bash
java -jar HeadlessAndroidScanner-x.y.z-jar-with-dependencies.jar \
--apkFile <YourApp.apk> \
--platformDirectory <path_to_platforms_directory>
--rulesDir ./CrySL-Rules/ \
--reportFormat CMD,SARIF \
--reportPath ./output/
```

### 3. Output

- Reports are written to `--reportPath` and/or printed to the console
- Misuse types include: `ConstraintError`, `TypestateError`, see [Error Types](https://crossingtud.github.io/CryptoAnalysis/latest/error-types/) for all error types

> ⚠️ Note: You may need to allocate more memory for large analyses:
> `-Xmx8g -Xss60m`

For advanced options, visit the [full documentation](https://crossingtud.github.io/CryptoAnalysis/latest/).

## Use as a GitHub Action

**CogniCrypt<sub>SAST</sub>** can be used as a GitHub action.

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

An example of how to use the GitHub action can be found in the [CryptoAnalysis-demo repository](https://github.com/CROSSINGTUD/CryptoAnalysis-demo/actions).
