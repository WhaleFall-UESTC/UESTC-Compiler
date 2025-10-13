package com.compiler.token;

import com.compiler.utils.MiniLogger;
import com.compiler.utils.PathConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestTokenStream {
    @Test
    public void testProduceAndConsume() {
        TokenStream.initialize();

        TokenStream.produce(new Token("12", "constant"));
        TokenStream.produce(new Token("begin", "begin"));
        TokenStream.produce(new Token("14", "constant"));

        Assertions.assertEquals("12", TokenStream.consume().getText());
        Assertions.assertTrue(TokenStream.consume().equals(new Token("begin", "begin")));
        Assertions.assertTrue(TokenStream.consume().match(new Token("15", "constant")));
    }

    @Test
    public void testTokenStream() {
        TokenStream.initialize();
        TokenStream.setOutputFile(PathConfig.TOKENS_DYD);
        TokenStream.produceWithOutput(new Token("21", "constant"));

        try {
            String read = Files.readString(Paths.get(PathConfig.TOKENS_DYD));
            Files.delete(Paths.get(PathConfig.TOKENS_DYD));
            Assertions.assertEquals("constant 11", read.trim());
        } catch (IOException e) {
            System.out.println("Error reading file");
        }
    }
}
