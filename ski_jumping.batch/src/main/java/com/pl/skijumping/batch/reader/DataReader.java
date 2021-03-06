package com.pl.skijumping.batch.reader;

import com.pl.skijumping.diagnosticmonitor.DiagnosticMonitor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DataReader {
    private final DiagnosticMonitor diagnosticMonitor;

    public DataReader(DiagnosticMonitor diagnosticMonitor) {
        this.diagnosticMonitor = diagnosticMonitor;
    }

    public String read(String filePath) {
        if(filePath == null) {
            diagnosticMonitor.logError("Filepath cannot be null!", getClass());
            return null;
        }

        diagnosticMonitor.logInfo(String.format("Start reading from file %s", filePath));
        List<String> fileLines;
        try {
            Path pathToFile = Paths.get(filePath);
            if (!pathToFile.toFile().exists()) {
                String errorMessage = "Cannot read from not existing file";
                diagnosticMonitor.logError(errorMessage, getClass());
                return null;
            }

            fileLines = Files.readAllLines(pathToFile, StandardCharsets.UTF_8);
            diagnosticMonitor.logInfo(String.format("Found %d lines to convert", fileLines.size()));
        } catch (IOException e) {
            diagnosticMonitor.logError(String.format("Cannot read content from file %s", filePath), getClass());
            return null;
        }

        if (fileLines.isEmpty()) {
            diagnosticMonitor.logInfo("File content is null or empty");
            return null;
        }

        String fileContent = String.join("", fileLines);

        diagnosticMonitor.logInfo(String.format("Finish reading file from: %s", filePath));
        fileContent = fileContent.replaceAll("  ", "");
        return fileContent.replaceAll("   ", "");
    }
}
