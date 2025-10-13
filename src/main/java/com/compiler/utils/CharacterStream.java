package com.compiler.utils;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CharacterStream {
    private static BufferedReader reader;
    private static int next = 0;
    private static boolean hasNext = false;
    private static boolean init = false;

    /**
     * initialize character stream
     * @param filePath the path of the file to read
     */
    public static void initialize(String filePath) {
        try {
            if (reader != null) {
                reader.close();
            }
            reader = new BufferedReader(new FileReader(filePath));
            readNextCharacter();
            init = true;
            MiniLogger.info("CharacterStream initialized");
        } catch (IOException e) {
            MiniLogger.logErrorAndExit("Failed to initialize character stream for {}. Error: {}", filePath, e);
        }
    }

    /**
     * consume a character from stream
     * @return next character of stream, null if it does not have
     */
    public static @Nullable Character consume() {
        if (!checkNextCharacter()) {
            return null;
        }
        Character result = (char) next;
        readNextCharacter();
        return result;
    }

    /**
     * get the next character of stream
     * @return next character of stream, null if it does not have
     */
    public static @Nullable Character peek() {
        if (!checkNextCharacter()) {
            return null;
        }
        return (char) next;
    }

    /**
     * get the i-th character from current position (0-based)
     * @param i distance from current position
     * @return the i-th character, null if it doesn't exist
     */
    public static @Nullable Character peek(int i) {
        if (!checkNextCharacter()) {
            return null;
        }
        if (i < 0) {
            return null;
        }
        if (i == 0) {
            // next produce can not handle i = 0
            return peek();
        }
        try {
            // Mark current position, allowing enough lookahead
            reader.mark(i + 1);
            // position 0 has already read
            for (int j = 0; j < i - 1; j++) {
                reader.read();
            }
            // Read the target character
            int targetChar = reader.read();
            // Reset to original position
            reader.reset();
            return (targetChar != -1) ? (char) targetChar : null;
        } catch (IOException e) {
            MiniLogger.warn("Error peeking character at position {}: {}", i, e);
            return null;
        }
    }

    /**
     * close the stream
     */
    public static void close() {
        try {
            if (reader != null) {
                reader.close();
                init = false;
                MiniLogger.info("CharacterStream closed");
            }
        } catch (IOException e) {
            MiniLogger.warn("Failed to close character stream: {}", e);
        }
    }

    public static boolean isHasNext() {
        return hasNext;
    }

    private static void readNextCharacter() {
        try {
            next = reader.read();
            hasNext = (next != -1);
        } catch (IOException e) {
            MiniLogger.logErrorAndExit("Error reading character: {}", e);
        }
    }

    /**
     * check if stream could output a character
     * @return true if it can, false instead;
     */
    private static boolean checkNextCharacter() {
        if (!init) {
            MiniLogger.error("Character stream has not been initialized");
            return false;
        }
        if (!hasNext) {
            MiniLogger.warn("Stream has no next character");
            return false;
        }
        return true;
    }
}
