package org.jdiai;

import com.epam.jdi.tools.Safe;
import org.jdiai.jsdriver.JSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import static java.lang.Runtime.getRuntime;
import static java.util.Arrays.stream;
import static org.jdiai.LocatorUtils.defineLocator;
import static org.jdiai.Pages.*;
import static org.jdiai.jsbuilder.JSBuilder.LOG_QUERY;

public interface TestInit {
    default JSDriver js(String locator) {
        return new JSDriver(driver(), defineLocator(locator));
    }
    default JSDriver js(String... locators) {
        By[] list = stream(locators).map(LocatorUtils::defineLocator).toArray(By[]::new);
        return new JSDriver(driver(), list);
    }

    default String[] withParent(String locator) {
        return new String[] {".uui-header", ".profile-photo", locator };
    }
    default String[] inForm(String locator) {
        return new String[] {".uui-header", "form", locator };
    }

    @BeforeSuite(alwaysRun = true)
    default void setUp() {
        killDrivers();
        initDriver();
        openSite();
        LOG_QUERY = true;
    }

    @AfterSuite(alwaysRun = true)
    default void tearDown() {
        killDrivers();
    }

    default void initDriver() {
        DRIVER = new Safe<>(() -> {
            WebDriver driver = chromeDriver();
            driver.get(HOME_PAGE);
            driver.manage().window().maximize();
            return driver;
        });
    }
    default void killDrivers() {
        try {
            getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
        } catch (Exception ignore) { }
    }
}
