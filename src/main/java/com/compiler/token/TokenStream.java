package com.compiler.token;

import com.compiler.utils.MiniLogger;
import com.compiler.utils.MiniWriter;
import com.compiler.utils.PathConfig;

import java.util.concurrent.ConcurrentLinkedDeque;

public class TokenStream {
    private static volatile TokenStream instance;
    private final ConcurrentLinkedDeque<Token> tokensQueue;
    private final int maxCapacity;

    private static MiniWriter writer;

    /**
     * initialize TokenStream
     * @return is initialization successful or not
     */
    public static boolean initialize() {
        if (instance == null) {
            synchronized (TokenStream.class) {
                if (instance == null) {
                    instance = new TokenStream();
                    return true;
                }
            }
        }
        // Already initialized
        return false;
    }

    /**
     * push a new token into stream
     * @param token the token to be added
     * @return if token was successfully added
     */
    public static boolean produce(Token token) {
        if (!hasInstance()) {
            return false;
        }
        if (token == null) {
            MiniLogger.error("Token is null");
            return false;
        }
        if (instance.tokensQueue.size() >= instance.maxCapacity) {
            MiniLogger.error("TokenStream Max Capacity Exceeded");
            return false;
        }
        return instance.tokensQueue.offer(token);
    }

    /**
     * consume a token from TokenStream
     * @return a token, null if TokenStream not initialized
     */
    public static Token consume() {
        if (!hasInstance()) {
            return null;
        }
        return instance.tokensQueue.poll();
    }

    /**
     * produce, but write to *.dyd at the same time
     * must call setOutputFile() first
     * @param token the token to be added
     * @return if token was successfully added and written
     */
    public static boolean produceWithOutput(Token token) {
        boolean result = produce(token);
        if (result) {
            if (writer == null) {
                MiniLogger.error("Writer is null");
                return false;
            }
            writer.write(token.print());
        }
        return result;
    }

    public static void setOutputFile(String outputFile) {
        writer = new MiniWriter(outputFile);
    }

    private TokenStream() {
        tokensQueue = new ConcurrentLinkedDeque<>();
        maxCapacity = 512;
    }

    private static boolean hasInstance() {
        if (instance == null) {
            MiniLogger.error("TokenStream did not initialize properly");
            return false;
        }
        return true;
    }
}
