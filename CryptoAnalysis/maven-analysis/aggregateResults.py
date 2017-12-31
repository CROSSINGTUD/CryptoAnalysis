#!/usr/bin/python

import os
RESULT_DIR = "results"
RESULT_FILE = "results.csv"

directory = os.fsencode(RESULT_DIR)
header = True;

for file in os.listdir(directory):
    filename = os.fsdecode(file)
    if filename.endswith(".csv"): 
        with open(RESULT_DIR+"/"+filename, 'r') as f:
            lines = f.readlines()
            lineCount = 0
            for line in lines:
                if header or lineCount == 1:
                    with open(RESULT_FILE, 'a') as o:
                        o.write(line)
                if header:
                    header = False
                lineCount +=1 