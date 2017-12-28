#!/usr/bin/python


import sys
from subprocess import call
from multiprocessing import Process, Pool

PROJECTS = "maven-central-index.txt"
MOUNT_POINT = "/Users/johannesspath/Arbeit/Fraunhofer/CryptoAnalysis/CryptoAnalysis/maven-analysis"
def cognicryptScan(arg):
	groupId = arg[0]
	artifactId = arg[1]
	version = arg[2]
	print("Analyzing " + groupId +" : " + artifactId + " : " + version)
	call(["python3", "cryptocheck.py", groupId, artifactId, version])

# Read in the file
with open(PROJECTS, 'r') as file:
	lines = file.readlines()
 
args = []
for line in lines:
	if line.startswith("#"): 
		continue
	line = line.replace("'","").strip()
	groupId,artifactId,_,version = line.split(":")
	args.append([groupId,artifactId,version])

if __name__ == '__main__':
	with Pool(10) as p:
		p.map(cognicryptScan, args)
	