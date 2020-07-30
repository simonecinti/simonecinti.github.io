#!/bin/sh
# ----------------------------------------------------------------
# zfind.sh  -  a zip finder bash script util
# ----------------------------------------------------------------
#
# usage:  ./zfind.sh "filename-or-wildcard" "pattern-to-search" 
#
# example: ./zfind.sh "*.jar" ".class"
#         Gets lists all file names having ".class" inside the
#         "*.jar" archives 
#          
#
# author: Simone Cinti - 2020-07-30
# ----------------------------------------------------------------

if [ "$#" -ne 2 ]; then
    echo 'zfind.sh - illegal number of parameters. Usage: ./zfind.sh "filename-or-wildcard" "pattern-to-search"'
    exit -1
fi

for f in $1; do echo "$f: "; unzip -l $f | grep -i $2; done

exit 0
