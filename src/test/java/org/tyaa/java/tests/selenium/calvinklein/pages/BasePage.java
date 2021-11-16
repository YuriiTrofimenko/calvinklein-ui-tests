package org.tyaa.java.tests.selenium.calvinklein.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.tyaa.java.tests.selenium.calvinklein.decorator.CustomWebElementFieldDecorator;
import org.tyaa.java.tests.selenium.calvinklein.decorator.customwebelements.BaseElement;
import org.tyaa.java.tests.selenium.calvinklein.decorator.customwebelements.Button;
import org.tyaa.java.tests.selenium.calvinklein.decorator.customwebelements.NavMenu;
import org.tyaa.java.tests.selenium.calvinklein.decorator.customwebelements.NavMenuLink;
import org.tyaa.java.tests.selenium.calvinklein.utils.Global;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;

/* Основная модель страницы, которая подходит под любую страницу сайта */
public class BasePage {

    protected WebDriver driver;

    @FindBy(className = "header")
    private WebElement header;

    @FindBy(className = "mega-menu__first-level")
    private NavMenu navMenu;

    private final By BODY_LOCATOR = By.cssSelector("body");
    private final By H1_LOCATOR = By.cssSelector("h1");
    private final By ERROR_BLOCK_LOCATOR = By.cssSelector(".genericErrorSpot");

    private final int MAX_ATTEMPT_COUNT = 3;

    private int attemptCount = 0;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(
            webDriver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState")
                .equals("complete")
        );
        PageFactory.initElements(new CustomWebElementFieldDecorator(driver), this);
        Actions actions = new Actions(driver);
        actions.moveToElement(header).perform();
    }

    public BasePage clickAgreeButton() {
        try {
            Button agreeButton =
                new Button(
                    driver,
                    driver.findElement(By.xpath("//button[contains(@class,'cookie-notice__agree-button')]"))
                );
            agreeButton.safeClickThenWaitForDisappear(
                By.xpath("//div[contains(@class,'ck-modal--cookieModalMain')]"),
                Global.properties.getImplicitlyWaitSeconds()
            );
            ((JavascriptExecutor) driver)
                .executeScript("!!document.activeElement ? document.activeElement.blur() : 0");
            sleep(3000);
        } catch (NoSuchElementException | InterruptedException ignored) {}
        return new BasePage(driver);
    }

    public BasePage clickCloseModalButton() throws InterruptedException {
        try {
            Button modalCloseButton =
                new Button(
                    driver,
                    driver.findElement(By.cssSelector(".ck-Button__no-style.ck-modal__close-btn"))
                );
            modalCloseButton.safeClickThenWaitForDisappear(
                By.cssSelector("ck-Button__no-style ck-modal__close-btn"),
                Global.properties.getImplicitlyWaitSeconds()
            );
        } catch (NoSuchElementException ignored) {
            /* if (attemptCount < MAX_ATTEMPT_COUNT) {
                sleep(1000);
                attemptCount++;
                clickCloseModalButton();
            } else {
                attemptCount = 0;
            } */
        }
        return new BasePage(driver);
    }

    public Stream<NavMenuLink> getNavMenuLinks() {
        return navMenu.getLinks();
    }

    public boolean checkNoError() {
        WebElement body = null;
        WebElement h1 = null;
        WebElement errorBlock = null;
        boolean isInternalServerError = false;
        boolean isBadGateway = false;
        try {
            body = driver.findElement(BODY_LOCATOR);
            h1 = driver.findElement(H1_LOCATOR);
            errorBlock = driver.findElement(ERROR_BLOCK_LOCATOR);
        } catch (NoSuchElementException ignored) {}
        if (body != null) {
            isInternalServerError = body.getText().equals("Internal Server Error");
        }
        if (h1 != null) {
            isBadGateway = h1.getText().equals("502 Bad Gateway");
        }
        return !isInternalServerError && !isBadGateway && errorBlock == null;
    }

    public void fixHeader() {
        if (header != null)
            ((JavascriptExecutor) driver)
                .executeScript("arguments[0].style.position='initial'", header);
    }

    /* public boolean checkByText (String text){
        WebElement element = driver.findElement(
            By.xpath(
                String.format("//*[text='%s']", text)
            )
        );
        return element != null;
    } */

    /* public WebElement getElementBySelector (String xpathSelector){
        WebElement element = driver.findElement(
            By.xpath(xpathSelector)
        );
        return element;
    } */

    /* public WebElement checkBySelectorAndHref (String xpathSelector, String href){
        WebElement element = driver.findElement(
            By.xpath(xpathSelector)
        );
        return (element != null && element.getAttribute("href").equals(href)) ? element : null;
    } */

    public List<String> getAllTexts () {
        return driver.findElements(By.xpath("//*[text()]")).stream()
            .map(WebElement::getText)
            .filter(s -> s != null && !s.isBlank())
            .collect(Collectors.toList());
    }

    public List<String> getAllUrls () {
        return driver.findElements(By.xpath("//a")).stream()
            .map(element -> element.getAttribute("href"))
            .filter(s -> s != null && !s.isBlank())
            .collect(Collectors.toList());
    }
}
