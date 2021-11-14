package org.tyaa.java.tests.selenium.calvinklein.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.tyaa.java.tests.selenium.calvinklein.pages.Facade;
import org.tyaa.java.tests.selenium.calvinklein.utils.Global;
import org.tyaa.java.tests.selenium.calvinklein.utils.StringsFileReader;
import org.tyaa.java.tests.selenium.calvinklein.utils.ValueWrapper;
import org.tyaa.java.tests.selenium.calvinklein.utils.WebDriverFactory;
import ru.yandex.qatools.ashot.Screenshot;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

public class RegressionSplashScreenshotTest {

    private final String SNAPSHOTS_PATH_ROOT = "target/snapshots";
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
    public void givenProdAndLiveHomePageScreens_whenCompared_thenEqual(
        String currentProdUrl,
        String currentLiveUrl
    ) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, InterruptedException {
        String urlSuffix =
            currentLiveUrl.replace(liveUrl, "")
                .toUpperCase()
                .replace('.', '_')
                .replace('/', '_');
        ValueWrapper<Screenshot> actualScreenshotWrapper = new ValueWrapper<>();
        ValueWrapper<Screenshot> expectedScreenshotWrapper = new ValueWrapper<>();
        ValueWrapper<Integer> screenshotsDiffSizeWrapper = new ValueWrapper<>();
        domManipulatorFacade.open(currentProdUrl)
            .agreeAndCloseCookieModal()
            .makeScreenshot(
                String.format(
                    // "%s/actual/Splash%s_%s.png",
                    "%s/actual_Splash%s_%s/image.png",
                    SNAPSHOTS_PATH_ROOT,
                    urlSuffix,
                    System.currentTimeMillis()
                ),
                actualScreenshotWrapper
            ).close()
            .open(currentLiveUrl)
            .agreeAndCloseCookieModal()
            .makeScreenshot(
                String.format(
                    // "%s/expected/Splash%s_%s.png",
                    "%s/expected_Splash%s_%s/image.png",
                    SNAPSHOTS_PATH_ROOT,
                    urlSuffix,
                    System.currentTimeMillis()
                ),
                expectedScreenshotWrapper
            ).close()
            .makeScreenshotsDiff(
                String.format(
                    // "%s/diff/Splash%s_%s.png",
                    "%s/diff_Splash%s_%s/image.png",
                    SNAPSHOTS_PATH_ROOT,
                    urlSuffix,
                    System.currentTimeMillis()
                ),
                expectedScreenshotWrapper.value,
                actualScreenshotWrapper.value,
                screenshotsDiffSizeWrapper
            );
        assertEquals(
            screenshotsDiffSizeWrapper.value.intValue(),
            0,
            String.format("The snapshots of pages %s and %s are different", currentProdUrl, currentLiveUrl)
        );
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
