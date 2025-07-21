# CogniCrypt<sub>SAST</sub> for Java Applications
The `HeadlessJavaScanner` implements an interface for CogniCrypt<sub>SAST</sub> that allows the analysis of Java applications. You can use it as an CLI tool or run it programmatically with a dependency.

## HeadlessJavaScanner as CLI tool
CogniCrypt<sub>SAST</sub> can be started as CLI tool via the file `HeadlessJavaScanner-x.y.z-jar-with-dependencies.jar`. You can build this file yourself (see the [installation](installation.md)) or download the last released version from the [GitHub releases](https://github.com/CROSSINGTUD/CryptoAnalysis/releases). The following list explains required and optional CLI options. See the [examples](examples.md) for concrete use cases

### Required arguments
The HeadlessJavaScanner requires two arguments:

**--appPath &lt;path_to_app&gt;**

The path of the application to be analyzed (.jar file or the root compilation output folder which contains the .class files in subdirectories).

**--rulesDir &lt;path_to_rules&gt;**

The path to the directory of the CrySL (source code format) rule files. The scanner supports basic directories and zip files. The source code for the rules can be found [here](https://github.com/CROSSINGTUD/Crypto-API-Rules).

### Optional arguments
**--framework &lt;framework&gt;**

The underlying static analysis framework. The framework is used to read the target application and construct the call graph. Possible values:

* `Soot`: Use [Soot](https://github.com/soot-oss/soot) as the underlying framework (default)
* `SootUp`: Use [SootUp](https://github.com/soot-oss/SootUp) as the underlying framework
* `Opal`: Use [Opal](https://github.com/opalj/opal) as the underlying framework

**--cg &lt;call_graph&gt;**

The call graph algorithm to construct the call graph for the analysis. Note that depending on the selected framework, the available algorithms differ. For each framework, `CHA` is the default algorithm. Possible values:

| Framework | Call Graph Algorithms           |
|-----------|---------------------------------|
| Soot      | CHA, RTA, VTA, SPARK, SPARK_LIB |
| SootUp    | CHA, RTA                        |
| Opal      | CHA, RTA, AllocSiteBased        |

**--addClassPath &lt;addClassPath&gt;**

Extend the current classpath the given classpath. This option is relevant if you have rules for classes that are not on the current classpath.

**--reportPath &lt;report_path&gt;**
Relative or absolute path for a directory to write the reports and visualization into.

**--reportFormat &lt;format1,format2,...&gt;**

The format(s) of the report. CogniCrypt<sub>SAST</sub> supports different formats to create an analysis report with the detected errors. Except the `CMD` value, all values require the `--reportPath` argument to be set. Multiple values can be concatenated by a comma (e.g. `CMD,TXT`). Possible values:

* `CMD`: Prints a formatted output to `System.out` (default).
* `TXT`: Creates a file `CryptoAnalysis-Report.txt` in the report path directory that contains a formatted output. The report is equivalent to the `CMD` output.
* `SARIF`: Creates a file `CryptoAnalysis-Report.json` in the report path directory that is formatted in the [SARIF 2.1](https://docs.oasis-open.org/sarif/sarif/v2.1.0/sarif-v2.1.0.html) format.
* `CSV`: Creates a file `CryptoAnalysis-Report.csv` in the report path directory that is formatted in the csv format.
* `CSV_SUMMARY`: Creates a file `CryptoAnalysis-Report-Summary.csv` in the report path directory that is formatted in the csv format. Compared to the `CSV` option, this version contains only a summary of the analysis results, e.g. only the total numbers of each error type.

**--visualization**

Creates a file `visualization.png` in the report path directory that visualizes the connection of detected errors. This argument requires the `--reportPath` argument to be set.

**--ignoreSections &lt;path_to_file&gt;**

Names of packages, classes and methods to be ignored during the analysis. This argument expects path to a file containing one name per line. This option may be useful if a larger section of the program should not be analyzed because they do not contain cryptographic operations. For example, in the following program
```java
public class Example {
    public String queryDatabase(String query) {
        ...
    }
    
    public void encryptDatabaseValues(String query) {
        // Querying the database may contain much logic without cryptographic operations, so we should exclude it
        String queryResult = queryDatabase(query);
        
        // Encrypt the query result
        Cipher cipher = Cipher.getInstance("AES");
        ...
    }
}
```
we query a database and encrypt the query result. However, as the developer, we know that the call to `queryDatabase` contains a lot of logic, but no relevant cryptographic operations. Hence, we can use a file
```
example.Example.queryDatabase
```
to avoid analyzing dataflows within this method. In general, the argument allows the exclusion of packages, classes and methods:

* Method: The name of the method to be excluded. The format is `<fully_qualified_class_name>.<method_name>`.
* Class: The fully qualified name of the class. This excludes the dataflow within all methods in the class.
* Package: The name of a package. This excludes the dataflow within all classes in the package. Use `*` excludes all classes and subpackages (e.g. `de.example.*`).

**--timeout &lt;timeout&gt;**

A timeout in milliseconds for Boomerang queries. In rare cases, Boomerang queries may require a lot of time to finish the pointer analysis. Using this argument allows the abortion of queries after some time. By default, no timeout is used.

## HeadlessJavaScanner with a dependency
CogniCrypt<sub>SAST</sub> provides a simple API that allows its usage inside a program. Its usage does not deviate from the CLI tool; for each argument, there is a corresponding `setter` method. Include the following dependency in your project and instantiate the `HeadlessJavaScanner`:

```
<dependency>
    <groupId>de.fraunhofer.iem</groupId>
    <artifactId>HeadlessJavaScanner</artifactId>
    <version>x.y.z</version>
</dependency>
```

You have two options to instantiate the `HeadlessJavaScanner` and continue with its results:

### Instantiation via CLI structure
A call to `createFromCLISettings(String[])` simulates the instantiation via the CLI. This can look like this:
```java
public class Example {
    
    public static void main(String[] args) {
        String[] myArgs = new String[] {"--appPath", "path/to/app", "--rulesDir", "path/to/rules"};
        HeadlessJavaScanner scanner = HeadlessJavaScanner.createFromCLISettings(myArgs);
        scanner.run();
        
        // Get the errors
        Table<WrappedClass, Method, Set<AbstractError>> errors = scanner.getCollectedErrors();
        
        // Continue with the collected errors
    }
}

```

### Instantiation via constructor
The `HeadlessJavaScanner` has a public constructor that requires the required arguments:
```java
public class Example {
    
    public static void main(String[] args) {
        HeadlessJavaScanner scanner = new HeadlessJavaScanner("path/to/app", "path/to/rules");
        // Some more configuration
        scanner.setFramework(ScannerSettings.Framework.SootUp);
        scanner.setCallGraphAlgorithm(ScannerSettings.CallGraphAlgorithm.RTA);
        scanner.run();

        // Get the errors
        Table<WrappedClass, Method, Set<AbstractError>> errors = scanner.getCollectedErrors();

        // Continue with the collected errors
    }
}
```
