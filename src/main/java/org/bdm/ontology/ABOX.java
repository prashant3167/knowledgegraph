package org.bdm.ontology;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.bdm.utils.constants;

/*import com.sdm.knowledge_graph.common.constants;
import com.sdm.knowledge_graph.common.utils;*/


public class ABOX {
    /**
     * Create schema of
     */
    public static void creteSchema() {

        OntModel model = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM_RDFS_INF );
        OntClass person = model.createClass( constants.BASE_URI.concat("Person") );
        OntClass author = model.createClass( constants.BASE_URI.concat("Author") );
        OntClass chair = model.createClass( constants.BASE_URI.concat("Chair") );
        OntClass editor = model.createClass( constants.BASE_URI.concat("Editor") );
        OntClass reviewer = model.createClass( constants.BASE_URI.concat("Reviewer") );

        /* /** / Person subclass */





        person.addSubClass( author );
        person.addSubClass( chair );
        person.addSubClass( editor );
        person.addSubClass( reviewer );

        System.out.println("I am here");
    }
}
