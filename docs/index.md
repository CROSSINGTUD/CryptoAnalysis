# Welcome to the Documentation of CogniCrypt<sub>SAST</sub>

CogniCrypt<sub>SAST</sub> is the static analysis component for [CogniCrypt](https://eclipse.dev/cognicrypt/). It takes a set of rules written in the specification language CrySL as input, performs a static analysis based on these specifications and creates a report with all violations.

CogniCrypt<sub>SAST</sub> provides the following features:

- A context-sensitive, field-sensitive and flow-sensitive typestate analysis using [IDEal](https://github.com/secure-software-engineering/Boomerang)
- A context-sensitive, field-sensitive and flow-sensitive pointer analysis using [Boomerang](https://github.com/secure-software-engineering/Boomerang)
- A CLI and API to analyze Java and Android applications
- Support for the static analysis frameworks [Soot](https://github.com/soot-oss/soot), [SootUp](https://github.com/soot-oss/SootUp) and [Opal](https://github.com/opalj/opal)
- A wide range of different error types that explain the violations of CrySL specifications
- An API to configure your own analysis

We provide examples and a quick start [here](examples.md)
