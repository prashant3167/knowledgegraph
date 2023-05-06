package org.bdm;

import org.bdm.ontology.ABOX;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ABOX.loadInstances();
    }
}