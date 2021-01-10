package org.jdiai.tools;

import com.epam.jdi.tools.Safe;
import org.jdiai.JS;
import org.jdiai.jswrap.JSSmart;
import org.jdiai.jswrap.JSWrapper;
import org.openqa.selenium.WebDriver;

import static org.jdiai.WebDriverByUtils.defineLocator;
import static org.jdiai.WebDriverByUtils.locatorsToBy;

public class JSTalk {
    public static Safe<WebDriver> DRIVER = new Safe<>(JSWrapper::chromeDriver);
    public static WebDriver driver() {
        return DRIVER.get();
    }
    public static void openPage(String url) {
        driver().get(url);
    }
    public static JS $(String locator) {
        return new JS(driver(), defineLocator(locator));
    }
    public static JS $(String... locators) {
        return new JS(driver(), locatorsToBy(locators));
    }
    public static JSSmart jsDriver() { return new JSSmart(driver()); }
}
