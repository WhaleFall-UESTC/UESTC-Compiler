package com.compiler.utils;

public class PathConfig {
    public static final String OUTPUT_DYD = ".dyd";
    public static final String OUTPUT_ERR = ".err";
    public static final String OUTPUT_VAR = ".var";
    public static final String OUTPUT_PRO = ".pro";

    public static final String TOKEN_MAP_JSON = "tokenMap.json";
    public static final String GRAMMAR_JSON = "grammar.json";

    public static String filename;

    public static void setFilename(String filename) {
        PathConfig.filename = filename;
    }

    public static String getFilename() {
        return filename;
    }

    public static String getOutputPro() {
        return filename + OUTPUT_PRO;
    }
    public static String getOutputVar() {
        return filename + OUTPUT_VAR;
    }
    public static String getOutputErr() {
        return filename + OUTPUT_ERR;
    }
}
