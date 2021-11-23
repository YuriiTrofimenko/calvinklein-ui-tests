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

public class DemoTest {

    private Facade domManipulatorFacade;
    private final String prodUrl = Global.properties.getProdUrl();
    private final String liveUrl = Global.properties.getLiveUrl();
    private final String prodUrl2 = Global.properties.getProdUrl2();
    private final String liveUrl2 = Global.properties.getLiveUrl2();

    @BeforeClass
    public void appSetup () {
        domManipulatorFacade = new Facade();
    }

    @Test(dataProvider = "urls")
    public void givenAllPagesContent_whenCompared_thenEqual(
        String currentProdUrl,
        String currentLiveUrl
    ) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, InterruptedException {

        System.out.printf("currentProdUrl = %s, currentLiveUrl = %s\n", currentProdUrl, currentLiveUrl);
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
            System.out.println(urls.get(i));
            if (urls.get(i).contains(liveUrl)) {
                System.out.println(1);
                currentProdUrl = urls.get(i).replace(liveUrl, prodUrl);
            } else {
                System.out.println(2);
                currentProdUrl = urls.get(i).replace(liveUrl2, prodUrl2);
                System.out.println(currentProdUrl);
            }
            urls2DArray[i][0] = currentProdUrl;
            urls2DArray[i][1] = urls.get(i);
        }
        return urls2DArray;
    }
}
