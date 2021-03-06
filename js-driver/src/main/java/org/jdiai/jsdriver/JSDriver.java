package org.jdiai.jsdriver;

import com.epam.jdi.tools.LinqUtils;
import org.jdiai.jsbuilder.*;
import org.jdiai.jsproducer.JSListProducer;
import org.jdiai.jsproducer.JSProducer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

import static com.epam.jdi.tools.LinqUtils.*;
import static org.jdiai.jsbuilder.ListSearch.CHAIN;
import static org.jdiai.jsbuilder.ListSearch.MULTI;

public class JSDriver {
    private final WebDriver driver;
    public final List<By> locators;
    private IJSBuilder builder;
    public ListSearch strategy = CHAIN;
    public String context = "document";

    private static IJSBuilder defaultBuilder(WebDriver driver) {
        return new JSBuilder(driver, new BuilderActions());
    }
    public JSDriver(WebDriver driver, By... locators) {
        this(driver, newList(locators), defaultBuilder(driver));
    }
    public JSDriver(WebDriver driver, List<By> locators) {
        this(driver, locators, defaultBuilder(driver));
    }
    public JSDriver(WebDriver driver, List<By> locators, IBuilderActions actions) {
        this(driver, locators, new JSBuilder(driver, actions));
    }
    public JSDriver(WebDriver driver, List<By> locators, IJSBuilder builder) {
        if (driver == null)
            throw new JSException("JSDriver init failed: WebDriver == null");
        this.driver = driver;
        this.locators = copyList(locators);
        this.builder = builder;
    }

    public JSDriver setBuilder(IJSBuilder builder) {
        this.builder = builder.copy();
        return this;
    }
    public JSDriver elementCtx() {
        builder().registerVariable("element");
        context = "element";
        return this;
    }

    public IJSBuilder buildOne() {
        if (locators().isEmpty()) {
            return builder();
        }
        if (locators().size() == 1) {
            return builder().oneToOne(context, firstLocator());
        }
        switch (strategy) {
            case CHAIN: return buildOneChain();
            case MULTI: return buildOneMultiSearch();
            default: return buildOneChain();
        }
    }
    public JSProducer getOne(String collector) {
        return new JSProducer(buildOne().getResult(collector).executeQuery());
    }
    public IJSBuilder buildList() {
        if (locators().isEmpty()) {
            return builder();
        }
        if (locators().size() == 1) {
            return builder().oneToList(context, firstLocator());
        }
        switch (strategy) {
            case CHAIN: return buildListChain();
            case MULTI: return buildListMultiSearch();
            default: return buildListChain();
        }
    }
    public JSListProducer getList(String collector) {
        return new JSListProducer(buildList().getResultList(collector).executeAsList());
    }
    public JSProducer getFirst(String collector) {
        return new JSProducer(buildList().getResult(collector).executeQuery());
    }
    public long getCount() {
        try {
            return (Long) buildList().addJSCode("return elements.length;").executeQuery();
        } catch (Exception ignore) { return -1; }
    }

    public IJSBuilder buildOneChain() {
        if (locators().isEmpty()) {
            return builder();
        }
        if (locators().size() == 1) {
            return buildOne();
        }
        IJSBuilder builder =  builder();
        String ctx = context;
        for (By locator : locators()) {
            builder.oneToOne(ctx, locator);
            ctx = "element";
        }
        return builder;
    }
    public JSProducer getOneChain(String collector) {
        return new JSProducer(buildOneChain().getResult(collector).executeQuery());
    }
    public IJSBuilder buildOneMultiSearch() {
        if (locators().isEmpty()) {
            return builder();
        }
        if (locators().size() == 1) {
            return buildOne();
        }
        builder().oneToList(context, firstLocator());
        for (By locator : listCopy(locators(), 1, -1)) {
            builder.listToList(locator);
        }
        builder().listToOne(lastLocator());
        return builder();
    }
    public JSProducer getOneMultiSearch(String collector) {
        return new JSProducer(buildOneMultiSearch().getResult(collector).executeQuery());
    }
    public IJSBuilder buildListChain() {
        if (locators().size() == 1) {
            return buildList();
        }
        String ctx = context;
        for (By locator : listCopyUntil(locators(), -1)) {
            builder().oneToOne(ctx, locator);
            ctx = "element";
        }
        builder().oneToList("element", lastLocator());
        return builder();
    }
    public JSListProducer getListChain(String collector) {
        return new JSListProducer(buildListChain().getResultList(collector).executeAsList());
    }
    public IJSBuilder buildListMultiSearch() {
        if (locators().size() == 1) {
            return buildList();
        }
        builder().oneToList(context, firstLocator());
        if (locators().size() > 2) {
            for (By locator : listCopy(locators(), 1, -1)) {
                builder().listToList(locator);
            }
        }
        builder().listToList(lastLocator());
        return builder();
    }
    public JSListProducer getListMultiSearch(String collector) {
        return new JSListProducer(buildListMultiSearch().getResultList(collector).executeAsList());
    }
    
    public JSDriver multiSearch() {
        strategy = MULTI;
        return this;
    }
    public WebDriver driver() {
        return this.driver;
    }
    public IJSBuilder builder() {
        if (builder == null)
            builder = new JSBuilder(driver());
        return builder;
    }
    public List<By> locators() {
        return this.locators;
    }
    public By firstLocator() {
        return locators.get(0);
    }
    public By lastLocator() {
        return LinqUtils.last(locators);
    }

    public void invoke(String action) {
        getOne("element." + action).asString();
    }
}
