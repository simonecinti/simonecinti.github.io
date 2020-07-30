#!/bin/sh
# ----------------------------------------------------------------
# zfind.sh  -  a zip finder bash script util
#
# usage:  ./zfind.sh "filename-or-wildcard" "pattern-to-search" 
#
# author: Simone Cinti - 2020-07-30
#
# ----------------------------------------------------------------

if [ "$#" -ne 2 ]; then
    echo 'zfind - illegal number of parameters. Usage: ./zfind.sh "filename-or-wildcard" "pattern-to-search"'
    exit -1
fi

for f in $1; do echo "$f: "; unzip -l $f | grep -i $2; done

exit 0
