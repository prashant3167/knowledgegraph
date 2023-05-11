package org.bdm;

import org.bdm.ontology.JournalAbox;
import org.bdm.ontology.ConferenceAbox;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        JournalAbox.loadArticles();
        ConferenceAbox.loadArticles();
    }
}