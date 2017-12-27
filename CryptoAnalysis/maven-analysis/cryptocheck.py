#!/usr/bin/python

import sys
from shutil import copyfile
POM_TEMPLATE = "pom.template"
POM_XML = "pom.xml"
ALL_JARS = "cp.txt"
ANALYSIS_JAR = "processDir.txt"
CRYPTOSCAN_JAR = "../../CryptoAnalysis/build/CryptoAnalysis-0.0.1-SNAPSHOT-jar-with-dependencies.jar"
RULES_DIR = "../../CryptoAnalysis/src/test/resources"
CALLGRAPH = "SPARK-LIBRARY"

groupId = sys.argv[1]
artifactId = sys.argv[2]
version = sys.argv[3]

copyfile(POM_TEMPLATE, POM_XML)

# Read in the file
with open(POM_XML, 'r') as file:
  filedata = file.read()

# Replace the target string
filedata = filedata.replace("${groupId}", groupId)
filedata = filedata.replace("${artifactId}", artifactId)
filedata = filedata.replace("${version}", version)

# Write the file out again
with open(POM_XML, 'w') as file:
  file.write(filedata)



from subprocess import call
call(["mvn", "dependency:build-classpath","-Dmdep.outputFile=" + ALL_JARS])
call(["mvn", "dependency:build-classpath","-DexcludeTransitive=true","-Dmdep.outputFile="+ ANALYSIS_JAR])


with open(ALL_JARS, 'r') as file:
  sootClassPath = file.read()

with open(ANALYSIS_JAR, 'r') as file:
  processDir = file.read()

call(["java", "-Xss64m", "-Xmx12g", "-cp",CRYPTOSCAN_JAR, "crypto.SourceCryptoScanner", processDir, sootClassPath, RULES_DIR, CALLGRAPH])