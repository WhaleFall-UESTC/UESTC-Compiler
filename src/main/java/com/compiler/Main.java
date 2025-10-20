package com.compiler;

import com.compiler.lexer.Lexer;
import com.compiler.parser.Parser;
import com.compiler.token.TokenStream;
import com.compiler.utils.CharacterStream;
import com.compiler.utils.PathConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        String filePath = parseArgs(args);
        initialize(filePath);
        Lexer.start();
        Parser.parse();
        fin();
    }

    private static void initialize(String codePath) {
        CharacterStream.initialize(codePath);
        TokenStream.initialize();
        String filename = parseFilename(codePath);
        TokenStream.setOutputFile(filename + PathConfig.OUTPUT_DYD);
        PathConfig.setFilename(filename);
        Lexer.initialize(codePath);
    }

    private static void fin() {
        CharacterStream.close();
    }

    private static void printUsageAndExit() {
        System.err.println("Usage: java Program <codePath>");
        System.exit(-1);
    }

    private static String parseArgs(String[] args) {
        if (args.length < 1) {
            printUsageAndExit();
        }
        Path path = Paths.get(args[0]);
        if (!Files.exists(path) || !Files.isRegularFile(path) || !Files.isReadable(path)) {
            printUsageAndExit();
        }
        return args[0];
    }

    private static String parseFilename(String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        } else {
            return fileName;
        }
    }
}