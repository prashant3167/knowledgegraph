package org.bdm.utils;

public class Formatter {

    public static String format(String str) {
        return str.replace(" ", "")
                .replace(".", "")
                .replaceAll("[^\\x00-\\x7F]", "")
                .replaceAll("\"", "");
    }
}
