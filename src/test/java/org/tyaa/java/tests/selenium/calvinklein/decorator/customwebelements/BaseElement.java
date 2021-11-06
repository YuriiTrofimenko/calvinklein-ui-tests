package org.tyaa.java.tests.selenium.calvinklein.decorator.customwebelements;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/* Базовая оболочка для веб-элементов */
public class BaseElement implements WebElement {

    protected WebDriver driver;
    protected WebElement element;

    public BaseElement(WebDriver driver, WebElement element) {
        this.driver = driver;
        this.element = element;
    }

    /* Ожидание завершения изменений элемента */
    public static void waitForUpdate(
        WebDriver driver,
        WebElement elementToWaitForUpdate,
        long timeOutInSeconds
    ) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        try {
            wait.until(ExpectedConditions.stalenessOf(elementToWaitForUpdate));
        } catch (TimeoutException ignored) {}
    }

    public static void performAndWaitForUpdate(
        WebDriver driver,
        Runnable actionToPerform,
        long timeOutInSeconds
    ) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        actionToPerform.run();
        wait.until(
            webDriver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState")
                .equals("complete")
        );
    }

    public static void performAndWaitForUpdate(
        WebDriver driver,
        Runnable actionToPerform,
        WebElement elementToWaitForUpdate,
        long timeOutInSeconds
    ) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        actionToPerform.run();
        try {
            wait.until(ExpectedConditions.stalenessOf(elementToWaitForUpdate));
        } catch (TimeoutException ignored) {}
    }

    public static void performAndWaitForUpdate(
        WebDriver driver,
        Runnable actionToPerform,
        By locatorToWaitForElementUpdate,
        long timeOutInSeconds
    ) {
        WebElement elementToWaitForUpdate = driver.findElement(locatorToWaitForElementUpdate);
        performAndWaitForUpdate(driver, actionToPerform, elementToWaitForUpdate, timeOutInSeconds);
    }

    public static void moveToElementAndWaitForUpdate(
        WebDriver driver,
        WebElement target,
        By locatorToWaitForElementUpdate,
        long timeOutInSeconds
    ) {
        performAndWaitForUpdate(
            driver,
            () -> {
                Actions actions = new Actions(driver);
                actions.moveToElement(target).perform();
            },
            locatorToWaitForElementUpdate,
            timeOutInSeconds
        );
    }

    /* Выполнить действие actionToPerform с элементом,
    * затем ожидать максимум до timeOutInSeconds секунд,
    * пока не исчезнет элемент с селектором locatorToWaitForDisappear */
    public static void performAndWaitForDisappear(
        WebDriver driver,
        Runnable actionToPerform,
        By locatorToWaitForDisappear,
        long timeOutInSeconds
    ) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        actionToPerform.run();
        try {
            wait.until(
                ExpectedConditions.not(
                    ExpectedConditions.presenceOfElementLocated(locatorToWaitForDisappear)
                )
            );
        } catch (TimeoutException ignored) {}
    }

    /* Попытаться выполнить действие с элементом,
    * и если элемент был перекрыт другим элементом
    * или обновлялся - повторять попытку снова, пока не получится выполнить действие */
    public void safeAction(Runnable actionToPerform, long timeOutInSeconds) {
        try {
            actionToPerform.run();
        } catch (ElementClickInterceptedException | StaleElementReferenceException ignored) {
            new WebDriverWait(driver, timeOutInSeconds)
                .until(ExpectedConditions.refreshed(
                    ExpectedConditions.elementToBeClickable(element)));
            actionToPerform.run();
        }
    }

    @Override
    public void click() {
        element.click();
    }

    @Override
    public void submit() {
        element.submit();
    }

    @Override
    public void sendKeys(CharSequence... charSequences) {
        element.sendKeys();
    }

    @Override
    public void clear() {
        element.clear();
    }


    @Override
    public boolean isSelected() {
        return element.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return element.isEnabled();
    }

    @Override
    public boolean isDisplayed() {
        return element.isDisplayed();
    }

    @Override
    public String getText() {
        return element.getText();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return element.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return element.findElement(by);
    }

    @Override
    public Point getLocation() {
        return element.getLocation();
    }

    @Override
    public Dimension getSize() {
        return element.getSize();
    }

    @Override
    public Rectangle getRect() {
        return element.getRect();
    }

    @Override
    public String getCssValue(String s) {
        return element.getCssValue(s);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
        return getScreenshotAs(outputType);
    }

    @Override
    public String getTagName() {
        return element.getTagName();
    }

    @Override
    public String getAttribute(String s) {
        return element.getAttribute(s);
    }
}
