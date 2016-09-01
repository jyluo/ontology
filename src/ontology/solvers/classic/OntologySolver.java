package ontology.solvers.classic;

import ontology.qual.Ontology;
import ontology.qual.OntologyValue;
import ontology.util.OntologyUtils;

import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.javacutil.AnnotationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.util.Elements;

import checkers.inference.InferenceSolution;
import checkers.inference.InferenceSolver;
import checkers.inference.model.Constraint;
import checkers.inference.model.Slot;
import constraintgraph.ConstraintGraph;
import constraintgraph.GraphBuilder;
import constraintgraph.Vertex;

public class OntologySolver implements InferenceSolver {

    protected AnnotationMirror Ontology;

    @Override
    public InferenceSolution solve(Map<String, String> configuration,
            Collection<Slot> slots, Collection<Constraint> constraints,
            QualifierHierarchy qualHierarchy,
            ProcessingEnvironment processingEnvironment) {

        Elements elements = processingEnvironment.getElementUtils();
        Ontology = AnnotationUtils.fromClass(elements, Ontology.class);
        GraphBuilder graphBuilder = new GraphBuilder(slots, constraints, true);
        ConstraintGraph constraintGraph = graphBuilder.buildGraph();

        List<SequenceSolver> sequenceSolvers = new ArrayList<>();
        // Configure datatype solvers
        for (Map.Entry<Vertex, Set<Constraint>> entry : constraintGraph.getConstantPath().entrySet()) {
            AnnotationMirror anno = entry.getKey().getValue();
            if (AnnotationUtils.areSameIgnoringValues(anno, Ontology)) {
                OntologyValue[] ontologyValues = OntologyUtils.getOntologyValues(anno);
                if (ontologyValues.length == 1) {
                    SequenceSolver solver = new SequenceSolver(ontologyValues[0], entry.getValue(),getSerializer(ontologyValues[0]));
                    sequenceSolvers.add(solver);
                }
            }
        }

        List<SequenceSolution> solutions = new ArrayList<>();
        try {
            if (sequenceSolvers.size() > 0) {
                solutions = solveInparallel(sequenceSolvers);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return getMergedSolution(processingEnvironment, solutions);
    }

    private List<SequenceSolution> solveInparallel(List<SequenceSolver> ontologySolvers)
            throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(ontologySolvers.size());

        List<Future<SequenceSolution>> futures = new ArrayList<Future<SequenceSolution>>();

        for (final SequenceSolver solver : ontologySolvers) {
            Callable<SequenceSolution> callable = new Callable<SequenceSolution>() {
                @Override
                public SequenceSolution call() throws Exception {
                    return solver.solve();
                }
            };
            futures.add(service.submit(callable));
        }
        service.shutdown();

        List<SequenceSolution> solutions = new ArrayList<>();
        for (Future<SequenceSolution> future : futures) {
            solutions.add(future.get());
        }
        return solutions;
    }

    protected OntologySerializer getSerializer(OntologyValue ontologyValue) {
        return new OntologySerializer(ontologyValue);
    }

    protected InferenceSolution getMergedSolution(ProcessingEnvironment processingEnvironment,
            List<SequenceSolution> solutions) {
        return new OntologySolution(solutions, processingEnvironment);
    }
}
