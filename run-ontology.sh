#!/bin/bash

ROOT=$(cd $(dirname "$0")/.. && pwd)

CFI="$ROOT"/checker-framework-inference

CF="$ROOT"/checker-framework

# SOLVER=ontology.solvers.classic.OntologySolver
SOLVER=ontology.solvers.backend.OntologyConstraintSolver

DEBUG_SOVLER=checkers.inference.solver.DebugSolver

IS_HACK=true

CHECKER=ontology.OntologyChecker

SOLVER="$DEBUG_SOVLER"
# IS_HACK=false

JR3D=~/.m2/repository/net/smert/jReactPhysics3D/0.4.0/jReactPhysics3D-0.4.0.jar
DYN4J_LIB="$ROOT"/3dProjects/dyn4j/lib/
DYN4J="$ROOT"/3dProjects/dyn4j/bin/:"$DYN4J_LIB"/hamcrest.jar:"$DYN4J_LIB"/junit.jar:"$DYN4J_LIB"/gluegen-rt.jar:"$DYN4J_LIB"/jogl-all.jar

CFI_JARS="$CFI"/dist/checker.jar:"$CFI"/dist/plume.jar:"$CFI"/dist/checker-framework-inference.jar

export CLASSPATH="$ROOT"/ontology/bin:"$ROOT"/generic-type-inference-solver/bin:"$DYN4J":"$JR3D"

# $CFI/scripts/inference-dev --checker "$CHECKER" --solver "$SOLVER" --hacks="$IS_HACK" -m INFER "$@"
mkdir ./annotated

# $CFI/scripts/inference-dev --checker "$CHECKER" --solver "$SOLVER" --solverArgs="backEndType=maxsatbackend.Lingeling,solveInParallel=false" --cfArgs="-AatfCacheSize=3000" --hacks="$IS_HACK" -m ROUNDTRIP -afud ./annotated "$@"
$CFI/scripts/inference-dev --checker "$CHECKER" --solver "$SOLVER" --cfArgs="-AatfCacheSize=3000" --hacks="$IS_HACK" -m ROUNDTRIP -afud ./annotated "$@"
# $CFI/scripts/inference-dev --checker "$CHECKER" --solver "$SOLVER" --cfArgs="-AatfCacheSize=3000" --hacks="$IS_HACK" -m TYPECHECK -afud ./annotated "$@"
