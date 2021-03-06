package org.jdiai.tools;

import org.jdiai.annotations.UI;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import static java.lang.String.format;
import static org.jdiai.jswraper.JSWrappersUtils.defineLocator;
import static org.openqa.selenium.support.How.*;
import static org.openqa.selenium.support.ui.Quotes.escape;

/**
 * Created by Roman Iovlev on 26.09.2019
 * Email: roman.iovlev.jdi@gmail.com; Skype: roman.iovlev
 */
public final class JSTalkUtils {

     public static By uiToBy(UI locator) {
         if (locator == null) return null;
         if (!locator.value().isEmpty()) {
             return defineLocator(locator.value());
         }
         if (!locator.id().isEmpty())
             return By.id(locator.id());
         if (!locator.clazz().isEmpty())
             return By.className(locator.clazz());
         if (!locator.xpath().isEmpty())
             return By.xpath(locator.xpath());
         if (!locator.css().isEmpty())
             return By.cssSelector(locator.css());
         if (!locator.text().isEmpty())
             return By.xpath(format(".//*/text()[normalize-space(.) = %s]/parent::*", escape(locator.text())));
         if (!locator.hasText().isEmpty())
             return By.xpath(format(".//*/text()[contains(normalize-space(.), %s)]/parent::*", escape(locator.hasText())));
         if (!locator.tag().isEmpty())
             return By.tagName(locator.tag());
         if (!locator.label().isEmpty())
             return By.cssSelector("[label=" + locator.label() + "]");
         if (!locator.alt().isEmpty())
             return By.cssSelector("[alt=" + locator.alt() + "]");
         if (!locator.hasValue().isEmpty())
             return By.cssSelector("[value=" + locator.hasValue() + "]");
         return null;
     }
     public static By findByToBy(FindBy locator) {
         if (locator == null) return null;
         if (!locator.id().isEmpty())
             return By.id(locator.id());
         if (!locator.className().isEmpty())
             return By.className(locator.className());
         if (!locator.xpath().isEmpty())
             return By.xpath(locator.xpath());
         if (!locator.css().isEmpty())
             return By.cssSelector(locator.css());
         if (!locator.linkText().isEmpty())
             return By.linkText(locator.linkText());
         if (!locator.name().isEmpty())
             return By.name(locator.name());
         if (!locator.partialLinkText().isEmpty())
             return By.partialLinkText(locator.partialLinkText());
         if (!locator.tagName().isEmpty())
             return By.tagName(locator.tagName());
         if (locator.how() != UNSET)
             return getHowLocator(locator);
         return null;
     }
     private static By getHowLocator(FindBy locator) {
         if (locator.how() == ID)
             return By.id(locator.using());
         if (locator.how() == CLASS_NAME)
             return By.className(locator.using());
         if (locator.how() == XPATH)
             return By.xpath(locator.using());
         if (locator.how() == CSS)
             return By.cssSelector(locator.using());
         if (locator.how() == LINK_TEXT)
             return By.linkText(locator.using());
         if (locator.how() == NAME)
             return By.name(locator.using());
         if (locator.how() == PARTIAL_LINK_TEXT)
             return By.partialLinkText(locator.using());
         if (locator.how() == TAG_NAME)
             return By.tagName(locator.using());
         return null;
     }
}
