package org.tyaa.java.tests.selenium.calvinklein.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.tyaa.java.tests.selenium.calvinklein.decorator.CustomWebElementFieldDecorator;
import org.tyaa.java.tests.selenium.calvinklein.decorator.customwebelements.BaseElement;
import org.tyaa.java.tests.selenium.calvinklein.decorator.customwebelements.Button;
import org.tyaa.java.tests.selenium.calvinklein.utils.Global;

import java.lang.reflect.InvocationTargetException;

import static java.lang.Thread.sleep;

/* Основная модель страницы, которая подходит под любую страницу сайта */
public class BasePage {

    protected WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(
            webDriver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState")
                .equals("complete")
        );
        PageFactory.initElements(new CustomWebElementFieldDecorator(driver), this);
    }

    public BasePage clickAgreeButton() throws IllegalAccessException, InvocationTargetException, InstantiationException {
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

            /* BaseElement.moveToElementAndWaitForUpdate(
                driver,
                driver.findElement(By.xpath("//body")),
                By.xpath("//a[contains(@class,'cta-white-transparent-inverts-black-color-white-bg')]"),
                3
            ); */
        } catch (NoSuchElementException | InterruptedException ignored) {}
        return new BasePage(driver);// this.getClass().getDeclaredConstructors()[0].newInstance(driver);
    }
}
