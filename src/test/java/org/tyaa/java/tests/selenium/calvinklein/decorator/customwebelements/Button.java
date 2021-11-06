package org.tyaa.java.tests.selenium.calvinklein.decorator.customwebelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/* Оболочка для веб-элементов типа "Кнопка" */
public class Button extends BaseElement {
    public Button(WebDriver driver, WebElement element) {
        super(driver, element);
    }
    /* Выполнить клик по текущему элементу,
     * и если элемент был перекрыт другим элементом
     * или обновлялся - повторять попытку снова, пока не получится выполнить клик */
    public void safeClick(long timeOutInSeconds) {
        safeAction(() -> element.click(), timeOutInSeconds);
    }
    /* Выполнить клик по текущему элементу,
     * затем ожидать максимум до timeOutInSeconds секунд,
     * пока не исчезнет элемент с селектором locatorToWaitForDisappear */
    public void safeClickThenWaitForDisappear(By locatorToWaitForDisappear, long timeOutInSeconds) {
        performAndWaitForDisappear(
            driver,
            () -> this.safeClick(timeOutInSeconds),
            locatorToWaitForDisappear,
            timeOutInSeconds
        );
    }
}
