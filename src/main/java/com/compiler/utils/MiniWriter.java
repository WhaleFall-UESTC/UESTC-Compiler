package com.compiler.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class MiniWriter {
    private final String filePath;

    public MiniWriter(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            MiniLogger.error("filePath is null or empty");
        }
        this.filePath = filePath;
        clear();
    }

    public boolean clear() {
        try (FileWriter writer = new FileWriter(filePath, false)) {
            return true;
        } catch (IOException e) {
            MiniLogger.error("Failed to clear file: " + filePath, e);
            return false;
        }
    }

    public boolean write(String content) {
        if (filePath == null) {
            MiniLogger.error("filePath is null");
            return false;
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, StandardCharsets.UTF_8, true))) {
            writer.println(content);
            return true;
        } catch (IOException e) {
            MiniLogger.error("Failed to write {}, {}", filePath, e);
            return false;
        }
    }

    public String getFilePath() {
        return filePath;
    }
}
