package com.compiler.parser;

import com.compiler.token.Token;
import com.compiler.token.TokenStream;
import com.compiler.utils.MiniLogger;

import java.util.*;

public class Parser {
    private static final String initProcredure = "global";

    private static ErrorTable errorTable = new ErrorTable();
    private static VariableTable variableTable = new VariableTable();
    private static ProcedureTable procedureTable = new ProcedureTable();

    private static boolean declaringFunc = true; // declaring global at first
    private static boolean declaringVar = false;
    private static boolean usingVar = false;
    private static boolean usingFunc = false;
    private static boolean usingConst = false;
    private static boolean parsingFunc = false;
    private static boolean parsingParam = false;
    private static boolean parsingVar = false;

    private static Deque<Integer> procedureStack = new ArrayDeque<Integer>();

    // for debug
    private static int markLevel = 0;

    private static final String[] tokenTypeBlackList = new String[]{
            "EOF", "EOLN"
    };

    static {
        int globalIdx = procedureTable.add(initProcredure, "integer", 0);
        procedureTable.procedureAddAdr(globalIdx, -1, -1);
        procedureStack.push(globalIdx);
    }

    static class Report {
        boolean noErrors;
        String message;
        Integer line;
        Report(boolean noErrors, String message, Integer line) {
            this.noErrors = noErrors;
            this.message = message;
            this.line = line;
        }
        static Report success() {
            return new Report(true, "", null);
        }
        static Report error(String message, Integer line) {
            return new Report(false, message, line);
        }
        boolean hasError() {
            return !noErrors;
        }
        String getMessage() {
            return message;
        }
        Integer getLine() {
            return line;
        }
    }

    private static int getCurrentLevel() {
        return procedureStack.size();
    }

    private static String getCurrentProcedureName() {
        return procedureTable.getProcedureName(getCurrentProcedureIndex());
    }

    private static int getCurrentProcedureIndex() {
        Integer currentProcedureIdx = procedureStack.peek();
        if (currentProcedureIdx == null || !procedureTable.isLegalIndex(currentProcedureIdx)) {
            String message = "Current Procedure Index " + currentProcedureIdx + " is NULL or out of bounds.";
            MiniLogger.error(message);
            throw new RuntimeException(message);
        }
        return currentProcedureIdx;
    }

    public static void parse() {
        parseHelper(Grammar.startSymbol);
        variableTable.print();
        procedureTable.print();
        errorTable.print();
    }

    private static boolean parseHelper(String symbol) {
        MiniLogger.info("Parsing " + symbol);

        // handle terminal
        if (Grammar.isTerminal(symbol)) {
            Token currentToken = peekTokenWithFilter();
            MiniLogger.info("Current Token: " + currentToken);
            // consume Token even if it does NOT match
            TokenStream.consume();
            if (currentToken != null && currentToken.match(symbol)) {
                Report report = matchedTokenHandler(currentToken);
                if (report.hasError()) {
                    errorTable.add(report.getLine(), report.getMessage());
                }
                // in parseHelper, once it matched, return true
                // even if there is something wrong
                return true;
            } else {
                // failed to match
                return false;
            }
        }

        // handle non-terminal
        List<List<String>> productions = Grammar.getProductions(symbol);
        if (productions == null) {
            MiniLogger.error("Invalid symbol: " + symbol);
            return false;
        }
        // sym is terminal
        for (List<String> production : productions) {
            // save current TokenStream status
            TokenStream.mark();
            // Assert this production will be success to match
            boolean success = true;
            for (String sym : production) {
                // handle ε
                if ("ε".equals(sym)) {
                    continue;
                }
                changeStatus(sym);
                // if symbol failed to match
                if (!parseHelper(sym)) {
                    success = false;
                    // failed to match, should reset status
                    resetStatus();
                    // skip to and try the next production
                    break;
                }
            }

            // every symbol in this production match
            if (success) {
                TokenStream.save();
                return true;
            }
            // this production failed to match, try another one
            TokenStream.reset();
        }

        return false; // All production failed to match
    }

    /**
     * Perform specific processing on specific Tokens
     * @param currentToken token to handle
     * @return token parse report
     */
    private static Report matchedTokenHandler(Token currentToken) {
        // handle identifier
        if (currentToken.isTypeIdentifier()) {
            if (declaringFunc && parsingFunc) {
                parsingFunc = false;
                return addNewProcedure(currentToken);
            }
            if (declaringFunc && parsingParam) {
                parsingParam = false;
                return addNewVariable(currentToken, VariableTable.VariableType.PARAMETER);
            }

            if (declaringVar && parsingVar) {
                declaringVar = false;
                parsingVar = false;
                return addNewVariable(currentToken, VariableTable.VariableType.VARIABLE);
            }
        }

        // handle begin
        if (currentToken.isTypeBegin()) {
            if (!declaringFunc) {
                MiniLogger.error("should be declaring function");
            }
            declaringFunc = false;
            // already push new procedure into stack
        }

        // handle end
        if (currentToken.isTypeEnd()) {
            procedureStack.pop();
        }

//        no need to handle, match successfully
        return Report.success();
    }

    /**
     * only used in parseHelper, when parsing a new function
     * add a new Proc into procedureTable
     * @param currentToken parsing token
     * @return Report
     */
    private static Report addNewProcedure(Token currentToken) {
        int currentLevel = getCurrentLevel();
        String newProcName = currentToken.getText();
        if (procedureTable.contains(newProcName, currentLevel)) {
            String errorMsg = "Procedure " + newProcName + " redefinition";
            MiniLogger.error(errorMsg);
            return Report.error(errorMsg, currentToken.getLine());
        }
        int newProcIdx = procedureTable.add(newProcName, "integer", currentLevel);
        procedureStack.push(newProcIdx);
        return Report.success();
    }

    private static Report addNewVariable(Token currentToken, VariableTable.VariableType vtype) {
        int currentLevel = getCurrentLevel();
        String newVarName = currentToken.getText();
        if (variableTable.contains(newVarName, currentLevel)) {
            String errorMsg = "Variable " + newVarName + " redefinition";
            MiniLogger.error(errorMsg);
            return Report.error(errorMsg, currentToken.getLine());
        }
        addVariableTable(newVarName, vtype);
        return Report.success();
    }

    private static void addVariableTable(String vname, VariableTable.VariableType vtype) {
        int varIdx = variableTable.add(
                vname,
                getCurrentProcedureName(),
                vtype,
                "integer",
                getCurrentLevel()
        );
        int currentProcIdx = getCurrentProcedureIndex();
        procedureTable.procedureAddAdr(currentProcIdx, varIdx, varIdx);
    }

    private static void changeStatus(String sym) {
        if (declaringFunc) {
            if ("function".equals(sym)) {
                parsingFunc = true;
            }
            if ("Parameter".equals(sym)) {
                parsingParam = true;
            }
        }
        if ("FunctionDecl".equals(sym)) {
            declaringFunc = true;
        }

        if (declaringVar) {
            if ("integer".equals(sym)) {
                parsingVar = true;
            }
        }
        if ("VariableDecl".equals(sym)) {
            declaringVar = true;
        }
    }

    private static void resetStatus() {
        if (declaringFunc) {
            declaringFunc = false;
        }
        if (declaringVar) {
            declaringVar = false;
        }
        if (parsingFunc) {
            parsingFunc = false;
        }
        if (parsingParam) {
            parsingParam = false;
        }
        if (parsingVar) {
            parsingVar = false;
        }
    }

    /**
     * Get a valid token from TokenStream
     * filter token type in tokenTypeBlacklist
     * @return valid token
     */
    private static Token peekTokenWithFilter() {
        while (isInvalidToken(TokenStream.peek())) {
            TokenStream.consume();
        }
        return TokenStream.peek();
    }

    private static boolean isInvalidToken(Token token) {
        for (String type : tokenTypeBlackList) {
            if (token.match(type)) {
                return true;
            }
        }
        if (token.match("EOF")) {
            // out of token, but the Parser is still reqirues
            MiniLogger.error("Out of Tokens");
            throw new RuntimeException("Out of Tokens");
        }
        return false;
    }
}
