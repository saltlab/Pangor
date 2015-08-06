#!/bin/bash

# This script works for the default folders
#  output.arff should be on output/output.arff
#  supplementary folder should be output/supplementary
#
# USAGE: 
#  $ bash instance_diff.sh <instance number>
#  $ bash instance_diff.sh 84
#  or 
#  $ ./instance_diff.sh 84

DIFF_PROGRAM=meld
ARFF_FILE=../output/output.arff
SUPPLEMENTARY_FOLDER=../output/supplementary/

# Get all attributes names
attributes=($(cat $ARFF_FILE | grep @attribute | awk -F " " '{print $2}'))

# Get feature vector
vector=$(cat $ARFF_FILE | grep -v @attribute | awk -F "," '$2=='$1)


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
$DIFF_PROGRAM ${SUPPLEMENTARY_FOLDER}$1_src.js ${SUPPLEMENTARY_FOLDER}$1_dst.js 2> /dev/null
