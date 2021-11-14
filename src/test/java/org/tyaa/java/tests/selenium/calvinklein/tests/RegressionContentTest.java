package org.tyaa.java.tests.selenium.calvinklein.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.tyaa.java.tests.selenium.calvinklein.pages.Facade;
import org.tyaa.java.tests.selenium.calvinklein.utils.ContentComparisonResult;
import org.tyaa.java.tests.selenium.calvinklein.utils.Global;
import org.tyaa.java.tests.selenium.calvinklein.utils.StringsFileReader;
import org.tyaa.java.tests.selenium.calvinklein.utils.ValueWrapper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.fail;

public class RegressionContentTest {

    private Facade domManipulatorFacade;
    private final String prodUrl = Global.properties.getProdUrl();
    private final String liveUrl = Global.properties.getLiveUrl();
    private final String prodUrl2 = Global.properties.getProdUrl();
    private final String liveUrl2 = Global.properties.getLiveUrl();

    @BeforeClass
    public void appSetup () {
        domManipulatorFacade = new Facade();
    }

    @Test(dataProvider = "urls")
    public void givenAllPagesContent_whenCompared_thenEqual(
        String currentProdUrl,
        String currentLiveUrl
    ) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, InterruptedException {

        List<ContentComparisonResult> results = new ArrayList<>();

        domManipulatorFacade.navigateThroughAllTheSectionsAndCompareContent(
                currentProdUrl,
                currentLiveUrl,
                results
            ).close();
        ValueWrapper<Boolean> testFiled = new ValueWrapper<>();
        testFiled.value = false;
        results.forEach(result -> {
            if (!result.text1.equals(result.text2)) {
                testFiled.value = true;
                System.out.printf(
                    "No equality: %s != %s (url = %s)\n",
                    result.text1,
                    result.text2,
                    result.url
                );
            }
        });
        if (testFiled.value) {
            fail();
        }
    }

    @DataProvider(parallel = true)
    public Object[][] urls() {
        List<String> urls =
            StringsFileReader.read("src/test/resources/urls.txt").collect(Collectors.toList());
        int rowAmount = urls.size();
        int columnAmount = 2;
        Object[][] urls2DArray = new Object[rowAmount][columnAmount];
        for (int i = 0; i < rowAmount; i++) {
            String currentProdUrl;
            if (urls.get(i).contains(liveUrl)) {
                currentProdUrl = urls.get(i).replace(liveUrl, prodUrl);
            } else {
                currentProdUrl = urls.get(i).replace(liveUrl2, prodUrl2);
            }
            urls2DArray[i][0] = currentProdUrl;
            urls2DArray[i][1] = urls.get(i);
        }
        return urls2DArray;
    }
}
