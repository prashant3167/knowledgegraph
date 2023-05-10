package org.bdm;

import org.bdm.ontology.ABOX;
import org.bdm.ontology.ConferenceAbox;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ABOX.loadArticles();
        ConferenceAbox.loadArticles();
    }
}