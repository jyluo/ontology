// import net.smert.jreactphysics3d.body
import ontology.qual.Ontology;
import ontology.qual.*;
import java.util.*;

public class Test {
    @Ontology(values={OntologyValue.FORCE_3D})
    Vector force;

    // @Ontology(values={OntologyValue.VELOCITY_3D})
    // Vector veclocity;



    void m (Vector v) {
        force.add(v);
    }

    // void m2 (Vector v) {
    //     veclocity.add(v);
    // }
}

class Vector {
    int x;

    @PolyOntology Vector add(@PolyOntology Vector this, @PolyOntology Vector v) {
        this.x += v.x;
        return this;
    }
}