package org.bdm;

import org.bdm.ontology.ABOX;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ABOX.loadArticles();
    }
//    public static void main(String[] args) {
//        // Create an ontology model
//        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
//
//        // Define the namespace prefixes
//        model.setNsPrefix("ex", "http://example.org#");
//        model.setNsPrefix("owl", OWL.NS);
//        model.setNsPrefix("rdf", RDF.getURI());
//        model.setNsPrefix("rdfs", RDFS.getURI());
//
//        // Create classes
//        OntClass personClass = model.createClass("ex:Person");
//        OntClass cityClass = model.createClass("ex:City");
//
//        // Create a property
//        OntProperty hasCityProperty = model.createObjectProperty("ex:hasCity");
//
//        // Create a restriction
//        Restriction restriction = model.createRestriction(null, hasCityProperty);
////        restriction.addCardinality(1);
//
//        // Add a value constraint to the restriction
//        Literal specificValue = model.createTypedLiteral("Specific Value");
////        restriction.asHasValueRestriction(specificValue);
//
//        // Associate the restrictxion with the Person class
//        personClass.addSuperClass(restriction);
//        // Save the ontology to a file
//        model.write(System.out, "N-TRIPLE");
////        model.write(writerStream, "N-TRIPLE");
//    }
}