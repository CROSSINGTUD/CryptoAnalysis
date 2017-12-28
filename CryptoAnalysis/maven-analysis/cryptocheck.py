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
TIMEOUT_IN_MINUTES = 30;

groupId = sys.argv[1]
artifactId = sys.argv[2]
version = sys.argv[3]

with open("log-start.txt", 'a') as file:
  file.write(groupId+":"+artifactId+":"+version+"\n")

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



FNULL = open(os.devnull, 'w')
from subprocess import call
call(["mvn", "dependency:build-classpath","-Dmdep.outputFile=" + ALL_JARS],cwd=folder,stdout=FNULL)
call(["mvn", "dependency:build-classpath","-DexcludeTransitive=true","-Dmdep.outputFile="+ ANALYSIS_JAR],cwd=folder,stdout=FNULL)


with open(folder+ALL_JARS, 'r') as file:
  sootClassPath = file.read()

with open(folder+ANALYSIS_JAR, 'r') as file:
  processDir = file.read()

f = open(folder+RESULT_FILE, "w")
print("Starting soot and writing output to " + folder+RESULT_FILE)
with open("log-start-soot.txt", 'a') as file:
  file.write(groupId+":"+artifactId+":"+version+"\n")
cmd = ["java", "-Xss64m", "-Xmx12g", "-cp",CRYPTOSCAN_JAR, "crypto.SourceCryptoScanner", processDir, sootClassPath, RULES_DIR, CALLGRAPH]
try:
	call(cmd, stdout=f,stderr=subprocess.STDOUT, timeout=TIMEOUT_IN_MINUTES*60)
	print("Finished with soot")
	# Write the file out again
	with open("log-success.txt", 'a') as file:
  		file.write(groupId+":"+artifactId+":"+version+"\n")
except TimeoutExpired:
	with open("log-soot-timeout.txt", 'a') as file:
  		file.write(groupId+":"+artifactId+":"+version+"\n")