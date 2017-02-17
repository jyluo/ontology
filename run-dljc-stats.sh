#!/bin/bash
ONTOLOGY_DIR=$(cd $(dirname "$0") && pwd)

#setup env variables
. $ONTOLOGY_DIR/env-setup.sh

build_cmd=''
while [ "$#" -gt 0 ]
do
    build_cmd="$build_cmd $1"
    shift
done

echo "time run-dljc.sh $build_cmd 1> ontology.log 2> ontology-error.log"

time $JSR308/ontology/run-dljc.sh $build_cmd 1> ontology.log 2> ontology-error.log

### collecting statistics

