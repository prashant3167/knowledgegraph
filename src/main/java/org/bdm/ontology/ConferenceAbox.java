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

public class ConferenceAbox {
    public static void loadArticles() throws IOException {
        Model model = ModelFactory.createDefaultModel().read(Constants.TBOX_MODEL_PATH);
        OntModel ontModel = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM_RDFS_INF, model);
        System.out.println(ontModel.listClasses());
        //Classes
        OntClass paperClass = ontModel.getOntClass( Constants.BASE_URI.concat("Paper") );
        OntClass conferenceClass = ontModel.getOntClass( Constants.BASE_URI.concat("Conference") );

        // Paper Subclass
        OntClass fullPaperClass = ontModel.getOntClass( Constants.BASE_URI.concat("FullPaper") );
        OntClass shortPaperClass = ontModel.getOntClass( Constants.BASE_URI.concat("ShortPaper") );
        OntClass demoPaperClass = ontModel.getOntClass( Constants.BASE_URI.concat("DemoPaper") );
        OntClass posterClass = ontModel.getOntClass( Constants.BASE_URI.concat("Poster") );

        // Conference Subclasses
        OntClass RegularConferenceClass = ontModel.getOntClass( Constants.BASE_URI.concat("RegularConference") );
        OntClass workshopClass = ontModel.getOntClass( Constants.BASE_URI.concat("Workshop") );
        OntClass symposiumClass = ontModel.getOntClass( Constants.BASE_URI.concat("Symposium") );
        OntClass expertGroupClass = ontModel.getOntClass( Constants.BASE_URI.concat("ExpertGroup") );

        //Object Properties
        OntProperty wrotePaper = ontModel.getOntProperty( Constants.BASE_URI.concat("wrotepaper") );
        OntProperty HasProceedings = ontModel.getOntProperty( Constants.BASE_URI.concat("hasproceedings") );
        OntProperty publishedIn = ontModel.getOntProperty( Constants.BASE_URI.concat("publishedin") );
        OntProperty inSubmission = ontModel.getOntProperty( Constants.BASE_URI.concat("insubmission") );
        OntProperty toVenue = ontModel.getOntProperty( Constants.BASE_URI.concat("tovenue") );
        OntProperty supervises = ontModel.getOntProperty( Constants.BASE_URI.concat("supervises") );
        OntProperty handlesConference = ontModel.getOntProperty( Constants.BASE_URI.concat("handlesconference") );
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
        DatatypeProperty fullName = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("fullName"));
        DatatypeProperty topic = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("topic"));
        DatatypeProperty publishDate = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("publish_date"));
        DatatypeProperty url = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("url"));
        DatatypeProperty key = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("key"));
        DatatypeProperty publicationYear = ontModel.getDatatypeProperty(Constants.BASE_URI.concat("publication_year"));

        //Read journal.csv
        BufferedReader csvReader = new BufferedReader(new FileReader(Constants.CONFERENCE_PATH));
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

            // ConferenceAbox
            String conference = URLEncoder.encode(record.get("booktitle"));
            String conferenceType = record.get("conference_type");
            Individual conferenceInd = null;
            if (conferenceType.equals("Regular"))
                conferenceInd = RegularConferenceClass.createIndividual(Constants.BASE_URI.concat(conference));
            else if (conferenceType.equals("Workshop"))
                conferenceInd = workshopClass.createIndividual(Constants.BASE_URI.concat(conference));
            else if (conferenceType.equals("Symposium"))
                conferenceInd = symposiumClass.createIndividual(Constants.BASE_URI.concat(conference));
            else if (conferenceType.equals("Expert Group"))
                conferenceInd = expertGroupClass.createIndividual(Constants.BASE_URI.concat(conference));
            else
                conferenceInd = conferenceClass.createIndividual(Constants.BASE_URI.concat(conference));
           conferenceInd.addProperty(venueTitle, record.get("booktitle"));

            // Procedings
            // It will be only added if accepted by reviewr
            String Proceedings = URLEncoder.encode(record.get("crossref").replace("journals","conf"));

            // If proceedings field is empty assign 1
            // proceeding is conference is  crossref
            //Individual proceedingsInd = proceedingsClass.createIndividual(Constants.BASE_URI.concat(Proceedings));
            Resource proceedingsInd = ontModel.createResource(Constants.BASE_URI.concat(Proceedings));
            proceedingsInd.addProperty(publicationYear, record.get("year").replace("journals","conf"));

            // HasProceedings
            conferenceInd.addProperty(HasProceedings, proceedingsInd);

            // Submission
            Resource submissionInd = ontModel.createResource(Constants.BASE_URI.concat("submission_"+paper+"_"+conference));
            paperInd.addProperty(inSubmission, submissionInd);
            submissionInd.addProperty(toVenue, conferenceInd);

            // Supervisor
            String supervisor = URLEncoder.encode(record.get("supervisor"));
            Resource supervisorInd = ontModel.createResource(Constants.BASE_URI.concat(supervisor));
            supervisorInd.addProperty(supervises, submissionInd);

            // Chairs
            String[] chairs = record.get("chair").split("\\|");
            for (String chair : chairs) {
                String e = URLEncoder.encode(chair);
                // Editor
                Resource chairInd = ontModel.createResource(Constants.BASE_URI.concat(e));
                // HandlesJournal
                chairInd.addProperty(handlesConference, conferenceInd);
                chairInd.addProperty(fullName, chair);
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
                paperInd.addProperty(publishedIn, proceedingsInd);

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
            // Conference
            String[] journal_keywords = record.get("conference_keywords").split("\\|");
            for (String keyword : journal_keywords) {
                String k = URLEncoder.encode(keyword);
                // ResearchArea
                Resource researchAreaInd = ontModel.createResource(Constants.BASE_URI.concat(k));
                // WrotePaper
                conferenceInd.addProperty(venueRelatedTo, researchAreaInd);
                researchAreaInd.addProperty(topic, keyword);
            }

            // Proceedings
            String[] proceedingsKeywords = record.get("proceedings_keywords").split("\\|");
            for (String keyword : proceedingsKeywords) {
                String k = URLEncoder.encode(keyword);
                // ResearchArea
                Resource researchAreaInd = ontModel.createResource(Constants.BASE_URI.concat(k));
                // WrotePaper
                proceedingsInd.addProperty(publicationRelatedTo, researchAreaInd);
                researchAreaInd.addProperty(topic, keyword);
            }

            // Additional properties
            paperInd.addProperty(key, record.get("key"));
            paperInd.addProperty(url, record.get("ee"));

            // Limit number of articles loaded
            if (++cnt >= Constants.MAX_ARTICLES) break;
        }

        FileOutputStream writerStream = new FileOutputStream( Constants.CONFERENCE_OUTPUT );
        model.write(writerStream, "N-TRIPLE");
        writerStream.close();
    }
}
