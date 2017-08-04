# CryptoMisuse Analysis

This folder contains a prototype of a static analysis that detects incorrect usages of the Java cryptographic APIs.
To run the analysis (on the example project `CryptoMisuse`) execute the following command in the current directory.

`java -cp ideal-crossing.jar crypto.SourceCryptoScanner $(pwd)\\CryptoMisuseExample\\bin myapp.Main`

or on a unix machine:

`java -cp ideal-crossing.jar crypto.SourceCryptoScanner $(pwd)/CryptoMisuseExample/bin myapp.Main`

It outputs error that are not according to the rule specification. The rule specifications are found in the folder 'rules'.
Within the CryptoMisuseExample project, it reports that the developer uses 
		`this.cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");`

Here a misuse is reported. One should not use AES in ECB mode, but in CBC instead.