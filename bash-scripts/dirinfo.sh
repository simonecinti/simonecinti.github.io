#!/bin/sh

workdir="$PWD"

if [ $# -gt 0 ]; then
	if [ -d "$@" ]; then
		workdir="$@"
	else
		echo "ERROR: '$@' is not a valid directory."
		exit 1
	fi
fi
if [ -d "$workdir" ]; then 

	printf "\\ttotal files: "
	filesCount=`find $workdir -type f | wc -l`
	printf "$filesCount\\r\\n"
	
	printf "\\ttotal sub-directories: "
	dirsCount=`find $workdir -type d | wc -l`
	dirsCount=$((dirsCount - 1))
	printf "$dirsCount\\r\\n"
	
	printf "\\ttotal size: "
	totalSize=`du $workdir -cb | tail -1`
	printf "$totalSize bytes\\r\\n"

	echo
fi
exit 0
