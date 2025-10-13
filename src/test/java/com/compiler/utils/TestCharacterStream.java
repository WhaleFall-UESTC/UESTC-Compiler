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
}
