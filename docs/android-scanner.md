# CogniCrypt<sub>SAST</sub> for Android Applications

The `HeadlessAndroidScanner` implements an interface for CogniCrypt<sub>SAST</sub> that allows the analysis of Android applications. You can use it as an CLI tool or run it programmatically with a dependency.

## HeadlessAndroidScanner as CLI tool
CogniCrypt<sub>SAST</sub> can be started as CLI tool via the file `HeadlessAndroidScanner-x.y.z-jar-with-dependencies.jar`. You can build this file yourself (see the [installation](installation.md)) or download the last released version from the [GitHub releases](https://github.com/CROSSINGTUD/CryptoAnalysis/releases). The following list explains required and optional CLI options. See the [examples](examples.md) for concrete use cases

### Required options
The HeadlessJavaScanner requires three arguments:

- **--apkFile &lt;path_to_apk_file&gt;**

    The path of the apk file to be analyzed.

- **--platformDirectory &lt;path_to_platform_dir&gt;**

    The path to the android SDK platforms. The platforms are obtainable via [Android Studio](https://developer.android.com/tools/releases/platforms?hl=de). Under the Android SDK location you find a folder `platforms`. Supply the `HeadlessAndroidScanner` with the path to this folder.

- **--rulesDir &lt;path_to_rules&gt;**

    The path to the directory of the CrySL (source code format) rule files. The scanner supports basic directories and zip files. The source code for the rules can be found [here](https://github.com/CROSSINGTUD/Crypto-API-Rules).

### Optional arguments
- **--cg &lt;call_graph&gt;**

    The call graph algorithm to construct the call graph for the analysis. Possible values:

    * `CHA` (default)
    * `RTA`
    * `VTA`
    * `SPARK`

- **--reportPath &lt;report_path&gt;**

    Relative or absolute path for a directory to write the reports and visualization into.

- **--reportFormat &lt;format1,format2,...&gt;**

    The format(s) of the report. CogniCrypt<sub>SAST</sub> supports different formats to create an analysis report with the detected errors. Except the `CMD` value, all values require the `--reportPath` argument to be set. Multiple values can be concatenated by a comma (e.g. `CMD,TXT`). Possible values:

    * `CMD`: Prints a formatted output to `System.out` (default).
    * `TXT`: Creates a file `CryptoAnalysis-Report.txt` in the report path directory that contains a formatted output. The report is equivalent to the `CMD` output.
    * `SARIF`: Creates a file `CryptoAnalysis-Report.json` in the report path directory that is formatted in the [SARIF 2.1](https://docs.oasis-open.org/sarif/sarif/v2.1.0/sarif-v2.1.0.html) format.
    * `CSV`: Creates a file `CryptoAnalysis-Report.csv` in the report path directory that is formatted in the csv format.
    * `CSV_SUMMARY`: Creates a file `CryptoAnalysis-Report-Summary.csv` in the report path directory that is formatted in the csv format. Compared to the `CSV` option, this version contains only a summary of the analysis results, e.g. only the total numbers of each error type.

- **--visualization**

    Creates a file `visualization.png` in the report path directory that visualizes the connection of detected errors. This argument requires the `--reportPath` argument to be set.

## HeadlessAndroidScanner with a dependency
CogniCrypt<sub>SAST</sub> provides a simple API that allows its usage inside a program. Its usage does not deviate from the CLI tool; for each argument, there is a corresponding `setter` method. Include the following dependency in your project and instantiate the `HeadlessJavaScanner`:

```
<dependency>
    <groupId>de.fraunhofer.iem</groupId>
    <artifactId>HeadlessAndroidScanner</artifactId>
    <version>x.y.z</version>
</dependency>
```

You have two options to instantiate the `HeadlessAndroidScanner` and continue with its results:

### Instantiation via CLI structure
A call to `createFromCLISettings(String[])` simulates the instantiation via the CLI. This can look like this:
```java
public class Example {
    
    public static void main(String[] args) {
        String[] myArgs = new String[] {"--appPath", "path/to/app", "platformDirectory", "path/to/platforms", "--rulesDir", "path/to/rules"};
        HeadlessAndroidScanner scanner = HeadlessAndroidScanner.createFromCLISettings(myArgs);
        scanner.run();
        
        // Read the errors
        Table<WrappedClass, Method, Set<AbstractError>> errors = scanner.getCollectedErrors();
        
        // Continue with the collected errors
    }
}

```

### Instantiation via constructor
The `HeadlessAndroidScanner` has a public constructor that requires the required arguments:
```java
public class Example {
    
    public static void main(String[] args) {
        HeadlessJavaScanner scanner = new HeadlessJavaScanner("path/to/app", "path/to/platforms", "path/to/rules");
        scanner.setCallGraphAlgorithm(AndroidSettings.CallGraphAlgorithm.SPARK);
        scanner.run();

        // Read the errors
        Table<WrappedClass, Method, Set<AbstractError>> errors = scanner.getCollectedErrors();

        // Continue with the collected errors
    }
}
```
