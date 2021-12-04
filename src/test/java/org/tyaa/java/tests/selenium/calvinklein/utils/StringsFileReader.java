package org.tyaa.java.tests.selenium.calvinklein.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
        return urls != null ? urls.filter(s -> !s.startsWith("//")) : null;
    }
    public static List<Integer> getSkipList(String filePath) throws IOException {
        List<Integer> numbers = null;
        try {
            BufferedReader reader =
                new BufferedReader(
                    new FileReader(
                        new File(filePath)
                            .getAbsoluteFile()
                    )
                );
            numbers = Arrays.stream(reader.readLine().split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return numbers;
    }
}
