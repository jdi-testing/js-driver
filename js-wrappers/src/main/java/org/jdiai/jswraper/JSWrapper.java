package org.jdiai.jswraper;

import com.epam.jdi.tools.Safe;
import org.jdiai.jsbuilder.JSBuilder;
import org.jdiai.jsbuilder.SmartBuilderActions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.jdiai.jswraper.JSWrappersUtils.NAME_TO_LOCATOR;
import static org.jdiai.jswraper.JSWrappersUtils.locatorsToBy;

public class JSWrapper {
    public static Safe<WebDriver> DRIVER = new Safe<>(JSWrapper::chromeDriver);
    public static WebDriver driver() {
        return DRIVER.get();
    }

    public static JSSmart $(String locator) {
        return new JSSmart(driver(), NAME_TO_LOCATOR.execute(locator));
    }
    public static JSSmart $(String... locators) {
        return new JSSmart(driver(), locatorsToBy(locators));
    }
    public static <T> JSEntity<T> $(Class<T> cl, String locator) {
        JSEntity<T> entity = new JSEntity<T>(driver(), NAME_TO_LOCATOR.execute(locator)).initClass(cl);
        entity.driver.setBuilder(new JSBuilder(driver(), new SmartBuilderActions()));
        return entity;
    }
    public static <T> JSEntity<T> $(Class<T> cl, String... locators) {
        JSEntity<T> entity = new JSEntity<T>(driver(), locatorsToBy(locators)).initClass(cl);
        entity.driver.setBuilder(new JSBuilder(driver(), new SmartBuilderActions()));
        return entity;
    }
    public static JSElement element(String locator) {
        return new JSElement(driver(), NAME_TO_LOCATOR.execute(locator));
    }
    public static JSElement element(String... locators) {
        return new JSElement(driver(), locatorsToBy(locators));
    }
    public static <T> JSEntity<T> element(Class<T> cl, String locator) {
        return new JSEntity<T>(driver(), NAME_TO_LOCATOR.execute(locator)).initClass(cl);
    }
    public static <T> JSEntity<T> element(Class<T> cl, String... locators) {
        return new JSEntity<T>(driver(), locatorsToBy(locators)).initClass(cl);
    }
    public static JSJson json(String locator) {
        return new JSJson(driver(), NAME_TO_LOCATOR.execute(locator));
    }
    public static JSJson json(String... locators) {
        return new JSJson(driver(), locatorsToBy(locators));
    }

    public static WebDriver chromeDriver() {
        System.setProperty("webdriver.chrome.driver", "/Users/romaniovlev/Downloads/chromedriver");
        // System.setProperty("webdriver.chrome.driver", "C:\\Selenium\\chromedriver.exe");
        return new ChromeDriver();
    }
    public static WebDriver firefoxDriver() {
        System.setProperty("webdriver.gecko.driver", "~/Downloads/chromedriver");
        // System.setProperty("webdriver.gecko.driver", "C:\\Selenium\\geckodriver.exe");
        return new FirefoxDriver();
    }
}
