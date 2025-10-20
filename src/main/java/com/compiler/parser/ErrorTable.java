package com.compiler.parser;

import com.compiler.utils.MiniWriter;
import com.compiler.utils.PathConfig;

import java.util.ArrayList;

public class ErrorTable {

    private ArrayList<ErrorEntry> errors = new ArrayList<ErrorEntry>();

    public static class ErrorEntry {
        int line;
        String errorMessage;

        ErrorEntry(int line, String errorMessage) {
            this.line = line;
            this.errorMessage = errorMessage;
        }
        String print() {
            return "***LINE:" + line + " " + errorMessage;
        }
    }

    public int add(int line, String errorMessage) {
        int pos = errors.size();
        errors.add(new ErrorEntry(line, errorMessage));
        return pos;
    }

    public void print() {
        MiniWriter writer = new MiniWriter(PathConfig.getOutputErr());
        for (ErrorEntry entry : errors) {
            writer.write(entry.print());
        }
    }

}
