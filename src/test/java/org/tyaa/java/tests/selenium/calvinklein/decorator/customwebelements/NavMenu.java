package org.tyaa.java.tests.selenium.calvinklein.decorator.customwebelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.stream.Stream;

/* Оболочка для веб-элементов типа "Меню навигации" */
public class NavMenu extends BaseElement {
    public NavMenu(WebDriver driver, WebElement element) {
        super(driver, element);
    }
    public Stream<NavMenuLink> getLinks() {
        return this.element.findElements(By.xpath("/li/a")).stream()
            .map(el -> new NavMenuLink(driver, el));
    }
}
