package org.jdiai.jsbuilder;

import com.epam.jdi.tools.func.JAction1;
import com.epam.jdi.tools.func.JFunc1;
import com.epam.jdi.tools.func.JFunc2;
import com.epam.jdi.tools.map.MapArray;
import com.epam.jdi.tools.pairs.Pair;
import org.jdiai.jsdriver.JSException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

import static com.epam.jdi.tools.PrintUtils.print;
import static com.epam.jdi.tools.ReflectionUtils.isClass;
import static com.epam.jdi.tools.StringUtils.LINE_BREAK;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.jdiai.jsbuilder.RetryFunctions.LIST_RETRY_DEFAULT;
import static org.jdiai.jsbuilder.RetryFunctions.RETRY_DEFAULT;

public class JSBuilder implements IJSBuilder {
    protected List<String> variables = new ArrayList<>();
    protected String query = "";
    protected String ctxCode = "";
    protected String replaceValue;
    protected JavascriptExecutor js;
    public static boolean LOG_QUERY = false;
    public static JFunc1<String, String> PROCESS_RESULT =
        result -> result.length() > 200  ? result.substring(0, 195) + "..." : result;
    public boolean logQuery = LOG_QUERY;
    public static JAction1<String> logger = System.out::println;
    protected MapArray<String, String> useFunctions = new MapArray<>();
    protected IBuilderActions builderActions;

    public JSBuilder() { }
    public JSBuilder(WebDriver driver) {
        this(driver, null);
    }
    public JSBuilder(WebDriver driver, IBuilderActions builderActions) {
        this.js = (JavascriptExecutor) driver;
        this.builderActions = builderActions != null
            ? builderActions
            : new BuilderActions();
        this.builderActions.setBuilder(this);
    }

    public IJSBuilder registerFunction(String name, String function) {
        useFunctions.update(name, function);
        return this;
    }
    public IJSBuilder logQuery() {
        this.logQuery = true;
        return this;
    }
    public IJSBuilder setTemplate(String replaceTo) {
        this.replaceValue = replaceTo;
        return this;
    }
    public static JFunc2<JavascriptExecutor, String, Object> RETRY = RETRY_DEFAULT;
    public Object executeQuery() {
        String jsScript = getQuery();
        if (logQuery) {
            logger.execute("Execute query:" + LINE_BREAK + jsScript);
        }
        Object result;
        try {
            result = RETRY.execute(js, jsScript);
        } finally {
            cleanup();
        }
        if (result != null && logQuery)
            logger.execute(">>> " + PROCESS_RESULT.execute(result.toString()));
        return result;
    }
    public static JFunc2<JavascriptExecutor, String, List<String>> LIST_RETRY = LIST_RETRY_DEFAULT;
    private static boolean smartStringify = true;
    public static void switchOffStringify() { smartStringify = false; }
    public List<String> executeAsList() {
        String jsScript = getQuery();
        if (logQuery)
            logger.execute("Execute query:" + LINE_BREAK + jsScript);
        List<String> result;
        try {
            result = LIST_RETRY.execute(js, jsScript);
        } finally {
            cleanup();
        }
        if (result != null && logQuery)
            logger.execute(">>> " + PROCESS_RESULT.execute(result.toString()));
        return result;
    }
    public String getQuery(String result) {
        return getQuery() + "return " + result;
    }
    public IJSBuilder addJSCode(String code) {
        query += code;
        return this;
    }
    public IJSBuilder addContextCode(String code) {
        ctxCode += code;
        return this;
    }
    public IJSBuilder oneToOne(String ctx, By locator) {
        return addJSCode(builderActions.oneToOne(ctx, locator));
    }
    public IJSBuilder listToOne(By locator) {
        return addJSCode(builderActions.listToOne(locator));
    }
    public IJSBuilder oneToList(String ctx, By locator) {
        return addJSCode(builderActions.oneToList(ctx, locator));
    }
    public IJSBuilder listToList(By locator) {
        return addJSCode(builderActions.listToList(locator));
    }
    public IJSBuilder getResult(String collectResult) {
        return addJSCode(builderActions.getResult(getCollector(collectResult)));
    }
    public IJSBuilder getResultList(String collectResult) {
        return addJSCode(builderActions.getResultList(getCollector(collectResult)));
    }
    public IJSBuilder trigger(String event) {
        return trigger(event,"'bubbles': true");
    }
    public IJSBuilder trigger(String event, String options) {
        return addJSCode("element.dispatchEvent(new Event('" + event + "', { " + options + " }));\n");
    }
    protected String getCollector(String collectResult) {
        if (smartStringify) {
            if (collectResult.contains(""))
            if (collectResult.trim().contains("return {") && collectResult.trim().contains("}")) {
                return collectResult.replace("return {", "return JSON.stringify({")
                    .replace("}", "})");
            }
            if (collectResult.trim().contains("return [") && collectResult.trim().contains("]")) {
                return collectResult.replace("return [", "return JSON.stringify([")
                    .replace("}", "})");
            }
            if (collectResult.trim().startsWith("{") && collectResult.trim().contains("}")) {
                return collectResult.replace("{", "JSON.stringify({")
                    .replace("}", "})");
            }
            if (collectResult.trim().startsWith("[") && collectResult.trim().contains("]")) {
                return collectResult.replace("[", "JSON.stringify([")
                    .replace("]", "])");
            }
        }
        return collectResult;
    }

    // region private
    public void registerVariables(String... vars) {
        for (String variable : vars) {
            if (!variables.contains(variable))
                variables.add(variable);
        }
    }
    public String registerVariable(String variable) {
        if (!variables.contains(variable))
            variables.add(variable);
        return variable + " = ";
    }
    private String beforeScript() {
        return isNotBlank(ctxCode) ? ctxCode + "\n": "";
    }
    public String rawQuery() {
        return beforeScript() + query;
    }
    public String getQuery() {
        String script = getScript();
        if (!script.contains("%s"))
            return script;
        if (replaceValue != null)
            return format(script, replaceValue);
        throw new JSException("Failed to execute js script for template without replaceValue. Use setTemplate(...) method for builder to set replaceValue");
    }
    protected String getScript() {
        if (variables.size() == 0 && useFunctions.size() == 0) {
            return query;
        }
        String jsScript = print(useFunctions.values(), "");
        String letVariables = variables.size() > 1
            ? print(variables, ", ") + ";\n"
            : "";
        return jsScript + "let " + letVariables + rawQuery();
    }
    public void cleanup() {
        useFunctions.clear();
        query = "";
        variables = new ArrayList<>();
        replaceValue = null;
        ctxCode = "";
    }
    public void updateFromBuilder(IJSBuilder builder) {
        if (!isClass(builder.getClass(), JSBuilder.class))
            return;
        JSBuilder jsBuilder = (JSBuilder) builder;
        for (Pair<String, String> pair : jsBuilder.useFunctions) {
            if (!useFunctions.has(pair.key)) {
                useFunctions.add(pair);
            }
        }
        for (String variable : jsBuilder.variables) {
            if (!variables.contains(variable)) {
                variables.add(variable);
            }
        }
    }
    public JSBuilder copy() {
        JSBuilder result = new JSBuilder();
        result.builderActions = builderActions;
        result.ctxCode = ctxCode;
        result.js = js;
        result.useFunctions = useFunctions;
        result.logQuery = logQuery;
        result.replaceValue = replaceValue;
        result.variables = variables;
        return result;
    }
    // endregion
}
