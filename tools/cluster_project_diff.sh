#!/bin/bash

# This script works for the default folders
#  output.arff should be on output/output.arff
#  supplementary folder should be output/supplementary
#
# USAGE: 
#  $ bash cluster_project_diff.sh <cluster number> <project id>
#  $ bash cluster_project_diff.sh 84 popcorn-time
#  or 
#  $ ./cluster_project_diff.sh 84 popcorn-time

DIFF_PROGRAM=meld
ARFF_FILE=../output/output.arff
SUPPLEMENTARY_FOLDER=../output/supplementary/

# Get all attributes names
attributes=($(cat $ARFF_FILE | grep @attribute | awk -F " " '{print $2}'))

# Get ids of instances
ids=$(cat $ARFF_FILE | grep -w "cluster"$1 | grep -v @attribute | grep $2 | awk -F "," '{print $2}')

# Iterate over instances
for id in $ids;
do    
    echo "----------------"

    # Get feature vector
    vector=$(cat $ARFF_FILE | grep -w "cluster"$1 | grep -v attribute | awk -F "," '$2=='$id)

    # Iterate over features
    # If is not 0, print attribute name and value
    i=0
    for value in $(echo $vector | tr "," " ");
    do
        if [ "$value" != "0" ] 
        then
            echo "${attributes[$i]}" - $value
        fi

        i=$((i+1))
    done 

    # Open diff program and redirect possible error outputs to /dev/null
    $DIFF_PROGRAM ${SUPPLEMENTARY_FOLDER}${id}_src.js ${SUPPLEMENTARY_FOLDER}${id}_dst.js 2> /dev/null
done
