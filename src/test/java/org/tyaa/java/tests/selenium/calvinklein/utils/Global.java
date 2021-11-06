package org.tyaa.java.tests.selenium.calvinklein.utils;

import org.tyaa.java.tests.selenium.calvinklein.utils.interfaces.IPropertiesStore;

public class Global {
    public static final IPropertiesStore properties = new FilePropertiesStore();
}
