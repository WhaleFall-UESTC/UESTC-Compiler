package com.compiler.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestCharacterStream {
    @Test
    public void testCharacterStream() {
        String message = "[INFO] --- compiler:3.8.1:testCompile (default-testCompile) @ miniCompiler ---\n[INFO] Nothing to compile - all classes are up to date";
        String filePath = "testCharacterStream.txt";
        Path file = Paths.get(filePath);

        try {
            Files.write(file, message.getBytes());
        } catch (IOException e) {
            System.out.println("Error writing to file");
            return;
        }

        CharacterStream.initialize(filePath);

        Assertions.assertEquals('[', CharacterStream.peek(0));
        Assertions.assertEquals('I', CharacterStream.peek(1));
        Assertions.assertEquals('N', CharacterStream.peek(2));
        Assertions.assertEquals('[', CharacterStream.peek());
        Assertions.assertEquals('F', CharacterStream.peek(3));
        Assertions.assertEquals('O', CharacterStream.peek(4));
        Assertions.assertEquals(']', CharacterStream.peek(5));
        Assertions.assertEquals('[', CharacterStream.peek());

        var sb = new StringBuilder();
        while (CharacterStream.isHasNext()) {
            sb.append(CharacterStream.consume());
        }
        CharacterStream.close();

        Assertions.assertEquals(message, sb.toString());

        try {
            Files.delete(file);
        } catch (IOException e) {
            System.out.println("Error deleting file");
        }
    }

    @Test
    public void testConsumeWhile() {
        String message = "Hello World! Compiler!";
        String filePath = "testConsumeWhile.txt";
        MiniWriter writer = new MiniWriter(filePath);
        writer.write(message);

        CharacterStream.initialize(filePath);

        String read =  CharacterStream.consumeWhile(c -> !Character.isWhitespace(c));
        Assertions.assertEquals("Hello", read);

        try {
            Files.delete(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("Error deleting file");
        }
    }
}
