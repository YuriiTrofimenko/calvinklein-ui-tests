package org.tyaa.java.tests.selenium.calvinklein.utils;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WebDriverFactory {

    private static Map<String, String> browsers;
    private static WebDriverFactory instance;
    private final ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<>();

    private String browser;

    static {
        WebDriverFactory.browsers = Global.properties.getSupportedBrowsers();
    }

    public static WebDriverFactory getInstance() {
        if (WebDriverFactory.instance == null) {
            WebDriverFactory.instance = new WebDriverFactory();
        }
        return WebDriverFactory.instance;
    }

    public WebDriver getDriver() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (webDriverThreadLocal.get() != null) {
            return webDriverThreadLocal.get();
        }
        try {
            String browser = System.getProperty("browser");
            this.browser =
                browser != null && !browser.isBlank()
                    ? browser.toLowerCase()
                    : Global.properties.getDefaultBrowser().getKey();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        if (!WebDriverFactory.browsers.containsKey(this.browser)) {
            throw new IllegalArgumentException(String.format("Driver %s Not Found", this.browser));
        }
        String driverFileNameExtension =
            Global.properties.getOs().equals("windows")
                ? ".exe"
                : "";
        System.setProperty(
            String.format("webdriver.%s.driver", this.browser),
            String.format("src/test/resources/drivers/%sdriver%s", this.browser, driverFileNameExtension)
        );
        String browserClassName = WebDriverFactory.browsers.get(this.browser);
        Class<?> browserDriverClass =
            Class.forName(String.format(
                "org.openqa.selenium.%s.%sDriver",
                browserClassName,
                StringUtils.capitalize(browserClassName))
            );
        WebDriver driver =
            (WebDriver) browserDriverClass.getConstructor().newInstance();
        driver.manage().timeouts().implicitlyWait(
            Global.properties.getImplicitlyWaitSeconds(),
            TimeUnit.SECONDS
        );
        webDriverThreadLocal.set(driver);
        return webDriverThreadLocal.get();
    }

    public void closeDriver() {
        try {
            webDriverThreadLocal.get().quit();
        }
        catch (Exception ex) {
            System.err.println("ERROR: Can not close Webdriver!");
        } finally {
            webDriverThreadLocal.remove();
        }
    }

}