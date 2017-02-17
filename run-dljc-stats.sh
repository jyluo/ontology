#!/bin/bash
ONTOLOGY_DIR=$(cd $(dirname "$0") && pwd)

#setup env variables
. $ONTOLOGY_DIR/env-setup.sh

project=$1
build_cmd=''
shift
while [ "$#" -gt 0 ]
do
    build_cmd="$build_cmd $1"
    shift
done

# CD into the project directory, then run the following script
cd $project

echo "$project> run-dljc.sh $build_cmd 1> ontology.log 2> ontology-error.log"

$JSR308/ontology/run-dljc.sh $build_cmd 1> ontology.log 2> ontology-error.log

### collecting statistics

