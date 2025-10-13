package com.compiler.lexer;

import com.compiler.token.Token;
import com.compiler.token.TokenStream;
import com.compiler.utils.CharacterStream;
import com.compiler.utils.MiniLogger;

public class Lexer {
    private static String codePath;

    /**
     * initialize Lexer
     * @param path the path of the code to be compiled
     */
    public static void initialize(String path) {
        codePath = path;
    }

    /**
     * start Lexer
     * warning: must initialize first
     */
    public static void start() {
        start(codePath);
    }

    /**
     * start Lexer
     * @param path the path of the code to be compiled
     */
    public static void start(String path) {
        while (CharacterStream.isHasNext()) {
            TokenStream.produceWithOutput(getNextToken());
        }
    }

    /**
     * Generate a token from CharacterStream
     * @return new token from CharacterStream
     */
    private static Token getNextToken() {
        // skip blank character
        while (' ' == CharacterStream.getNext()) {
            CharacterStream.consume();
        }

        // match keyword or identifier
        if (Character.isLetter(CharacterStream.getNext())) {
            String lexeme = CharacterStream.consumeWhile(c -> Character.isLetterOrDigit(c));
            if (Token.isKeyWord(lexeme)) {
                return new Token(lexeme, lexeme);
            } else {
                return new Token(lexeme, "identifier");
            }
        }

        // match constant
        if (Character.isDigit(CharacterStream.getNext())) {
            String lexeme = CharacterStream.consumeWhile(c -> Character.isDigit(c));
            return new Token(lexeme, "constant");
        }

        // match math operator
        if (Token.isMathOp(CharacterStream.getNext())) {
            String lexeme = CharacterStream.consumeWhile(Token::isMathOp);
            if (Token.isKeyWord(lexeme)) {
                return new Token(lexeme, lexeme);
            } else {
                raiseInvalidToken();
            }
        }

        // match end of a sentence
        if (';' == CharacterStream.getNext()) {
            CharacterStream.consume();
            return new Token(";", ";");
        }

        // match EOLN/EOF
        if ('\n' == CharacterStream.getNext() || '\r' == CharacterStream.getNext()) {
            if ('\r' == CharacterStream.consume() && '\n' == CharacterStream.getNext() ) {
                CharacterStream.consume();
            }
            if (CharacterStream.isHasNext()) {
                return new Token("\n", "EOLN");
            } else {
                return new Token("\n", "EOF");
            }
        }
        return null;
    }

    private static void raiseInvalidToken() {
        MiniLogger.error("Invalid token");
        System.exit(-1);
    }

    private static void raiseInvalidToken(String message) {
        MiniLogger.error(message);
        System.exit(-1);
    }
}
