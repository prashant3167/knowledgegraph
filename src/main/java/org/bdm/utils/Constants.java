package org.bdm.utils;

import java.util.ArrayList;

public class Constants {
    public static final String BASE_URI = "http://www.bdma.sdm#";
    public static final String TBOX_MODEL_PATH = "data/TBOX.ttl";
    public static final String ABOX_MODEL_PATH = "data/ABOX.nt";
    public static final String ARTICLES_PATH = "data/output_article.csv";
//    ArrayList<String> gfg = new ArrayList<String>();
//    gfg.add("test");
    public static final ArrayList<String> PAPER_TYPE = new ArrayList<String>() {
        {
            //Can be full paper, short paper,demo paper and poster(only for conference)
            add("Full");
            add("Short");
            add("Demo");
            add("Poster");
        }
    };




    public static final int MAX_ARTICLES = 100;
}
