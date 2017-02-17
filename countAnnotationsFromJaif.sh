#!/bin/bash
ONTOLOGY_DIR=$(cd $(dirname "$0") && pwd)

project=$1

if [ -d $project/annotated ] ; then
	echo "annotations in java file: "
	find $project -name *.java | xargs grep -l "@Ontology(" | grep "annotated" | xargs grep -o "@Ontology(" | wc -l
	echo "ontology values in java file: "
	find $project -name *.java | xargs grep -l "@Ontology(" | grep "annotated" | xargs grep -o "OntologyValue.ontology" | wc -l
	echo "in jaif file: "
	find $project -name default.jaif | xargs grep -o "@ontology.qual" | wc -l
	echo "Files, blank, comment, source LOC total:"
	cloc $project | grep Java
	echo "Files, blank, comment, source LOC in annotated:"
	cloc $project/annotated | grep Java
else
	echo $project "doesn't have an annotated directory"

fi

### collecting statistics

