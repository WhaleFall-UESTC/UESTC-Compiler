package com.compiler.token;

import com.compiler.utils.MiniLogger;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

public class TokenStream {
    private static volatile TokenStream instance;
    private final ConcurrentLinkedDeque<Token> tokensQueue;
    private final int maxCapacity;

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
        if (!checkInstance()) {
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
        if (!checkInstance()) {
            return null;
        }
        return instance.tokensQueue.poll();
    }

    private TokenStream() {
        tokensQueue = new ConcurrentLinkedDeque<>();
        maxCapacity = 512;
    }

    private static boolean checkInstance() {
        if (instance == null) {
            MiniLogger.error("TokenStream did not initialize properly");
            return false;
        }
        return true;
    }
}
