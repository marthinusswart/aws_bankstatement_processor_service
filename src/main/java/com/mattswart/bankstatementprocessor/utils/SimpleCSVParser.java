package com.mattswart.bankstatementprocessor.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class SimpleCSVParser {
private static final char DEFAULT_SEPARATOR = ',';
    private static final char DOUBLE_QUOTES = '"';
    private static final char DEFAULT_QUOTE_CHAR = DOUBLE_QUOTES;
    private static final String NEW_LINE = "\n";

    private boolean isMultiLine = false;
    private String pendingField = "";
    private String[] pendingFieldLine = new String[] {};

    private BufferedReader br;

    public void initialiseFile(String csvFileString) throws Exception {
        File file = Paths.get(csvFileString).toFile();
        initialiseFile(file);
    }

    public void initialiseFile(File csvFile) throws Exception {
        var fileReader = new FileReader(csvFile);
        this.br = new BufferedReader(fileReader);
    }

    public String[] readLine() throws Exception {
        String line = this.br.readLine();
        if (line != null){
            var csvRecord = parseLine(line);
            return csvRecord;
        } else {
            return null;
        }        
    }

    private String[] parseLine(String line)
            throws Exception {

        List<String> result = new ArrayList<>();

        boolean inQuotes = false;
        boolean isFieldWithEmbeddedDoubleQuotes = false;
        var quoteChar = DEFAULT_QUOTE_CHAR;
        var separator = DEFAULT_SEPARATOR;

        StringBuilder field = new StringBuilder();

        for (char c : line.toCharArray()) {

            if (c == DOUBLE_QUOTES) { // handle embedded double quotes ""
                if (isFieldWithEmbeddedDoubleQuotes) {

                    if (field.length() > 0) { // handle for empty field like "",""
                        field.append(DOUBLE_QUOTES);
                        isFieldWithEmbeddedDoubleQuotes = false;
                    }

                } else {
                    isFieldWithEmbeddedDoubleQuotes = true;
                }
            } else {
                isFieldWithEmbeddedDoubleQuotes = false;
            }

            if (isMultiLine) { // multiline, add pending from the previous field
                field.append(pendingField).append(NEW_LINE);
                pendingField = "";
                inQuotes = true;
                isMultiLine = false;
            }

            if (c == quoteChar) {
                inQuotes = !inQuotes;
            } else {
                if (c == separator && !inQuotes) { // if find separator and not in quotes, add field to the list
                    result.add(field.toString());
                    field.setLength(0); // empty the field and ready for the next
                } else {
                    field.append(c); // else append the char into a field
                }
            }

        }

        // line done, what to do next?
        if (inQuotes) {
            pendingField = field.toString(); // multiline
            isMultiLine = true;
        } else {
            result.add(field.toString()); // this is the last field
        }

        return result.toArray(String[]::new);

    }
}
