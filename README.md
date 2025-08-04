# CogniCrypt<sub>SAST</sub>

This repository contains **CogniCrypt<sub>SAST</sub>**, the static analysis component for [CogniCrypt](https://www.cognicrypt.org). 
The static analysis **CogniCrypt<sub>SAST</sub>** takes rules written in the specification language CrySL as input, 
and performs a static analysis based on the specification of the rules. CrySL is a domain-specific language (DSL) designed to encode usage specifications for cryptographic 
libaries (e.g., the [JCA](https://docs.oracle.com/en/java/javase/14/security/java-cryptography-architecture-jca-reference-guide.html) in particular). More information on CrySL and the static analysis may be found in [this paper](http://drops.dagstuhl.de/opus/volltexte/2018/9215/).

Refer to the [documentation](https://crossingtud.github.io/CryptoAnalysis/latest/) for more technical details.

## Running CognitCrypt<sub>SAST</sub>

**CogniCrypt<sub>SAST</sub>** analyzes Java and Android apps to detect cryptographic misuses based on [CrySL rules](https://github.com/CROSSINGTUD/Crypto-API-Rules).

### 1. Prepare Your Inputs

- Compile your application to a `.jar` or `.class` output
- Download CrySL rules (e.g. [JCA rules](https://github.com/CROSSINGTUD/Crypto-API-Rules))

### 2. Run the Analysis

```bash
java -jar HeadlessJavaScanner-x.y.z-jar-with-dependencies.jar \
  --appPath ./YourApp.jar \
  --rulesDir ./CrySL-Rules/ \
  --reportFormat CMD \
  --reportPath ./output/ \
  --visualization
```

### 3. Output

- Reports are written to `--reportPath` and/or printed to the console
- Misuse types include: `ConstraintError`, `TypestateError`, see [Error Types](https://crossingtud.github.io/CryptoAnalysis/latest/error-types/) for more.
- `visualization.png` shows misuse dependencies

> ⚠️ Note: You may need to allocate more memory for large analyses:
> `-Xmx8g -Xss60m`

For advanced options, Android support, and more, visit the [full documentation](https://crossingtud.github.io/CryptoAnalysis/latest/).

## Android Support

**CogniCrypt<sub>SAST</sub>** also supports analysis of Android applications via `HeadlessAndroidScanner`. You'll need an `.apk` file and the Android SDK platform directory.

See the [Android analysis guide](https://crossingtud.github.io/CryptoAnalysis/latest/android-scanner/) for full instructions and setup.

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

## Releases

You can checkout a pre-compiled version of **CogniCrypt<sub>SAST</sub>** [here](https://github.com/CROSSINGTUD/CryptoAnalysis/releases). We recommend using the latest version. You can find **CogniCrypt<sub>SAST</sub>** also on [Maven Central](https://central.sonatype.com/artifact/de.fraunhofer.iem/CryptoAnalysis).


## Checkout and Build

**CogniCrypt<sub>SAST</sub>** uses Maven as build tool. You can compile and build this project via

```mvn clean package -DskipTests```.

The packaged  `jar` artifacts including all dependencies can be found in `/apps`. Building requires at least Java 17.


## How can I contribute?
We hare happy for every contribution from the community!

* [Contributing](CONTRIBUTING.md) for details on issues and merge requests.
* [Coding Guidles](CODING.md) for this project.

