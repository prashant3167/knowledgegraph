package org.bdm.ontology;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.bdm.utils.Constants;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;

public class JournalAbox {
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
        OntClass journalClass = ontModel.getOntClass( Constants.BASE_URI.concat("Journal") );

        //Object Properties
        OntProperty wrotePaper = ontModel.getOntProperty( Constants.BASE_URI.concat("wrotepaper") );
        OntProperty hasVolume = ontModel.getOntProperty( Constants.BASE_URI.concat("hasvolume") );
        OntProperty publishedIn = ontModel.getOntProperty( Constants.BASE_URI.concat("publishedin") );
        OntProperty inSubmission = ontModel.getOntProperty( Constants.BASE_URI.concat("insubmission") );
        OntProperty toVenue = ontModel.getOntProperty( Constants.BASE_URI.concat("tovenue") );
        OntProperty supervises = ontModel.getOntProperty( Constants.BASE_URI.concat("supervises") );
        OntProperty handlesJournal = ontModel.getOntProperty( Constants.BASE_URI.concat("handlesjournal") );
        OntProperty venueRelatedTo = ontModel.getOntProperty( Constants.BASE_URI.concat("venuerelatedto") );
        OntProperty paperRelatedTo = ontModel.getOntProperty( Constants.BASE_URI.concat("paperrelatedto") );
        OntProperty publicationRelatedTo = ontModel.getOntProperty( Constants.BASE_URI.concat("publicationrelatedto") );
        OntProperty wroteReview = ontModel.getOntProperty( Constants.BASE_URI.concat("wrotereview") );
        OntProperty reviewFor = ontModel.getOntProperty( Constants.BASE_URI.concat("reviewfor") );

        //Datatype Properties
        DatatypeProperty text = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("text"));
        DatatypeProperty decision = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("decision"));
        DatatypeProperty paperTitle = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("paper_title"));
        DatatypeProperty venueTitle = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("venue_title"));
        DatatypeProperty volumeProperty = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("volume"));
        DatatypeProperty fullName = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("fullName"));
        DatatypeProperty topic = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("topic"));
        DatatypeProperty publishDate = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("publish_date"));
        DatatypeProperty url = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("url"));
        DatatypeProperty key = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("key"));

        //Read journal.csv
        BufferedReader csvReader = new BufferedReader(new FileReader(Constants.ARTICLES_PATH));
        CSVParser parser = CSVFormat.DEFAULT.withDelimiter(',').withHeader().parse(csvReader);

        int cnt = 0;
        for(CSVRecord record : parser) {

            // Article (paper)
            String paper = URLEncoder.encode(record.get("title"));
            String paperType = record.get("paper_type");
            Individual paperInd = null;
            if (paperType.equals("Full Paper"))
                paperInd = fullPaperClass.createIndividual(Constants.BASE_URI.concat(paper));
            else if (paperType.equals("Short Paper"))
                paperInd = shortPaperClass.createIndividual(Constants.BASE_URI.concat(paper));
            else if (paperType.equals("Demo Paper"))
                paperInd = demoPaperClass.createIndividual(Constants.BASE_URI.concat(paper));
            else if (paperType.equals("Poster"))
                paperInd = posterClass.createIndividual(Constants.BASE_URI.concat(paper));
            else
                paperInd = paperClass.createIndividual(Constants.BASE_URI.concat(paper));
            paperInd.addProperty(paperTitle, record.get("title"));
            paperInd.addProperty(publishDate, record.get("mdate"));

            // Journal
            String journal = URLEncoder.encode(record.get("journal"));
            Individual journalInd = journalClass.createIndividual(Constants.BASE_URI.concat(journal));
            journalInd.addProperty(venueTitle, record.get("journal"));

            // Volume
            String volume = URLEncoder.encode(record.get("volume"));
            Resource volumeInd = ontModel.createResource(Constants.BASE_URI.concat(volume));
            volumeInd.addProperty(volumeProperty, record.get("volume"));

            // HasVolume
            journalInd.addProperty(hasVolume, volumeInd);

            // Submission
            Resource submissionInd = ontModel.createResource(Constants.BASE_URI.concat("submission_"+paper+"_"+journal));
            paperInd.addProperty(inSubmission, submissionInd);
            submissionInd.addProperty(toVenue, journalInd);

            // Supervisor
            String supervisor = URLEncoder.encode(record.get("supervisor"));
            Resource supervisorInd = ontModel.createResource(Constants.BASE_URI.concat(supervisor));
            supervisorInd.addProperty(supervises, submissionInd);

            // Editors
            String[] editors = record.get("editors").split("\\|");
            for (String editor : editors) {
                String e = URLEncoder.encode(editor);
                // Editor
                Resource editorInd = ontModel.createResource(Constants.BASE_URI.concat(e));
                // HandlesJournal
                editorInd.addProperty(handlesJournal, journalInd);
                editorInd.addProperty(fullName, editor);
            }

            // Reviewers
            String[] reviewers = record.get("reviewers").split("\\|");
            Resource reviewer1 = ontModel.createResource(Constants.BASE_URI.concat(URLEncoder.encode(reviewers[0])));
            Resource reviewer2 = ontModel.createResource(Constants.BASE_URI.concat(URLEncoder.encode(reviewers[1])));
            reviewer1.addProperty(fullName, reviewers[0]);
            reviewer2.addProperty(fullName, reviewers[1]);
            //Reviews
            String text1 = record.get("review_reviewer_0");
            Resource reviewInd1 = ontModel.createResource(Constants.BASE_URI.concat("review_"+URLEncoder.encode(reviewers[0])+"_"+paper));
            String text2 = record.get("review_reviewer_1");
            Resource reviewInd2 = ontModel.createResource(Constants.BASE_URI.concat("review_"+URLEncoder.encode(reviewers[1])+"_"+paper));
            //WroteReview
            reviewer1.addProperty(wroteReview, reviewInd1);
            reviewer2.addProperty(wroteReview, reviewInd2);
            //ReviewFor
            reviewInd1.addProperty(reviewFor, submissionInd);
            reviewInd2.addProperty(reviewFor, submissionInd);
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
                Resource authorInd = ontModel.createResource(Constants.BASE_URI.concat(a));
                // WrotePaper
                authorInd.addProperty(wrotePaper, paperInd);
                authorInd.addProperty(fullName, author);
            }

            // Research areas
            // Paper
            String[] keywords = record.get("paper_keywords").split("\\|");
            for (String keyword : keywords) {
                String k = URLEncoder.encode(keyword);
                // ResearchArea
                Resource researchAreaInd = ontModel.createResource(Constants.BASE_URI.concat(k));
                // WrotePaper
                paperInd.addProperty(paperRelatedTo, researchAreaInd);
                researchAreaInd.addProperty(topic, keyword);
            }
            // Journal
            String[] journal_keywords = record.get("journal_keywords").split("\\|");
            for (String keyword : journal_keywords) {
                String k = URLEncoder.encode(keyword);
                // ResearchArea
                Resource researchAreaInd = ontModel.createResource(Constants.BASE_URI.concat(k));
                // WrotePaper
                journalInd.addProperty(venueRelatedTo, researchAreaInd);
                researchAreaInd.addProperty(topic, keyword);
            }

            // Volume
            String[] volume_keywords = record.get("volume_keywords").split("\\|");
            for (String keyword : volume_keywords) {
                String k = URLEncoder.encode(keyword);
                // ResearchArea
                Resource researchAreaInd = ontModel.createResource(Constants.BASE_URI.concat(k));
                // WrotePaper
                volumeInd.addProperty(publicationRelatedTo, researchAreaInd);
                researchAreaInd.addProperty(topic, keyword);
            }

            // Additional properties
            paperInd.addProperty(key, record.get("key"));
            paperInd.addProperty(url, record.get("ee"));

            // Limit number of articles loaded
            if (++cnt >= Constants.MAX_ARTICLES) break;
        }

        FileOutputStream writerStream = new FileOutputStream( Constants.ARTICLES_OUTPUT );
        model.write(writerStream, "N-TRIPLE");
        writerStream.close();
        System.out.println("I am here");

    }
}
