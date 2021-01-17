package org.jdiai.jswrap;

import com.epam.jdi.tools.Safe;
import org.jdiai.jsbuilder.JSBuilder;
import org.jdiai.jsbuilder.SmartBuilderActions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.jdiai.WebDriverByUtils.defineLocator;
import static org.jdiai.WebDriverByUtils.locatorsToBy;

public class JSWrapper {
    public static Safe<WebDriver> DRIVER = new Safe<>(JSWrapper::chromeDriver);

    public static JSSmart $(String locator) {
        return new JSSmart(DRIVER.get(), defineLocator(locator));
    }
    public static JSSmart $(String... locators) {
        return new JSSmart(DRIVER.get(), locatorsToBy(locators));
    }
    public static <T> JSEntity<T> $(Class<T> cl, String locator) {
        JSEntity<T> entity = new JSEntity<T>(DRIVER.get(), defineLocator(locator)).initClass(cl);
        entity.driver.setBuilder(new JSBuilder(DRIVER.get(), new SmartBuilderActions()));
        return entity;
    }
    public static <T> JSEntity<T> $(Class<T> cl, String... locators) {
        JSEntity<T> entity = new JSEntity<T>(DRIVER.get(), locatorsToBy(locators)).initClass(cl);
        entity.driver.setBuilder(new JSBuilder(DRIVER.get(), new SmartBuilderActions()));
        return entity;
    }
    public static JSElement element(String locator) {
        return new JSElement(DRIVER.get(), defineLocator(locator));
    }
    public static JSElement element(String... locators) {
        return new JSElement(DRIVER.get(), locatorsToBy(locators));
    }
    public static <T> JSEntity<T> element(Class<T> cl, String locator) {
        return new JSEntity<T>(DRIVER.get(), defineLocator(locator)).initClass(cl);
    }
    public static <T> JSEntity<T> element(Class<T> cl, String... locators) {
        return new JSEntity<T>(DRIVER.get(), locatorsToBy(locators)).initClass(cl);
    }
    public static JSJson json(String locator) {
        return new JSJson(DRIVER.get(), defineLocator(locator));
    }
    public static JSJson json(String... locators) {
        return new JSJson(DRIVER.get(), locatorsToBy(locators));
    }

    public static WebDriver chromeDriver() {
        System.setProperty("webdriver.chrome.driver", "/Users/romaniovlev/Downloads/chromedriver");
        // System.setProperty("webdriver.chrome.driver", "C:\\Selenium\\chromedriver.exe");
        return new ChromeDriver();
    }
    public static WebDriver firefoxDriver() {
        System.setProperty("webdriver.gecko.driver", "C:\\Selenium\\geckodriver.exe");
        // System.setProperty("webdriver.gecko.driver", "C:\\Selenium\\geckodriver.exe");
        return new FirefoxDriver();
    }
}