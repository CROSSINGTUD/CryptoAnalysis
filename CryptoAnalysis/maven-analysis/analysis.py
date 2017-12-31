#!/usr/bin/python

import sys, os, json, requests
from subprocess import call
from multiprocessing import Process, Pool

PROJECTS = "maven-central-index.txt"

def cognicryptScan(arg):
	global started
	groupId = arg[0]
	artifactId = arg[1]
	version = arg[2]
	print("Analyzing " + groupId +" : " + artifactId + " : " + version)
	call(["python3", "cryptocheck.py", groupId, artifactId, version])


def getArtifactVersion(groupId, artifactId)
	url = "http://search.maven.org/solrsearch/select?q=g:{}+AND+a:{}&rows=20&wt=json"
    resp = requests.get(url=url.format(groupId,artifactId))
    data = json.loads(resp.text)
    version = data["response"]["docs"][0]["latestVersion"];
    return version

# Read in the file
with open(PROJECTS, 'r') as file:
	lines = file.readlines()
 
args = []
for line in lines:
	if line.startswith("#"): 
		continue
	line = line.strip()
	groupId,artifactId = line.split(":")
	try:
		version = getArtifactVersion(groupId,artifactId)
		args.append([groupId,artifactId,version])
	except Exception
		print("Failed to get version number "+ groupId + " "+ artifactId)

if __name__ == '__main__':
	with Pool(10) as p:
		p.map(cognicryptScan, args)
		

