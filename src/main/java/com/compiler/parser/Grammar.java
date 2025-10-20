package com.compiler.parser;

import com.compiler.utils.JsonReader;
import com.compiler.utils.MiniLogger;
import com.compiler.utils.PathConfig;

import java.util.List;
import java.util.Map;

public class Grammar {
    public static final List<String> terminals;
    public static final List<String> nonterminals;
    public static final Map<String, List<List<String>>> grammar;
    public static final String startSymbol;

    static {
        terminals = JsonReader.readStringList(PathConfig.GRAMMAR_JSON, "terminals");
        nonterminals = JsonReader.readStringList(PathConfig.GRAMMAR_JSON, "nonTerminals");
        grammar = JsonReader.readMapListListString(PathConfig.GRAMMAR_JSON, "grammar");
        startSymbol = JsonReader.readStringValue(PathConfig.GRAMMAR_JSON, "startSymbol");
    }

    public static boolean isTerminal(String terminal) {
        return terminals.contains(terminal);
    }

    public static boolean isNonTerminal(String nonTerminal) {
        return nonterminals.contains(nonTerminal);
    }

    /**
     * return the productions of a symbol
     * @param symbol symbol
     * @return productions of symbol. If it is not a non-terminal, return null
     */
    public static List<List<String>> getProductions(String symbol) {
        if (!isNonTerminal(symbol)) {
            return null;
        }
        return grammar.get(symbol);
    }
}
