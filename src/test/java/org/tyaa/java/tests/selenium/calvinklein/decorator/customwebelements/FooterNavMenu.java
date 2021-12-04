package org.tyaa.java.tests.selenium.calvinklein.decorator.customwebelements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.stream.Stream;

/* Оболочка для веб-элементов типа "Меню навигации" */
public class FooterNavMenu extends BaseElement {
    public FooterNavMenu(WebDriver driver, WebElement element) {
        super(driver, element);
    }
    public Stream<NavMenuLink> getLinks() {
        return element.findElements(
            By.cssSelector(
                "ul.footer__list > li > a"
            )
        ).stream()
            .map(el -> new NavMenuLink(driver, el));
    }
}
