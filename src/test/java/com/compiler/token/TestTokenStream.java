package com.compiler.token;

import com.compiler.utils.PathConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestTokenStream {
    @Test
    public void testProduceAndConsume() {
        TokenStream.initialize();

        TokenStream.produce(new Token("12", "constant", 0));
        TokenStream.produce(new Token("begin", "begin", 0));
        TokenStream.produce(new Token("14", "constant", 0));

        Assertions.assertEquals("12", TokenStream.consume().getText());
        Assertions.assertTrue(TokenStream.consume().equals(new Token("begin", "begin", 0)));
        Assertions.assertTrue(TokenStream.consume().equals(new Token("15", "constant", 0)));
    }

    @Test
    public void testTokenStream() {
        TokenStream.initialize();
        TokenStream.setOutputFile(PathConfig.OUTPUT_DYD);
        TokenStream.produceWithOutput(new Token("21", "constant", 0));

        try {
            String read = Files.readString(Paths.get(PathConfig.OUTPUT_DYD));
            Files.delete(Paths.get(PathConfig.OUTPUT_DYD));
            Assertions.assertEquals("constant 11", read.trim());
        } catch (IOException e) {
            System.out.println("Error reading file");
        }
    }
}
