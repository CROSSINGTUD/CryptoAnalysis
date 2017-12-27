#!/usr/bin/python

import sys, os, errno, subprocess
from shutil import copyfile
POM_TEMPLATE = "pom.template"

ALL_JARS = "classPath.txt"
ANALYSIS_JAR = "applicationClassPath.txt"
CRYPTOSCAN_JAR = "../../CryptoAnalysis/build/CryptoAnalysis-0.0.1-SNAPSHOT-jar-with-dependencies.jar"
RULES_DIR = "../../CryptoAnalysis/src/test/resources"
CALLGRAPH = "SPARK-LIBRARY"
POM_XML = "pom.xml"
RESULT_FILE = "cognicrypt-results.txt"
ANALYSIS_FOLDER_PREFIX = "output"

groupId = sys.argv[1]
artifactId = sys.argv[2]
version = sys.argv[3]

folder = ANALYSIS_FOLDER_PREFIX + "/" + groupId + "/"+artifactId+"/"+version+"/"
if not os.path.exists(folder):
    os.makedirs(folder)
pom = folder + POM_XML

copyfile(POM_TEMPLATE, pom)

# Read in the file
with open(pom, 'r') as file:
  filedata = file.read()

# Replace the target string
filedata = filedata.replace("${groupId}", groupId)
filedata = filedata.replace("${artifactId}", artifactId)
filedata = filedata.replace("${version}", version)

# Write the file out again
with open(pom, 'w') as file:
  file.write(filedata)



from subprocess import call
call(["mvn", "dependency:build-classpath","-Dmdep.outputFile=" + ALL_JARS],cwd=folder)
call(["mvn", "dependency:build-classpath","-DexcludeTransitive=true","-Dmdep.outputFile="+ ANALYSIS_JAR],cwd=folder)


with open(folder+ALL_JARS, 'r') as file:
  sootClassPath = file.read()

with open(folder+ANALYSIS_JAR, 'r') as file:
  processDir = file.read()

f = open(folder+RESULT_FILE, "w")
print("Starting soot and writing output to " + folder+RESULT_FILE)
call(["java", "-Xss64m", "-Xmx12g", "-cp",CRYPTOSCAN_JAR, "crypto.SourceCryptoScanner", processDir, sootClassPath, RULES_DIR, CALLGRAPH], stdout=f,stderr=subprocess.STDOUT, timeout=1800)
print("Finished with soot")