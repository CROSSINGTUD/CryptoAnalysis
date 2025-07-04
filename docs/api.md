# API of CryptoAnalysis
The `CryptoAnalysis` module contains the static analysis components. It allows the configuration of CogniCrypt<sub>SAST</sub> to match your own needs. The following example shows an alternative configuration with SootUp.

## Basic example idea
For research purposes, we are interested in the number of triggered Boomerang queries and the accumulated time to solve the queries. Thereby, we consider only SootUp as the underlying static analysis framework and `RTA` as the call graph algorithm.

## Including the dependencies
Include the `HeadlessJavaScanner` module in your project:

```
<dependency>
    <groupId>de.fraunhofer.iem</groupId>
    <artifactId>HeadlessJavaScanner</artifactId>
    <version>x.y.z</version>
</dependency>
```

## Defining a listener
`CryptoAnalysis` defines a set of listeners that are informed during the analysis. There are 3 types of listeners:
- `IAnalysisListener`: Listener that tracks events during the analysis (e.g. the start of the typestate analysis, the analysis of seeds etc.)
- `IResultsListener`: Listener that tracks intermediate analysis results (e.g. typestate analysis results, Boomerang query results etc.)
- `IErrorListener`: Listener that tracks the report of detected errors

Since we are interested in the starting and end points of solving Boomerang queries, we define an `IAnalysisListener` and consider the methods that are called when Boomerang queries are triggered:

```java
public class QueryTrackingListener implements IAnalysisListener {

    // Keep track of the triggered queries
    private int triggeredQueries = 0;
    // Watch to measure the accumulated time for each query
    private Stopwatch watch = Stopwatch.createUnstarted();

    public int getTriggeredQueries() {
        return triggeredQueries;
    }

    public String getWatchTime() {
        return watch.toString();
    }

    @Override
    public void beforeTriggeringBoomerangQuery(BackwardQuery query) {
        watch.start();
    }

    @Override
    public void afterTriggeringBoomerangQuery(BackwardQuery query) {
        watch.stop();
        triggeredQueries++;
    }
}
```

## Set up and run the HeadlessAndroidScanner
We can use the API of the `HeadlessJavaScanner` to configure CogniCrypt<sub>SAST</sub> to run with our own listener:

```java
public class BoomerangQueryTracking {

    public static void main(String[] args) {
        HeadlessJavaScanner scanner = new HeadlessJavaScanner("path/to/app", "path/to/rules");
        
        // We are only interested in SootUp with RTA
        scanner.setFramework(ScannerSettings.Framework.SootUp);
        scanner.setCallGraphAlgorithm(ScannerSettings.CallGraphAlgorithm.RTA);

        // We do not need a report
        scanner.setReportFormats(Collections.emptySet());

        // Add our own listener
        QueryTrackingListener listener = new QueryTrackingListener();
        scanner.addAnalysisListener(listener);

        // Run the scanner and print the results
        scanner.run();
        System.out.println("Triggered Boomerang queries: " + listener.getTriggeredQueries());
        System.out.println("Time for solving all queries: " + listener.getWatchTime());
    }
}
```