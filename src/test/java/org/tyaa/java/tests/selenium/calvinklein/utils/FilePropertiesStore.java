package org.tyaa.java.tests.selenium.calvinklein.utils;

import org.tyaa.java.tests.selenium.calvinklein.utils.interfaces.IPropertiesStore;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FilePropertiesStore implements IPropertiesStore {

    private static final String PROPS_CATALOG = "src/test/resources/";
    private static final Set<String> PROPS_FILE_NAMES =
        Set.of("supported-browsers", "main-config", "base-urls", "skip-list");
    private static final Properties properties = new Properties();

    private static Map<String, String> supportedBrowsers;
    // private static Map<String, String> domains;
    private static Map<String, List<Integer>> skipLists;

    static {
        PROPS_FILE_NAMES.forEach(propsFileName -> {
            try (FileInputStream fis =
                     new FileInputStream(
                         String.format("%s%s.properties", PROPS_CATALOG, propsFileName)
                     )
            ) {
                properties.load(fis);
            } catch (IOException ex) {
                System.err.printf("ERROR: properties file '%s' does not exist", propsFileName);
            }
        });
    }

    @Override
    public Map<String, String> getSupportedBrowsers() {
        return (supportedBrowsers != null)
            ? supportedBrowsers
            : (supportedBrowsers = properties.entrySet().stream()
                .filter(entry -> entry.getKey().toString().startsWith("driver."))
                .collect(Collectors.toMap(
                    entry -> entry.getKey().toString().replace("driver.", ""),
                    entry -> entry.getValue().toString(),
                    (o, o2) -> o,
                    LinkedHashMap::new
                )));
    }

    @Override
    public Map.Entry<String, String> getDefaultBrowser() throws Exception {
        String defaultBrowserKey = properties.getProperty("default-browser");
        Optional<Map.Entry<String, String>> defaultBrowserOptional =
            getSupportedBrowsers().entrySet().stream()
                .filter(entry -> entry.getKey().equals(defaultBrowserKey))
                .findFirst();
        if (defaultBrowserOptional.isPresent()) {
            return defaultBrowserOptional.get();
        } else {
            throw new Exception("Default browser info not found");
        }
    }

    @Override
    public String getOs() {
        return properties.getProperty("os");
    }

    @Override
    public Integer getImplicitlyWaitSeconds() {
        return Integer.parseInt(properties.getProperty("implicitlyWaitSeconds"));
    }

    @Override
    public String getProdUrl() {
        return properties.getProperty("prod");
    }

    @Override
    public String getLiveUrl() {
        return properties.getProperty("live");
    }

    @Override
    public String getProdUrl2() {
        return properties.getProperty("prod2");
    }

    @Override
    public String getLiveUrl2() {
        return properties.getProperty("live2");
    }

    @Override
    public List<Integer> getSkipList(String url) {
        if (skipLists == null) {
            skipLists = properties.entrySet().stream()
                .filter(entry -> entry.getKey().toString().startsWith("https"))
                .collect(Collectors.toMap(
                    entry -> entry.getKey().toString(),
                    entry -> {
                        String[] indexStrings = entry.getValue().toString().split(",");
                        List<Integer> indexIntegers = new ArrayList<>();
                        for (String indexString : indexStrings) {
                            indexIntegers.add(Integer.parseInt(indexString));
                        }
                        return indexIntegers;
                    },
                    (o, o2) -> o,
                    LinkedHashMap::new
                ));
        }
        return skipLists.get(url);
    }
}
