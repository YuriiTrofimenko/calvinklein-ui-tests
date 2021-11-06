package org.tyaa.java.tests.selenium.calvinklein.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.tyaa.java.tests.selenium.calvinklein.pages.Facade;
import org.tyaa.java.tests.selenium.calvinklein.utils.Global;
import org.tyaa.java.tests.selenium.calvinklein.utils.StringsFileReader;
import org.tyaa.java.tests.selenium.calvinklein.utils.ValueWrapper;
import ru.yandex.qatools.ashot.Screenshot;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class GeneralSITsTest {

    private Facade domManipulatorFacade;

    @BeforeClass
    public void appSetup () {
        domManipulatorFacade = new Facade();
    }

    @Test(dataProvider = "urls")
    public void givenNavMenuLinks_whenIterate_thenNoErrors(String currentUrl) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, InterruptedException {
        ValueWrapper<List<String>> errorStringsWrapper = new ValueWrapper<>();
        errorStringsWrapper.value = new ArrayList<>();
        domManipulatorFacade.open(currentUrl)
            .agreeAndCloseCookieModal()
            .navigateThroughAllTheSectionsAndCheckNoErrors(errorStringsWrapper)
            .close();
        if (errorStringsWrapper.value.size() > 0) {
            System.err.println("*** Navigation errors ***");
            errorStringsWrapper.value.forEach(System.err::println);
            System.err.println("******");
            fail();
        }
    }

    @DataProvider(parallel = true)
    public Object[][] urls() {
        List<String> urls =
            StringsFileReader.read("src/test/resources/urls.txt").collect(Collectors.toList());
        final int rowAmount = urls.size();
        final int columnAmount = 1;
        Object[][] urlsArray = new Object[rowAmount][columnAmount];
        for (int i = 0; i < rowAmount; i++) {
            urlsArray[i][0] = urls.get(i);
        }
        return urlsArray;
    }
}
