package org.bdm.ontology;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.bdm.utils.Constants;
import org.bdm.utils.Formatter;

import java.io.*;

public class ABOX {
    /**
     * Create schema of
     */
    public static void loadInstances() throws IOException {
        Model model = ModelFactory.createDefaultModel().read(Constants.TBOX_MODEL_PATH);
        OntModel ontModel = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM_RDFS_INF, model);

        //Classes
        OntClass paperClass = ontModel.getOntClass( Constants.BASE_URI.concat("Paper") );
        OntClass authorClass = ontModel.getOntClass( Constants.BASE_URI.concat("Author") );
        OntClass volumeClass = ontModel.getOntClass( Constants.BASE_URI.concat("Volume") );
        OntClass journalClass = ontModel.getOntClass( Constants.BASE_URI.concat("Journal") );

        //Properties
        OntProperty wrotePaper = ontModel.getOntProperty( Constants.BASE_URI.concat("wrotepaper") );
        OntProperty hasVolume = ontModel.getOntProperty( Constants.BASE_URI.concat("hasvolume") );
        OntProperty publishedIn = ontModel.getOntProperty( Constants.BASE_URI.concat("publishedin") );

        //Read output_article.csv
        BufferedReader csvReader = new BufferedReader(new FileReader(Constants.ARTICLES_PATH));
        CSVParser parser = CSVFormat.DEFAULT.withDelimiter(';').withHeader().parse(csvReader);

        int cnt = 0;
        for(CSVRecord record : parser) {

            // Article (paper)
            String paperTitle = Formatter.format(record.get("title"));
            Individual paperInd = paperClass.createIndividual(Constants.BASE_URI.concat(paperTitle));

            // Journal
            String journal = Formatter.format(record.get("journal"));
            Individual journalInd = journalClass.createIndividual(Constants.BASE_URI.concat(journal));

            // Volume
            String volume = Formatter.format(record.get("volume"));
            // If volume field is empty assign 1
            if(volume.equals(""))
                volume = "1";
            // Volume is journal title _ volume
            Individual volumeInd = volumeClass.createIndividual(Constants.BASE_URI.concat(journal + "_" + volume));

            // HasVolume
            journalInd.addProperty(hasVolume, volumeInd);

            // PublishedIn
            paperInd.addProperty(publishedIn, volumeInd);

            // Authors
            String[] authors = record.get("author").split("\\|");
            for (String author : authors) {
                String a = Formatter.format(author);
                // Author
                Individual authorInd = authorClass.createIndividual(Constants.BASE_URI.concat(a));
                // WrotePaper
                authorInd.addProperty(wrotePaper, paperInd);
            }

            // Limit number of articles loaded
            if (++cnt >= Constants.MAX_ARTICLES) break;
        }

        FileOutputStream writerStream = new FileOutputStream( Constants.ABOX_MODEL_PATH );
        model.write(writerStream, "N-TRIPLE");
        writerStream.close();

    }
}
