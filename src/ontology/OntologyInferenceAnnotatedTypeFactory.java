package ontology;

import javax.lang.model.element.AnnotationMirror;

import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.type.treeannotator.ImplicitsTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy;

import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;

import checkers.inference.ConstraintManager;
import checkers.inference.InferenceAnnotatedTypeFactory;
import checkers.inference.InferenceChecker;
import checkers.inference.InferenceQualifierHierarchy;
import checkers.inference.InferenceTreeAnnotator;
import checkers.inference.InferrableChecker;
import checkers.inference.SlotManager;
import checkers.inference.VariableAnnotator;
import checkers.inference.model.ConstantSlot;
import checkers.inference.model.PreferenceConstraint;
import checkers.inference.model.Slot;
import checkers.inference.model.VariableSlot;
import ontology.qual.OntologyValue;
import ontology.util.OntologyUtils;

public class OntologyInferenceAnnotatedTypeFactory extends InferenceAnnotatedTypeFactory {

    public OntologyInferenceAnnotatedTypeFactory(InferenceChecker inferenceChecker, boolean withCombineConstraints,
            BaseAnnotatedTypeFactory realTypeFactory, InferrableChecker realChecker, SlotManager slotManager,
            ConstraintManager constraintManager) {
        super(inferenceChecker, withCombineConstraints, realTypeFactory, realChecker, slotManager, constraintManager);
    }

    @Override
    public TreeAnnotator createTreeAnnotator() {
        return new ListTreeAnnotator(new ImplicitsTreeAnnotator(this),
                new OntologyInferenceTreeAnnotator(this, realChecker,
                        realTypeFactory, variableAnnotator, slotManager));
    }

    @Override
    public QualifierHierarchy createQualifierHierarchy( MultiGraphQualifierHierarchy.MultiGraphFactory factory ) {
        return new OntologyInferenceQualifierHierarchy(factory);
    }

    public class OntologyInferenceQualifierHierarchy extends InferenceQualifierHierarchy {

        public OntologyInferenceQualifierHierarchy(MultiGraphFactory multiGraphFactory) {
            super(multiGraphFactory);
        }

        @Override
        public boolean isSubtype(final AnnotationMirror subtype, final AnnotationMirror supertype) {
            super.isSubtype(subtype, supertype);
            if (!isVarAnnot(subtype) || !isVarAnnot(supertype)) {
                return true;
            }

            final Slot subSlot   = slotMgr.getSlot(subtype);
            final Slot superSlot = slotMgr.getSlot(supertype);
            if (subSlot instanceof ConstantSlot && superSlot instanceof VariableSlot &&
                    ! (superSlot instanceof ConstantSlot)) {
//                System.out.println("\n--- Test ---: add preference: " + supertype + " to " + subSlot);
                constraintMgr.add(new PreferenceConstraint((VariableSlot) superSlot, (ConstantSlot) subSlot, 50));
            }

            return true;
        }
    }

    public class OntologyInferenceTreeAnnotator extends InferenceTreeAnnotator {

        private final VariableAnnotator variableAnnotator;

        public OntologyInferenceTreeAnnotator(InferenceAnnotatedTypeFactory atypeFactory,
                InferrableChecker realChecker, AnnotatedTypeFactory realAnnotatedTypeFactory,
                VariableAnnotator variableAnnotator, SlotManager slotManager) {
            super(atypeFactory, realChecker, realAnnotatedTypeFactory, variableAnnotator, slotManager);
            this.variableAnnotator = variableAnnotator;
        }

        @Override
        public Void visitNewClass(final NewClassTree newClassTree, final AnnotatedTypeMirror atm) {
            switch (OntologyUtils.determineOntologyValue(atm.getUnderlyingType())) {
            case SEQUENCE: {
                AnnotationMirror anno = OntologyUtils.createOntologyAnnotationByValues(processingEnv, OntologyValue.SEQUENCE);
                ConstantSlot cs = variableAnnotator.createConstant(anno, newClassTree);
                atm.replaceAnnotation(cs.getValue());
                }
                break;

            case TOP:
            default:
                break;
            }
            variableAnnotator.visit(atm, newClassTree.getIdentifier());
            return null;
        }

        @Override
        public Void visitNewArray(final NewArrayTree newArrayTree, final AnnotatedTypeMirror atm) {
            AnnotationMirror anno = OntologyUtils.createOntologyAnnotationByValues(processingEnv, OntologyValue.SEQUENCE);
            ConstantSlot cs = variableAnnotator.createConstant(anno, newArrayTree);
            atm.replaceAnnotation(cs.getValue());
            variableAnnotator.visit(atm, newArrayTree);
            return null;
        }

        @Override
        public Void visitParameterizedType(final ParameterizedTypeTree param, final AnnotatedTypeMirror atm) {
            TreePath path = atypeFactory.getPath(param);
            if (path != null) {
                final TreePath parentPath = path.getParentPath();
                final Tree parentNode = parentPath.getLeaf();
                if (!parentNode.getKind().equals(Kind.NEW_CLASS)) {
                    variableAnnotator.visit(atm, param);
                }
            }
            return null;
        }
    }
}
