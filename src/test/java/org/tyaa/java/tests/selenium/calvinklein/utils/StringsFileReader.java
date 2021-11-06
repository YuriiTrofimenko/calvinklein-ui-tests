package org.tyaa.java.tests.selenium.calvinklein.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.stream.Stream;

public class StringsFileReader {
    public static Stream<String> read(String filePath) {
        Stream<String> urls = null;
        try {
            BufferedReader reader =
                new BufferedReader(
                    new FileReader(
                        new File(filePath)
                            .getAbsoluteFile()
                    )
                );
            urls = reader.lines();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return urls;
    }
}
