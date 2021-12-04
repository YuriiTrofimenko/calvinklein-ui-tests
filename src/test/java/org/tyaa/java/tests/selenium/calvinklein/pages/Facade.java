package org.tyaa.java.tests.selenium.calvinklein.pages;

import org.openqa.selenium.*;
import org.tyaa.java.tests.selenium.calvinklein.decorator.customwebelements.FooterNavMenu;
import org.tyaa.java.tests.selenium.calvinklein.decorator.customwebelements.NavMenuLink;
import org.tyaa.java.tests.selenium.calvinklein.utils.*;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public Facade navigateThroughAllTheSectionsAndCheckNoErrors (
        ValueWrapper<List<String>> errorStringsWrapper
    ) throws NoSuchMethodException, InterruptedException, IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        try {
            WebDriver driver = driverFactory.getDriver();
            BasePage startBasePage = new BasePage(driverFactory.getDriver());
            List<NavMenuLink> navigationLinkElements =
                startBasePage.getNavMenuLinks().collect(Collectors.toList());
            final int navLinksCount = navigationLinkElements.size();
            // startBasePage.clickCloseModalButton();
            if(!startBasePage.checkNoError()){
                errorStringsWrapper.value.add(
                    String.format(
                        "Error. Home page; Url: '%s'\n",
                        driverFactory.getDriver().getCurrentUrl()
                    )
                );
            }
            for (int i = 1; i <= navLinksCount; i++) {
                WebElement navMenuLinkItem =
                    driver.findElement(
                        By.cssSelector(
                            String.format(".mega-menu__first-level > li:nth-child(%d)", i)
                        )
                    );
                NavMenuLink navMenuLink = new NavMenuLink(
                    driver,
                    navMenuLinkItem.findElement(By.cssSelector("a"))
                );
                navMenuLink.safeClickThenWaitForDocument(Global.properties.getImplicitlyWaitSeconds());
                BasePage currentSectionPage = new BasePage(driverFactory.getDriver());
                if (i == 1) {
                    currentSectionPage.clickCloseModalButton();
                }
                if(!currentSectionPage.checkNoError()){
                    errorStringsWrapper.value.add(
                        String.format(
                            "Error. Link: '%s'; Url: '%s'\n",
                            navMenuLink.getAttribute("href"),
                            driverFactory.getDriver().getCurrentUrl()
                        )
                    );
                }
            }
        } catch (Exception ex) {
            this.close();
            throw ex;
        }
        return this;
    }

    public Facade makeScreenshot (
        String pathToSave,
        ValueWrapper<Screenshot> screenshotWrapper
    ) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, InterruptedException {
        BasePage page = new BasePage(driverFactory.getDriver());
        page.fixHeader();
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

    /* // заполнение списка элементов, найденных на первой версии сайта
    public Facade fillElementList (List<ModelToCompare> expectedElementList) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        BasePage page = new BasePage(driverFactory.getDriver());
        expectedElementList.forEach(item -> {
            WebElement element = page.getElementBySelector(item.xpathSelector);
            if (element != null) {
                item.element = element;
            } else {
                item.errorText = "Element not found";
            }
        });
        return this;
    } */

    /* // сравнение текстов и ссылок, найденных в элементах первой версии сайта,
    // с текстами и ссылками из соответствующих элементов второй версии сайта
    public Facade compareElements (List<ModelToCompare> expectedElementList) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        BasePage page = new BasePage(driverFactory.getDriver());
        expectedElementList.forEach(item -> {
            if (!item.isSecondSelectorDifferent) {
                WebElement element =
                    page.getElementBySelector(item.xpathSelector);
                if (element == null) {
                    item.errorText = "Element not found";
                } else {
                    if(element.getText() != null
                        && !element.getText().equals(item.element.getText())) {
                        item.errorText =
                            String.format(
                                "Texts not equal: %s != %s\n",
                                element.getText(),
                                item.element.getText()
                            );
                    }
                    if (item.element.getAttribute("href") != null
                        && !element.getAttribute("href").equals(item.element.getAttribute("href"))) {
                        item.errorText +=
                            String.format(
                                "Hrefs not equal: %s != %s\n",
                                element.getAttribute("href"),
                                item.element.getAttribute("href")
                            );
                    }
                }
            } else {
                if (!page.checkByText(item.text)) {
                    item.errorText = "Text not found";
                }
            }
        });
        return this;
    } */

    public Facade getAllTexts (List<String> texts) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        try {
            BasePage page = new BasePage(driverFactory.getDriver());
            texts.addAll(page.getAllTexts());
        } catch (Exception ex) {
            ex.printStackTrace();
            this.close();
            throw ex;
        }
        return this;
    }

    public Facade getAllUrls (List<String> urls) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        try {
            BasePage page = new BasePage(driverFactory.getDriver());
            urls.addAll(page.getAllUrls());
        } catch (Exception ex) {
            this.close();
            throw ex;
        }
        return this;
    }

    public Facade navigateThroughAllTheSectionsAndCompareContent (
        String baseUrl1,
        String baseUrl2,
        List<ContentComparisonResult> results
    ) throws NoSuchMethodException, InterruptedException, IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        // сбор всех строк текста и гиперссылок с первой версии всех страниц
        open(baseUrl1);
        agreeAndCloseCookieModal();
        WebDriver driver = driverFactory.getDriver();
        try {
            BasePage startBasePage = new BasePage(driverFactory.getDriver());
            List<NavMenuLink> navigationLinkElements =
                startBasePage.getNavMenuLinks().collect(Collectors.toList());
            final int navLinksCount = navigationLinkElements.size();
            for (int i = 1; i <= navLinksCount; i++) {
                if (Global.properties.getNavLinkSkipList().contains(i)) {
                    continue;
                }
                System.out.println("loop 1 = " + i);
                WebElement navMenuLinkItem =
                    driver.findElement(
                        By.cssSelector(
                            String.format(".mega-menu__first-level > li:nth-child(%d)", i)
                        )
                    );
                NavMenuLink navMenuLink = new NavMenuLink(
                    driver,
                    navMenuLinkItem.findElement(By.cssSelector("a"))
                );
                try {
                    navMenuLink.safeClickThenWaitForDocument(Global.properties.getImplicitlyWaitSeconds());
            } catch (ElementClickInterceptedException ex) {
                startBasePage.clickCloseModalButton();
                navMenuLink.safeClickThenWaitForDocument(Global.properties.getImplicitlyWaitSeconds());
            }
            ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(Global.properties.getImplicitlyWaitSeconds() * 1000);
            List<String> texts1 = new ArrayList<>();
                ((JavascriptExecutor) driver)
                    .executeScript("return document.getElementsByClassName('CKCountdown__timer-container')[0]?.remove();");
            getAllTexts(texts1);
            getAllUrls(texts1);
            texts1.forEach(s -> {
                results.add(new ContentComparisonResult(s.replace(baseUrl1, "").replace(baseUrl2, ""), null, driver.getCurrentUrl()));
            });
            System.out.println("texts1 count = " + texts1.size());
        }
    } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }

        // сбор всех строк текста и гиперссылок со второй версии всех страниц
        // для сравнения
        // с соответствующими строками текста и гиперссылками с первой версии всех страниц
        open(baseUrl2);
        agreeAndCloseCookieModal();
        List<String> texts2 = new ArrayList<>();
        try {
            BasePage startBasePage = new BasePage(driverFactory.getDriver());
            List<NavMenuLink> navigationLinkElements =
                startBasePage.getNavMenuLinks().collect(Collectors.toList());
            final int navLinksCount = navigationLinkElements.size();
            startBasePage.clickCloseModalButton();
            for (int i = 1; i <= navLinksCount; i++) {
                if (Global.properties.getNavLinkSkipList().contains(i)) {
                    continue;
                }
                System.out.println("loop 2 = " + i);
                WebElement navMenuLinkItem =
                    driver.findElement(
                        By.cssSelector(
                            String.format(".mega-menu__first-level > li:nth-child(%d)", i)
                        )
                    );
                NavMenuLink navMenuLink = new NavMenuLink(
                    driver,
                    navMenuLinkItem.findElement(By.cssSelector("a"))
                );
                try {
                    navMenuLink.safeClickThenWaitForDocument(Global.properties.getImplicitlyWaitSeconds());
                } catch (ElementClickInterceptedException ex) {
                    startBasePage.clickCloseModalButton();
                    navMenuLink.safeClickThenWaitForDocument(Global.properties.getImplicitlyWaitSeconds());
                }

                ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, document.body.scrollHeight)");
                Thread.sleep(Global.properties.getImplicitlyWaitSeconds() * 1000);
                ((JavascriptExecutor) driver)
                    .executeScript("return document.getElementsByClassName('CKCountdown__timer-container')[0]?.remove();");
                getAllTexts(texts2);
                getAllUrls(texts2);
                System.out.println("texts2 count = " + texts2.size());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            this.close();
        }

        for (int j = 0; j < results.size(); j++) {
            try {
                // System.out.printf("%s -> %s\n", results.get(j).text1, texts2.get(j));
                results.get(j).text2 = texts2.get(j).replace(baseUrl1, "").replace(baseUrl2, "");
            } catch (IndexOutOfBoundsException ignored) {}
        }

        System.out.println("results count = " + results.size());

        return this;
    }

    /* public Facade navigateThroughAllTheSidebarPagesAndCheckNoErrors(ValueWrapper<List<String>> errorStringsWrapper) {
        try {
            WebDriver driver = driverFactory.getDriver();
            BasePage startBasePage = new BasePage(driver);
            if(!startBasePage.checkNoError()){
                errorStringsWrapper.value.add(
                    String.format(
                        "Error. Home page; Url: '%s'\n",
                        driverFactory.getDriver().getCurrentUrl()
                    )
                );
            }
            FooterNavMenu customerServiceMenu =
                startBasePage.getFooterMenus().skip(2).findFirst().get();
            NavMenuLink faqMenuLink = customerServiceMenu.getLinks()
                .skip(customerServiceMenu.getLinks().count() - 1)
                .findFirst().get();
            faqMenuLink.safeClickThenWaitForDocument(Global.properties.getImplicitlyWaitSeconds());
            SideBarBasePage sideBarPage = new SideBarBasePage(driver);
            if(!sideBarPage.checkNoError()) {
                errorStringsWrapper.value.add(
                    String.format(
                        "Error. Faq page; Url: '%s'\n",
                        driverFactory.getDriver().getCurrentUrl()
                    )
                );
            }
            try {
                sideBarPage.getSidebar().findElements(By.cssSelector("*"));
            } catch (Exception ignored) {
                errorStringsWrapper.value.add(
                    String.format(
                        "Error: sidebar not found. Faq page; Url: '%s'\n",
                        driverFactory.getDriver().getCurrentUrl()
                    )
                );
            }

            FooterNavMenu aboutMenu =
                sideBarPage.getFooterMenus().skip(3).findFirst().get();
            List<NavMenuLink> aboutMenuLinks = aboutMenu.getLinks().collect(Collectors.toList());
            List<Integer> skipList =
                StringsFileReader.getSkipList("src/test/resources/sidebar-pages-skip-list.txt");
            for (int i = 0; i < aboutMenuLinks.size(); i++) {
                if (!skipList.contains(i + 1)) {
                    sideBarPage.clickCloseModalButton();
                    aboutMenuLinks.get(i).safeClickThenWaitForDocument(Global.properties.getImplicitlyWaitSeconds());
                    sideBarPage = new SideBarBasePage(driver);
                    if(!sideBarPage.checkNoError()) {
                        errorStringsWrapper.value.add(
                            String.format(
                                "Error. Faq page; Url: '%s'\n",
                                driverFactory.getDriver().getCurrentUrl()
                            )
                        );
                    }
                    try {
                        sideBarPage.getSidebar().findElements(By.cssSelector("*"));
                    } catch (Exception ignored) {
                        errorStringsWrapper.value.add(
                            String.format(
                                "Error: sidebar not found. Some sidebar page; Url: '%s'\n",
                                driverFactory.getDriver().getCurrentUrl()
                            )
                        );
                    }
                    aboutMenu =
                        sideBarPage.getFooterMenus().skip(3).findFirst().get();
                    aboutMenuLinks = aboutMenu.getLinks().collect(Collectors.toList());
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            this.close();
        }
        return this;
    } */

    public Facade navigateThroughAllTheSidebarPagesAndCheckNoErrors(ValueWrapper<List<String>> errorStringsWrapper, Integer footerGroupNumber) {
        try {
            WebDriver driver = driverFactory.getDriver();
            BasePage page = new BasePage(driver);
            if(!page.checkNoError()){
                errorStringsWrapper.value.add(
                    String.format(
                        "Error. Home page; Url: '%s'\n",
                        driverFactory.getDriver().getCurrentUrl()
                    )
                );
            }
            FooterNavMenu footerGroupMenu =
                page.getFooterMenus().skip(footerGroupNumber - 1).findFirst().get();
            List<NavMenuLink> footerGroupMenuLinks = footerGroupMenu.getLinks().collect(Collectors.toList());
            List<Integer> skipList =
                StringsFileReader.getSkipList(
                    String.format("src/test/resources/sidebar-pages-skip-list-%s.txt",
                    footerGroupNumber)
                );
            for (int i = 0; i < footerGroupMenuLinks.size(); i++) {
                if (!skipList.contains(i + 1)) {
                    page.clickCloseModalButton();
                    footerGroupMenuLinks.get(i).safeClickThenWaitForDocument(Global.properties.getImplicitlyWaitSeconds());
                    page = new SideBarBasePage(driver);
                    if(!page.checkNoError()) {
                        errorStringsWrapper.value.add(
                            String.format(
                                "Error. Some sidebar page; Url: '%s'\n",
                                driverFactory.getDriver().getCurrentUrl()
                            )
                        );
                    }
                    try {
                        ((SideBarBasePage)page).getSidebar().findElements(By.cssSelector("*"));
                    } catch (Exception ignored) {
                        errorStringsWrapper.value.add(
                            String.format(
                                "Error: sidebar not found. Some sidebar page; Url: '%s'\n",
                                driverFactory.getDriver().getCurrentUrl()
                            )
                        );
                    }
                    footerGroupMenu =
                        page.getFooterMenus().skip(3).findFirst().get();
                    footerGroupMenuLinks = footerGroupMenu.getLinks().collect(Collectors.toList());
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception message: " + ex.getMessage());
            ex.printStackTrace();
            this.close();
        }
        return this;
    }
}
