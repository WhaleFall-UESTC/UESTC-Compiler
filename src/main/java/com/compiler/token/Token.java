package com.compiler.token;

import com.compiler.utils.JsonReader;
import com.compiler.utils.PathConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Token {
    private String text;
    private String type;
    private int typeId;

    private static Map<String, Integer> tokenMap;
    private static List<Character> mathOps;

    public Token(String text, String type, int typeId) {
        this.text = text;
        this.type = type;
        this.typeId = typeId;
    }

    public Token(String text, String type) {
        this(text, type, Token.getTypeId(type));
    }

    /**
     * return typeId by its type
     * @param type String, the token's type
     * @return index of the type, defined in tokenMap.json
     */
    public static Integer getTypeId(String type) {
        return tokenMap.get(type);
    }

    /**
     * judge if a String is the keyword in MINI
     * the type of key word is just the key word itself
     * @param lexeme a newly generated token's content
     * @return if lexeme a key word in MINI
     */
    public static boolean isKeyWord(String lexeme) {
        return tokenMap.containsKey(lexeme);
    }

    public static boolean isTokenType(String type) {
        return tokenMap.containsKey(type);
    }

    public static boolean isMathOp(Character c) {
        return mathOps.contains(c);
    }

    /**
     * if two token is the same type
     * @param token token to be compared
     * @return if token and this are the same type
     */
    public boolean match(Token token) {
        return this.typeId == token.typeId;
    }

    /**
     * token format printed in *.dyd
     * @return dyd format
     */
    public String print() {
        return type + " " + typeId;
    }

    @Override
    public String toString() {
        return text + " " + type + " " + typeId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != Token.class) return false;
        return match((Token) obj);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (isTokenType(type)) {
            this.type = type;
            this.typeId = Token.getTypeId(type);
        }
    }

    public int getTypeId() {
        return typeId;
    }

    /*
      initialization when the class is loaded
      read json file
     */
    static {
        tokenMap = JsonReader.readMapInteger(PathConfig.TOKEN_MAP_JSON, "tokenMap");
        mathOps = JsonReader.readCharList(PathConfig.TOKEN_MAP_JSON, "mathOps");
    }
}
