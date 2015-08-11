#!/bin/bash

# This script works for the default folders
#  output.arff should be on output/output.arff
#  supplementary folder should be output/supplementary
#
# USAGE: 
#  $ bash cluster_nav_projects_diff.sh <cluster number>
#  $ bash cluster_nav_project_diff.sh 84
#  or 
#  $ ./cluster_nav_project_diff.sh 84

DIFF_PROGRAM=meld
ARFF_FILE=../output/output.arff
SUPPLEMENTARY_FOLDER=../output/supplementary/
CLIPBOARD_PROGRAM=xclip
BROWSER=

# Get all attributes names
attributes=($(cat $ARFF_FILE | grep @attribute | awk -F " " '{print $2}'))

# Get all projects that has instances on this cluster
projects=($(cat $ARFF_FILE | grep -w "cluster"$1 | grep -v @attribute | awk -F "," '{print $3}' | sort | uniq))

echo "------------ Exploring ${#projects[@]} projects ------------"
echo ${projects[@]}

# Iterate over projects
for project in "${projects[@]}";
do
	# Get the ids of all instances of this project on this cluster
    ids_from_this_project=($(cat $ARFF_FILE | grep -w "cluster"$1 | grep -v @attribute | grep ,$project, | awk -F "," '{print $2}'))

    echo "--------- Project: ${project} (${#ids_from_this_project[@]} commits) ---------"

    # Iterate over th eids
    for id in "${ids_from_this_project[@]}";
    do
	    # Get feature vector
	    vector=$(cat $ARFF_FILE | grep -w "cluster"$1 | grep -v attribute | awk -F "," '$2=='$id)

			# Find the ids of the ProjectHomepage and RepairedCommitID
			projectHomepageIndex=-1
			repairedCommitIDIndex=-1
			i=0
			for attribute in "${attributes[@]}";
			do
				if [ $attribute = "ProjectHomepage" ]; then
					projectHomepageIndex=$i
				fi
				if [ $attribute = "RepairedCommitID" ]; then
					repairedCommitIDIndex=$i
				fi
				i=$((i+1))
			done

			# Build the URL for the diff on GitHub
			if [ $projectHomepageIndex -ge 0 -a $repairedCommitIDIndex -ge 0 ]; then
				values=($(echo $vector | tr "," " "))
				commit_url="${values[$projectHomepageIndex]}/commit/${values[$repairedCommitIDIndex]}"
				echo "Commit URL - $commit_url"
			else
				commit_url=
			fi

	    # Iterate over features. This is only for printing
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

	    # LINUX SPECIFIC: copy ID and projectID to clipboard. 
	    # \t so when we paste on Google Docs it is on 2 different cells
	    printf "$id \t $project" | $CLIPBOARD_PROGRAM -selection clipboard

			# Open the link to the GitHub diff
			if [ ! -z "$BROWSER" -a ! -z "$commit_url" ]; then
				$BROWSER $commit_url 
			fi

	    # Close *all* running instances of DIFF_PROGRAM
	    # TODO: Close only the last one
	    killall $DIFF_PROGRAM > /dev/null 2>&1

	    # Open diff program and redirect possible error outputs to /dev/null
	    $DIFF_PROGRAM ${SUPPLEMENTARY_FOLDER}${id}_src.js ${SUPPLEMENTARY_FOLDER}${id}_dst.js 2> /dev/null &

	    # Print options
	    echo ""
	    echo "[n] for [n]ext instance in same project."
	    echo "[c] or [enter] for [c]ontinue to next project."
	    echo "[q] for [q]uit"
	    read -p "" input
	    case $input in
	        [n]* ) echo "" ;;
			[c]* ) break;;
	        [q]* ) exit;;
	        * ) break;;
	    esac
	done
done

# Close *all* running instances of DIFF_PROGRAM
# TODO: Close only the last one
killall $DIFF_PROGRAM > /dev/null 2>&1
