# Installation and Setup

## Building the project
CogniCrypt<sub>SAST</sub> uses Maven as build tool. You can compile this project and build the executable `jar` files via the command
```
mvn clean package -DskipTests
```
The packaged `jar` artefacts including all dependencies can be found in the created `/apps` directory. Building requires at least Java 17.

## Including the project as dependency
You can find CogniCrypt<sub>SAST</sub> on Maven Central. Depending on your use case, include the following dependencies in your project (replace `x.y.z` with the most recent version):
- CryptoAnalysis and its scopes
```pom
<dependency>
    <groupId>de.fraunhofer.iem</groupId>
    <artifactId>CryptoAnalysis</artifactId>
    <version>x.y.z</version>
</dependency>
<dependency>
    <groupId>de.fraunhofer.iem</groupId>
    <artifactId>CryptoAnalysis-Scopes</artifactId>
    <version>x.y.z</version>
</dependency>
```
- HeadlessJavaScanner
```pom
<dependency>
    <groupId>de.fraunhofer.iem</groupId>
    <artifactId>HeadlessJavaScanner</artifactId>
    <version>x.y.z</version>
</dependency> 
```
- HeadlessAndroidScanner
```pom
<dependency>
    <groupId>de.fraunhofer.iem</groupId>
    <artifactId>HeadlessAndroidScanner</artifactId>
    <version>x.y.z</version>
</dependency> 
```

## Use as a GitHub action
CogniCrypt<sub>SAST</sub> can be used as a GitHub action.

- name: Run CogniCrypt
  uses: CROSSINGTUD/CryptoAnalysis@<version>
  with:
  appPath: "CryptoAnalysisTargets/HelloWorld/HelloWorld.jar"
  basePath: "CryptoAnalysisTargets/HelloWorld"

The `appPath` needs to be configured to point to a compiled version of your application.

The `basePath` is used to relate paths in the analyzed jar and the source tree. Class `com.example` is searched for at `basePath/com/example`.

See [action.yml](https://github.com/CROSSINGTUD/CryptoAnalysis/blob/develop/action.yml) for all input options.

An example of how to use the GitHub action can be found in the [CryptoAnalysis-demo repository](https://github.com/CROSSINGTUD/CryptoAnalysis-demo/actions).

## Running tests
The project is configured to run the tests in each module and for each framework separately. If you plan to run the tests, use the following commands:
- Test CryptoAnalysis: `mvn clean verify -f CryptoAnalysis -DtestSetup=<framework>`
- Test the HeadlessJavaScanner: `mvn clean verify -f HeadlessJavaScanner -DtestSetup=<framework>`
- Test the HeadlessAndroidScanner: `mvn clean verify -f HeadlessAndroidScanner -DtestSetup=FlowDroid`

Replace `<framework>` with `Soot`, `SootUp` or `Opal` to run the tests with the corresponding underlying framework.
