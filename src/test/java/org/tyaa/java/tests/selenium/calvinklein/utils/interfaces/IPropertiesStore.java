package org.tyaa.java.tests.selenium.calvinklein.utils.interfaces;

import java.util.Map;

public interface IPropertiesStore {
    Map<String, String> getSupportedBrowsers();
    Map.Entry<String, String> getDefaultBrowser() throws Exception;
    String getOs();
    Integer getImplicitlyWaitSeconds();
    String getProdUrl();
    String getLiveUrl();
    String getProdUrl2();
    String getLiveUrl2();
}
