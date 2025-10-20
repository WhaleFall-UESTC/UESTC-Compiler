package com.compiler.token;

import com.compiler.utils.MiniLogger;
import com.compiler.utils.MiniWriter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TokenStream {
    private static volatile TokenStream instance;
    private final ConcurrentLinkedDeque<Token> tokensQueue;
    private final int maxCapacity;

    private Deque<List<Token>> savedState = new ArrayDeque<>();
    private static boolean consumedAfterMark = false;
    private static boolean marked = false;

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
        if (marked) {
            consumedAfterMark = true;
        }
        return instance.tokensQueue.poll();
    }

    /**
     * Peek at the next token in the stream without removing it.
     * @return the next token, or {@code null} if the stream is empty or not initialized
     */
    public static Token peek() {
        if (!hasInstance()) {
            return null;
        }
        return instance.tokensQueue.peek();
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

    /**
     * Save the current state of the token stream as a stack
     * a mark() must appear in pair with reset() or save()
     */
    public static void mark() {
        if (!hasInstance()) {
            MiniLogger.error("TokenStream not initialized, cannot mark");
            return;
        }

        var newMark = new ArrayList<>(instance.tokensQueue);
        instance.savedState.push(newMark);
        marked = true;
    }

    /**
     * Reset the token stream to the state saved by the last mark().
     * If no mark was made, this is a no-op.
     * must appear in pair after mark()
     */
    public static void reset() {
        if (!hasInstance()) {
            MiniLogger.error("TokenStream not initialized, cannot reset");
            return;
        }
        if (instance.savedState == null || instance.tokensQueue.isEmpty()) {
            // No mark was made, do nothing
            return;
        }
        // Clear current queue and restore from saved state
        var savedState = instance.savedState.pop();
        instance.tokensQueue.clear();
        instance.tokensQueue.addAll(savedState);
    }

    /**
     * save the current change after mark
     * drop the latest mark
     * must appear in pair after mark()
     */
    public static void save() {
        if (!hasInstance()) {
            MiniLogger.error("TokenStream not initialized, cannot reset");
            return;
        }
        if (instance.savedState == null || instance.tokensQueue.isEmpty()) {
            // No mark was made, do nothing
            return;
        }
        instance.savedState.pop();
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
