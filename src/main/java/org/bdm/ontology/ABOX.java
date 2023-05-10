package org.bdm.ontology;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.bdm.utils.Constants;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;

public class ABOX {
    public static void loadArticles() throws IOException {
        Model model = ModelFactory.createDefaultModel().read(Constants.TBOX_MODEL_PATH);
        OntModel ontModel = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM_RDFS_INF, model);
        System.out.println(ontModel.listClasses());
        //Classes
        OntClass paperClass = ontModel.getOntClass( Constants.BASE_URI.concat("Paper") );
        OntClass fullPaperClass = ontModel.getOntClass( Constants.BASE_URI.concat("FullPaper") );
        OntClass shortPaperClass = ontModel.getOntClass( Constants.BASE_URI.concat("ShortPaper") );
        OntClass demoPaperClass = ontModel.getOntClass( Constants.BASE_URI.concat("DemoPaper") );
        OntClass posterClass = ontModel.getOntClass( Constants.BASE_URI.concat("Poster") );
        OntClass authorClass = ontModel.getOntClass( Constants.BASE_URI.concat("Author") );
        OntClass volumeClass = ontModel.getOntClass( Constants.BASE_URI.concat("Volume") );
        OntClass journalClass = ontModel.getOntClass( Constants.BASE_URI.concat("Journal") );
        OntClass reviewerClass = ontModel.getOntClass( Constants.BASE_URI.concat("Reviewer") );
        OntClass reviewClass = ontModel.getOntClass( Constants.BASE_URI.concat("Review") );
        OntClass researchAreaClass = ontModel.getOntClass( Constants.BASE_URI.concat("ResearchArea") );
        OntClass editorClass = ontModel.getOntClass( Constants.BASE_URI.concat("Editor") );

        //Object Properties
        OntProperty wrotePaper = ontModel.getOntProperty( Constants.BASE_URI.concat("wrotepaper") );
        OntProperty hasVolume = ontModel.getOntProperty( Constants.BASE_URI.concat("hasvolume") );
        OntProperty publishedIn = ontModel.getOntProperty( Constants.BASE_URI.concat("publishedin") );
        OntProperty submittedTo = ontModel.getOntProperty( Constants.BASE_URI.concat("submittedto") );
        OntProperty handlesJournal = ontModel.getOntProperty( Constants.BASE_URI.concat("handlesjournal") );
        OntProperty venueRelatedTo = ontModel.getOntProperty( Constants.BASE_URI.concat("venuerelatedto") );
        OntProperty paperRelatedTo = ontModel.getOntProperty( Constants.BASE_URI.concat("paperrelatedto") );
        OntProperty wroteReview = ontModel.getOntProperty( Constants.BASE_URI.concat("wrotereview") );
        OntProperty reviewFor = ontModel.getOntProperty( Constants.BASE_URI.concat("reviewfor") );

        //Datatype Properties
        DatatypeProperty text = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("text"));
        DatatypeProperty decision = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("decision"));

        //Read journal.csv
        BufferedReader csvReader = new BufferedReader(new FileReader(Constants.ARTICLES_PATH));
        CSVParser parser = CSVFormat.DEFAULT.withDelimiter(',').withHeader().parse(csvReader);

        int cnt = 0;
        for(CSVRecord record : parser) {

            // Article (paper)
            String paperTitle = URLEncoder.encode(record.get("title"));
            String paperType = record.get("paper_type");
            Individual paperInd = null;
                if (paperType.equals("Full Paper"))
                    paperInd = fullPaperClass.createIndividual(Constants.BASE_URI.concat(paperTitle));
                else if (paperType.equals("Short Paper"))
                    paperInd = shortPaperClass.createIndividual(Constants.BASE_URI.concat(paperTitle));
                else if (paperType.equals("Demo Paper"))
                    paperInd = demoPaperClass.createIndividual(Constants.BASE_URI.concat(paperTitle));
                else if (paperType.equals("Poster"))
                    paperInd = posterClass.createIndividual(Constants.BASE_URI.concat(paperTitle));
                else
                    paperInd = paperClass.createIndividual(Constants.BASE_URI.concat(paperTitle));

            // Journal
            String journal = URLEncoder.encode(record.get("journal"));
            Individual journalInd = journalClass.createIndividual(Constants.BASE_URI.concat(journal));

            // Volume
            String volume = URLEncoder.encode(record.get("volume"));
            // If volume field is empty assign 1
            if(volume.equals(""))
                volume = "1";
            // Volume is journal title _ volume
            Individual volumeInd = volumeClass.createIndividual(Constants.BASE_URI.concat(journal + "_" + volume));

            // HasVolume
            journalInd.addProperty(hasVolume, volumeInd);

            // SubmittedTo
            paperInd.addProperty(submittedTo, journalInd);

            // Editors
            String[] editors = record.get("editors").split("\\|");
            for (String editor : editors) {
                String e = URLEncoder.encode(editor);
                // Editor
                Individual editorInd = editorClass.createIndividual(Constants.BASE_URI.concat(e));
                // HandlesJournal
                editorInd.addProperty(handlesJournal, journalInd);
            }

            // Reviewers
            String[] reviewers = record.get("reviewers").split("\\|");
            Individual reviewer1 = reviewerClass.createIndividual(Constants.BASE_URI.concat(URLEncoder.encode(reviewers[0])));
            Individual reviewer2 = reviewerClass.createIndividual(Constants.BASE_URI.concat(URLEncoder.encode(reviewers[1])));
            //Reviews
            String text1 = record.get("review_reviewer_0");
            Individual reviewInd1 = reviewClass.createIndividual(Constants.BASE_URI.concat(URLEncoder.encode(text1)));
            String text2 = record.get("review_reviewer_1");
            Individual reviewInd2 = reviewClass.createIndividual(Constants.BASE_URI.concat(URLEncoder.encode(text2)));
            //WroteReview
            reviewer1.addProperty(wroteReview, reviewInd1);
            reviewer2.addProperty(wroteReview, reviewInd2);
            //ReviewFor
            reviewInd1.addProperty(reviewFor, paperInd);
            reviewInd2.addProperty(reviewFor, paperInd);
            //Decision
            String decision1 = record.get("decison_reviewer_0");
            reviewInd1.addProperty(decision, decision1);
            String decision2 = record.get("decison_reviewer_1");
            reviewInd2.addProperty(decision, decision2);
            //Review text
            reviewInd1.addProperty(text, text1);
            reviewInd2.addProperty(text, text2);

            // PublishedIn
            if (decision1.equals("accept") || decision2.equals("accept"))
                paperInd.addProperty(publishedIn, volumeInd);

            // Authors
            String[] authors = record.get("author").split("\\|");
            for (String author : authors) {
                String a = URLEncoder.encode(author);
                // Author
                Individual authorInd = authorClass.createIndividual(Constants.BASE_URI.concat(a));
                // WrotePaper
                authorInd.addProperty(wrotePaper, paperInd);
            }

            // Limit number of articles loaded
            if (++cnt >= Constants.MAX_ARTICLES) break;
        }

        FileOutputStream writerStream = new FileOutputStream( Constants.ARTICLES_OUTPUT );
        model.write(writerStream, "N-TRIPLE");
        writerStream.close();
        System.out.println("I am here");

    }
}
