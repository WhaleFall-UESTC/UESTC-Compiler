package com.compiler.lexer;

public class Token {
    private String text;
    private String type;
    private int typeId;
    
    @Override
    public String toString() {
        return "type: " + type + ", text: " + text;
    }
}
