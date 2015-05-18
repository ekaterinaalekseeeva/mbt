import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ekaterina.Alekseeva on 11-Mar-15.
 */
public class Parser {
    private static String baseURL;

    private static WebDriver driver = new FirefoxDriver();
//    private static WebDriver driver;

    private static Pages pages = new Pages();


//    TODO: think about adding other elements (div?)
    private static String[] clickableElements = {
            "a",
//            "a[href]",
            "button",
            "span.checkbox",
            "span[class*='toggle']",
            "span[class*='btn']",
            "td[class*='tabpanel-item']",
            "input[type='checkbox']",
            "ul.comboboxList > li"};
    private static String[] writableElements = {
//        TODO: write selectors for input properly
            "input",
//            "input button",
            "textarea"};

//    public static ArrayList<WebElement> foundWebElements = new ArrayList<WebElement>();
    public static ArrayList<Element> foundElements = new ArrayList<Element>();
    public static ArrayList<String> ignoredSelectors = new ArrayList<String>();
    public static ArrayList<String> selectors = new ArrayList<String>();

    public static int graphCounter = 0;
    public static int URLCounter;

//    Generate unique selector for given element
    public static String createSelector(WebElement element, String selectorType){
        StringBuilder curSel = new StringBuilder();
        String id = element.getAttribute("id");
        String title = element.getAttribute("title");
        String class_ = element.getAttribute("class");
        String cn = element.getAttribute("cn");
        String name = element.getAttribute("name");
        String value = element.getAttribute("value");
//        String type = element.getAttribute("type");
        int counter = 0;

        if (selectorType.equals(Constants.xpath_selector)){
            curSel.append("//");
        }
        curSel.append(element.getTagName());

//        If element has id, it is a ready unique selector
        if (id != null && !id.equals("")){
            if (selectorType.equals(Constants.xpath_selector)){
                curSel.append("[@id='");
                curSel.append(id);
                curSel.append("']");
            } else {
                curSel.append("[id='");
                curSel.append(id);
                curSel.append("']");
            }
//            else try to find more attributes
        } else {
            if (title != null && !title.equals("")){
                counter++;
                boolean part = false;
                if (element.getTagName().equals("a") && class_.contains("attribute")){
                    title = title.split(":")[0];
                    System.out.println(title);
                    part = true;
                }
                if (selectorType.equals(Constants.xpath_selector)){
                    if (counter == 1){
                        curSel.append("[");
                    } else{
                        curSel.append(" and ");
                    }
                    if (part){
                        curSel.append("contains(@title, '");
                        curSel.append(title);
                        curSel.append("')");
                    } else {
                        curSel.append("@title='");
                        curSel.append(title);
                        curSel.append("'");
                    }

                } else {
                    if (part){
                        curSel.append("[title*='");
                        curSel.append(title);
                        curSel.append("']");
                    } else {
                        curSel.append("[title='");
                        curSel.append(title);
                        curSel.append("']");
                    }
                }
            }

//            class often contains several values, so we take first of them
            if (class_ != null && !class_.equals("")){
                counter++;
                String[] c = class_.split(" ");
                for (String i : c){
                    if (!i.equals("")){
                        class_ = i;
                        break;
                    }
                }
//                System.out.println("Class: " + class_);
                if (selectorType.equals(Constants.xpath_selector)){
                    if (counter == 1){
                        curSel.append("[");
                    } else{
                        curSel.append(" and ");
                    }
                    curSel.append("contains(@class, '");
                    curSel.append(class_);
                    curSel.append("')");
                } else {
                    curSel.append("[class*='");
                    curSel.append(class_);
                    curSel.append("']");
                }
            }

            if (cn != null && !cn.equals("")){
                counter++;
                if (selectorType.equals(Constants.xpath_selector)){
                    if (counter == 1){
                        curSel.append("[");
                    } else{
                        curSel.append(" and ");
                    }
                    curSel.append("@cn='");
                    curSel.append(cn);
                    curSel.append("'");
                } else {
                    curSel.append("[cn='");
                    curSel.append(cn);
                    curSel.append("']");
                }
            }

            if (name != null && !name.equals("")){
                counter++;
                if (selectorType.equals(Constants.xpath_selector)){
                    if (counter == 1){
                        curSel.append("[");
                    } else{
                        curSel.append(" and ");
                    }
                    curSel.append("@name='");
                    curSel.append(name);
                    curSel.append("'");
                } else {
                    curSel.append("[name='");
                    curSel.append(name);
                    curSel.append("']");
                }
            }

//            if anchor element doesn't have any attributes except href, try to get it by href or pathname
            if (element.getTagName().equals("a") && counter == 0) {
                String href = element.getAttribute("href");
                String pathname = element.getAttribute("pathname");
                if (href != null && !href.equals("")) {
                    counter++;
                    if (selectorType.equals(Constants.xpath_selector)) {
                        if (counter == 1) {
                            curSel.append("[");
                        } else {
                            curSel.append(" and ");
                        }
                        curSel.append("@href='");
                        curSel.append(pathname);
                        curSel.append("' or @href='");
                        curSel.append(href);
                        curSel.append("'");
                    } else {
                        curSel.append("[href='");
                        curSel.append(pathname);
                        curSel.append("'],[href='");
                        curSel.append(href);
                        curSel.append("']");
                    }
                }
            }

            if (value != null && !value.equals("") && !element.getTagName().equals("li") && !element.getTagName().equals("input")){
                counter++;
                if (selectorType.equals(Constants.xpath_selector)){
                    if (counter == 1){
                        curSel.append("[");
                    } else{
                        curSel.append(" and ");
                    }
                    curSel.append("@value='");
                    curSel.append(value);
                    curSel.append("']");
                } else {
                    curSel.append("[value='");
                    curSel.append(value);
                    curSel.append("']");
                }
//            }


//
//            if (type != null && !type.equals("") && !type.equals("text")){
//                counter++;
////                System.out.println("Type: " + type);
//                if (selectorType.equals(Constants.xpath_selector)){
//                    if (counter == 1){
//                        curSel.append("[");
//                    } else{
//                        curSel.append(" and ");
//                    }
//                    curSel.append("@type='");
//                    curSel.append(type);
//                    curSel.append("']");
//                } else {
//                    curSel.append("[type='");
//                    curSel.append(type);
//                    curSel.append("']");
//                }
            } else {
                if (selectorType.equals(Constants.xpath_selector)){
                    curSel.append("]");
                }
            }
        }
        return curSel.toString();
    }

//    Checks page in current state, seeks for necessary elements, adds new appeared elements
    public static void parsingElements(String selector, String action, Element parent, boolean ignored, boolean terminal, boolean specialCond, boolean condTerminal, SpecialConditionsElement spCondEl){
        List<WebElement> elems = driver.findElements(By.cssSelector(selector));
        for (WebElement elem : elems) {
            System.out.println(selector);
//            if (!foundWebElements.contains(elem)) {
            if (elem.isDisplayed()){
//                foundWebElements.add(elem);
                String xpathSelector = createSelector(elem, Constants.xpath_selector);
                WebElement par = driver.findElement(By.xpath(xpathSelector + "/.."));
                WebElement parpar = driver.findElement(By.xpath(xpathSelector + "/../.."));
                String parSelector = createSelector(par, Constants.css_selector);
                String parparSelector = createSelector(parpar, Constants.css_selector);
                String cssSelector = createSelector(elem, Constants.css_selector);
                System.out.println(parparSelector + " " + parSelector + " " + cssSelector);
                if (ignored && !ignoredSelectors.contains(parparSelector + " " + parSelector + " " + cssSelector)) {
                    ignoredSelectors.add(parparSelector + " " + parSelector + " " + cssSelector);
                } else
                    if (!selectors.contains(parparSelector + " " + parSelector + " " + cssSelector) && !(parparSelector + " " + parSelector + " " + cssSelector).equals("div[class*='yt-attach-file-dialog__permitted-group-fieldset'] div[class*='combobox'] a[class*='arrow']")) {
                        Element tmpEl = new Element();
                        tmpEl.setElement(elem);
                        tmpEl.setTerminal(terminal);
                        tmpEl.setSpecialCond(specialCond);
                        if (specialCond){
                            tmpEl.setSpCondEl(spCondEl);
                        }
                        tmpEl.setCondTerminal(condTerminal);
                        tmpEl.setSelector(parparSelector + " " + parSelector + " " + cssSelector);
                        tmpEl.setNumber(elems.indexOf(elem));
                        tmpEl.setAction(action);
                        tmpEl.setParent(parent);
                        foundElements.add(tmpEl);
                        selectors.add(parparSelector + " " + parSelector + " " + cssSelector);
                        System.out.println("added");
                    }
            }
            System.out.println();
        }
    }

// Calls parsing method for different types of elements
    public static void parsingPage(String pageName, Element parent, String area, ArrayList<SpecialConditionsElement.AllowedSelector> selectors){
//        System.out.println("parsingPage entered!");
        ArrayList<String> terminal = new ArrayList<String>();
        ArrayList<String> ignored = new ArrayList<String>();
        ArrayList<SpecialConditionsElement> specialCond = new ArrayList<SpecialConditionsElement>();
        for (Pages.Page p : pages.pagesList){
            if (p.name.equals(pageName)){
                terminal = p.terminalElementsSelectors;
                ignored = p.ignoredElementsSelectors;
                specialCond = p.specialConditionsElements;
            }
        }

        String selector;

        for (String s: ignored){
            if (area == null){
                selector = s;
            } else {
                selector = area + " " + s;
            }

//            System.out.println(selector);
            parsingElements(selector, Constants.action_click, parent, true, false, false, false, null);
        }

        for (SpecialConditionsElement el: specialCond){
            if (area == null){
                selector = el.selector;
            } else {
                selector = area + " " + el.selector;
            }

//            System.out.println(selector);
            parsingElements(selector, Constants.action_click, parent, false, false, true, false, el);
        }

        for (String s: terminal){
            if (area == null){
                selector = s;
            } else {
                selector = area + " " + s;
            }

//            System.out.println(selector);
            parsingElements(selector, Constants.action_click, parent, false, true, false, false, null);
        }

        if (selectors == null) {
            for (String s : clickableElements) {
                if (area == null){
                    selector = s;
                } else {
                    selector = area + " " + s;
                }
                parsingElements(selector, Constants.action_click, parent, false, false, false, false, null);
            }

            for (String s : writableElements) {
                if (area == null){
                    selector = s;
                } else {
                    selector = area + " " + s;
                }
                parsingElements(selector, Constants.action_write, parent, false, false, false, false, null);
            }
        } else {
            for (SpecialConditionsElement.AllowedSelector s : selectors){
                if (s.action.equals(Constants.action_click)) {
                    parsingElements(s.selector, Constants.action_click, parent, false, false, false, false, null);
                } else if (s.action.equals(Constants.action_write)) {
                    parsingElements(s.selector, Constants.action_write, parent, false, false, false, false, null);
                }
            }
        }
    }

    public static WebElement getElement(Element e){
//        System.out.println(e);
        try{
            e.getElement().getTagName();
            return e.getElement();
        } catch (StaleElementReferenceException exception){
//            List<WebElement> elems = driver.findElements(By.cssSelector(e.getSelector()));
//            return elems.get(e.getNumber());
            return  driver.findElement(By.cssSelector(e.getSelector()));
        }
    }

    public static ArrayList<Element> elementLineage(Element element){
        Element nextParent = element.getParent();
        ArrayList <Element> lineage = new ArrayList<Element>();
        while (nextParent != null){
            lineage.add(nextParent);
            nextParent = nextParent.getParent();
        }
        return lineage;
    }

    public static void reproduceElementAppearance(Element element){
        ArrayList <Element> lineage = elementLineage(element);

        System.out.println("Element lineage:");
        for (Element e : lineage){
            System.out.println(e);
        }

        Element elem;
        for (int i = lineage.size()-1; i >= 0; i--){
            elem = lineage.get(i);
            if (elem.getAction().equals(Constants.action_click)){
                getElement(elem).click();
            } else {
                //todo: m.b. random text generation?
                getElement(elem).sendKeys("sdfihsdifhsid234211@#!#$@^\\%@^*&!||&&//?,.`~");
            }
            //todo: wait until page is updated
            try {
                Thread.sleep(2000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void drawGraph(){
        System.out.println("Drawing graph " + graphCounter);
        URLCounter = 0;
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("GUIgraph"+graphCounter));

            out.write("graph GUIgraph" + graphCounter + " {\n");
            StringBuilder curStr = new StringBuilder();
            String resStr;
            for (Element e: foundElements) {
                curStr.delete(0, curStr.length());
                curStr.append(e);
                curStr.append(" [label=\"");
//                curStr.append(getElement(e).getTagName());
//                curStr.append(" ");
//                curStr.append(getElement(e).getText());
//                curStr.append(" ");
//                curStr.append(getElement(e).getAttribute("id"));
//                curStr.append(" ");
//                curStr.append(getElement(e).getAttribute("title"));
//                curStr.append(getElement(e).getAttribute("class"));
//                curStr.append(getElement(e).getAttribute("cn"));
                curStr.append(e.getSelector());
                curStr.append("\"];");
                resStr = curStr.toString();
                resStr = resStr.replaceAll("\n", "");
                resStr = resStr.replaceAll("@", "");
                out.write(resStr + "\n");
                if (e.getParent() != null) {
                    curStr.delete(0, curStr.length());
                    curStr.append(e);
                    for (Element lin : elementLineage(e)) {
                        curStr.append(" -- ");
                        curStr.append(lin);
                    }
                    curStr.append(";");
                    resStr = curStr.toString();
                    resStr = resStr.replaceAll("@", "");
                    out.write(resStr + "\n");
                }
                if (!e.getUrl().equals("")){
                    curStr.delete(0, curStr.length());
                    curStr.append("URL");
                    curStr.append(URLCounter);
                    curStr.append(" [label=\"");
                    curStr.append(e.getUrl());
                    curStr.append("\" shape=box];\n");
                    curStr.append("URL");
                    curStr.append(URLCounter);
                    curStr.append(" -- ");
                    curStr.append(e);
                    curStr.append(";");
                    resStr = curStr.toString();
                    resStr = resStr.replaceAll("@", "");
                    out.write(resStr + "\n");
                    URLCounter++;
                }
            }
            out.write("}");

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        graphCounter++;
    }

    public static void process (String pageName, Element parent, String area, ArrayList<SpecialConditionsElement.AllowedSelector> selectors) {
        int numberOfElements = foundElements.size();

        parsingPage(pageName, parent, area, selectors);

//        If no new elements were found, exit function
        System.out.println("old size = " + numberOfElements + " new size = " + foundElements.size());
        if (foundElements.size() == numberOfElements){
            return;
        }
        drawGraph();

//        for (Element elem : foundElements) {
        int firstIndex = (numberOfElements == 0) ? 0 : numberOfElements-1;
        for (int i = firstIndex; i < foundElements.size(); i++) {
            // Logging
            Element elem = foundElements.get(i);
            System.out.println("---------------------------------------------------------------------");
            System.out.println("New parsing:");
            System.out.println(elem.getSelector());
//            if (elem.getParent() == null) {
//                System.out.print(elem + " " + getElement(elem).getTagName() + " " + getElement(elem).getAttribute("class") + " ");
//                System.out.println(getElement(elem).getAttribute("id") + " Is terminal: " + elem.getTerminal() + " Is displayed: " + getElement(elem).isDisplayed());
//            }

            try {
                if (elem.getParent() != null && !getElement(elem).isDisplayed()) {
                    driver.navigate().refresh();
                    reproduceElementAppearance(elem);
                }
            } catch (NoSuchElementException e){
                if (elem.getParent() != null) {
                    driver.navigate().refresh();
                    reproduceElementAppearance(elem);
                }
            }

            System.out.println(" Is displayed: " + getElement(elem).isDisplayed());

            // Processing of non-terminal common elements
            if (!elem.getTerminal() && !elem.getCondTerminal() && !elem.getSpecialCond() && elem != parent) {
                System.out.println("------------- common");
                System.out.println(elem.getSelector());
//                try {
//                    System.out.println(" Is displayed: " + getElement(elem).isDisplayed());
//                    if (elem.getParent() != null && !getElement(elem).isDisplayed()) {
//                        driver.navigate().refresh();
//                        reproduceElementAppearance(elem);
//                    }
//                } catch (NoSuchElementException e){
//                    if (elem.getParent() != null) {
//                        driver.navigate().refresh();
//                        reproduceElementAppearance(elem);
//                    }
//                }
//
//                System.out.println(" Is displayed: " + getElement(elem).isDisplayed());

                if (elem.getAction().equals(Constants.action_click)) {
                    getElement(elem).click();
                } else {
                    //todo: m.b. random text generation?
                    getElement(elem).sendKeys("sdfihsdifhsid234211@#!#$@^\\%@^*&!||&&//?,.`~");
                }
                //todo: wait until page is updated
                try {
                    Thread.sleep(1500);                 //1000 milliseconds is one second.
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
//                System.out.println("Enter recursion");
                process(pageName, elem, null, null);
//                System.out.println("Exit recursion");
                driver.navigate().refresh();
                drawGraph();
            }

//          processing non-terminal special-conditions elements
            else if (elem.getSpecialCond() && !elem.getTerminal() && !elem.getCondTerminal() && elem != parent) {
                System.out.println("------------- special-condition");
                System.out.println(elem.getSelector());
//                try {
//                    if (elem.getParent() != null && !getElement(elem).isDisplayed()) {
//                        driver.navigate().refresh();
//                        reproduceElementAppearance(elem);
//                    }
//                } catch (NoSuchElementException e){
//                    if (elem.getParent() != null) {
//                        driver.navigate().refresh();
//                        reproduceElementAppearance(elem);
//                    }
//                }

                if (elem.getSpCondEl().type.equals(Constants.type_search_area)){
                    getElement(elem).click();

                    //todo: wait until page is updated
                    try {
                        Thread.sleep(1500);                 //1000 milliseconds is one second.
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }

//                    System.out.println("Enter recursion");
                    process(pageName, elem, elem.getSpCondEl().area, null);
//                    System.out.println("Exit recursion");

                } else if (elem.getSpCondEl().type.equals(Constants.type_search_elements)){
                    getElement(elem).click();

                    //todo: wait until page is updated
                    try {
                        Thread.sleep(1500);                 //1000 milliseconds is one second.
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }

//                    System.out.println("Enter recursion");
                    process(pageName, elem, null, elem.getSpCondEl().allowedSelectors);
//                    System.out.println("Exit recursion");

                } else if (elem.getSpCondEl().type.equals(Constants.type_write)){
//                    todo: random choose index from array
                    getElement(elem).sendKeys(elem.getSpCondEl().allowedWrite.get(0));

                } else if (elem.getSpCondEl().type.equals(Constants.type_alert_accept)) {
                    try {
                        getElement(elem).click();
                    } catch (UnhandledAlertException e){
                        Alert alert = driver.switchTo().alert();
                        alert.accept();
                    }

                    process(pageName, elem, null, null);

                } else if (elem.getSpCondEl().type.equals(Constants.type_alert_decline)) {
                    try {
                        getElement(elem).click();
                    } catch (UnhandledAlertException e){
                        Alert alert = driver.switchTo().alert();
                        alert.dismiss();
                    }

                    process(pageName, elem, null, null);

                } else {
                    System.out.println("Unknown type");
                }

                driver.navigate().refresh();
                drawGraph();
            }

            // Processing terminal elements
            else if (!elem.getSpecialCond() && (elem.getTerminal() || elem.getCondTerminal()) && elem != parent){
                System.out.println("------------- terminal");
                System.out.println(elem.getSelector());
//                try {
//                    if (elem.getParent() != null && !getElement(elem).isDisplayed()) {
//                        driver.navigate().refresh();
//                        reproduceElementAppearance(elem);
//                    }
//                } catch (NoSuchElementException e){
//                    if (elem.getParent() != null) {
//                        driver.navigate().refresh();
//                        reproduceElementAppearance(elem);
//                    }
//                }
                if (elem.getCondTerminal()){
//                    todo: do conditions
                }
//                if (elem.getAction().equals(Constants.action_click)) {
                getElement(elem).click();
//                }

                //todo: wait until page is updated
                try {
                    Thread.sleep(5000);                 //1000 milliseconds is one second.
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

                String url = driver.getCurrentUrl();
                System.out.println("URL" + url);
                if (!url.equals(baseURL)) {
                    elem.setUrl(url);
                    driver.get(baseURL);
                }

                //todo: wait until page is updated
                try {
                    Thread.sleep(5000);                 //1000 milliseconds is one second.
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

                drawGraph();
            }
        }
    }

    public static void login(){
        driver.get("http://unit-530.labs.intellij.net:8080/hub/auth/login");
        try {
            Thread.sleep(1500);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        driver.findElement(By.cssSelector("input#username")).sendKeys("root");
        driver.findElement(By.cssSelector("input#password")).sendKeys("root");
        driver.findElement(By.cssSelector("button.login-button")).click();
    }

    public static void main(String[] args) {
//        System.setProperty("webdriver.chrome.driver", "C:\\SeleniumWD\\chromedriver\\chromedriver.exe");
//        driver =  new ChromeDriver();
        String pageName = "FSI";
        baseURL = "http://unit-530.labs.intellij.net:8080/issue/BDP-652";
        login();
        driver.get(baseURL);
        //todo: waiting
        try {
            Thread.sleep(5000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        process(pageName, null, null, null);
//        WebElement e = driver.findElement(By.xpath("//div[@class='combobox']/../.."));
//        WebElement e = driver.findElement(By.cssSelector("div[class='combobox'][class='selectProject']"));
//        System.out.println(e.getAttribute("href"));
//        List<WebElement> elems = driver.findElements(By.cssSelector("input"));
//        for (WebElement elem : elems) {
//            System.out.println(createSelector(elem, Constants.xpath_selector));
//            System.out.println(createSelector(elem, Constants.css_selector));
//        }




        driver.close();
    }
}
