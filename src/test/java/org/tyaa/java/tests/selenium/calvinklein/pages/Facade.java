package org.tyaa.java.tests.selenium.calvinklein.pages;

import org.tyaa.java.tests.selenium.calvinklein.utils.ValueWrapper;
import org.tyaa.java.tests.selenium.calvinklein.utils.WebDriverFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/* Фасад, скрывающий работу с окном браузера и с моделями веб-страниц от классов тестов */
public class Facade {

    private final WebDriverFactory driverFactory;

    public Facade() {
        driverFactory = WebDriverFactory.getInstance();
    }

    public Facade open(String urlString) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        driverFactory.getDriver().get(urlString);
        driverFactory.getDriver().manage().window().maximize();
        return this;
    }

    public Facade close() {
        driverFactory.closeDriver();
        return this;
    }

    public Facade agreeAndCloseCookieModal () throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        new BasePage(driverFactory.getDriver()).clickAgreeButton();
        return this;
    }

    public Facade makeScreenshot (
        String pathToSave,
        ValueWrapper<Screenshot> screenshotWrapper
    ) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, InterruptedException {
        screenshotWrapper.value =
            new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100))
            .takeScreenshot(driverFactory.getDriver());
        File file = new File(pathToSave);
        if (file.getParentFile().mkdirs()) {
            ImageIO.write(screenshotWrapper.value.getImage(), "PNG", file);
        } else {
            System.err.printf("Screen persisting error (path: %s)", pathToSave);
        }
        return this;
    }

    public Facade makeScreenshotsDiff (
        String pathToSave,
        Screenshot firstScreenshot,
        Screenshot secondScreenshot,
        ValueWrapper<Integer> diffSizeWrapper
    ) throws IOException {
        ImageDiff diff =
            new ImageDiffer().makeDiff(firstScreenshot, secondScreenshot);
        File diffFile = new File(pathToSave);
        diffSizeWrapper.value = diff.getDiffSize();
        if (diffSizeWrapper.value > 0) {
            if (diffFile.getParentFile().mkdirs()) {
                ImageIO.write(diff.getMarkedImage(), "PNG", diffFile);
            } else {
                System.err.printf("Screen persisting error (path: %s)", pathToSave);
            }
        }
        return this;
    }
}
