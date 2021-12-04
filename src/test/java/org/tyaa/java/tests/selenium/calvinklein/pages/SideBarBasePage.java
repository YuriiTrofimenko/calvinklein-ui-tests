package org.tyaa.java.tests.selenium.calvinklein.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SideBarBasePage extends BasePage {

    @FindBy(className = "sidebar")
    private WebElement sidebar;

    public SideBarBasePage(WebDriver driver) {
        super(driver);
    }

    public WebElement getSidebar() {
        return sidebar;
    }
}
