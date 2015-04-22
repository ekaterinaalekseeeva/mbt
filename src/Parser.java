import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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

    public static ArrayList<WebElement> foundWebElements = new ArrayList<WebElement>();
    public static ArrayList<Element> foundElements = new ArrayList<Element>();

    public static int graphCounter = 0;

    public static String createSelector(WebElement element, String selectorType){
        StringBuilder curSel = new StringBuilder();
        String id = element.getAttribute("id");
        String title = element.getAttribute("title");
        String class_ = element.getAttribute("class");
        String cn = element.getAttribute("cn");
        String type = element.getAttribute("type");
        int counter = 0;

        if (selectorType.equals(Constants.xpath_selector)){
            curSel.append("//");
        }
        curSel.append(element.getTagName());

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
        } else {
            if (title != null && !title.equals("")){
                counter++;
                if (selectorType.equals(Constants.xpath_selector)){
                    if (counter == 1){
                        curSel.append("[");
                    } else{
                        curSel.append(" and ");
                    }
                    curSel.append("@title='");
                    curSel.append(title);
                    curSel.append("'");
                } else {
                    curSel.append("[title='");
                    curSel.append(title);
                    curSel.append("']");
                }
            }

            if (class_ != null && !class_.equals("")){
                counter++;
                String[] c = class_.split(" ");
                if (selectorType.equals(Constants.xpath_selector)){
                    if (counter == 1){
                        curSel.append("[");
                    } else{
                        curSel.append(" and ");
                    }
                    curSel.append("contains(@class, '");
                    curSel.append(c[0]);
                    curSel.append("')");
                } else {
                    curSel.append("[class*='");
                    curSel.append(c[0]);
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

            if (element.getTagName().equals("a") && (id == null || id.equals("")) && counter == 0){
                String href = element.getAttribute("pathname");
                if (href != null && !href.equals("")){
                    counter++;
                    if (selectorType.equals(Constants.xpath_selector)){
                        if (counter == 1){
                            curSel.append("[");
                        } else{
                            curSel.append(" and ");
                        }
                        curSel.append("@href='");
                        curSel.append(href);
                        curSel.append("'");
                    } else {
                        curSel.append("[href='");
                        curSel.append(href);
                        curSel.append("']");
                    }
                }
            }

            if (type != null && !type.equals("")){
                counter++;
                if (selectorType.equals(Constants.xpath_selector)){
                    if (counter == 1){
                        curSel.append("[");
                    } else{
                        curSel.append(" and ");
                    }
                    curSel.append("@type='");
                    curSel.append(type);
                    curSel.append("']");
                } else {
                    curSel.append("[type='");
                    curSel.append(type);
                    curSel.append("']");
                }
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
            if (!foundWebElements.contains(elem)) {
                foundWebElements.add(elem);
                if (!ignored) {
                    Element tmpEl = new Element();
                    tmpEl.setElement(elem);
                    tmpEl.setTerminal(terminal);
                    tmpEl.setSpecialCond(specialCond);
                    if (specialCond){
                        tmpEl.setSpCondEl(spCondEl);
                    }
                    tmpEl.setCondTerminal(condTerminal);
                    String xpathSelector = createSelector(elem, Constants.xpath_selector);
                    driver.findElement(By.xpath(xpathSelector));
                    WebElement par = driver.findElement(By.xpath(xpathSelector + "/.."));
                    String parSelector = createSelector(par, Constants.css_selector);
                    String fullSelector = createSelector(elem, Constants.css_selector);
//                    System.out.println(xpathSelector);
//                    System.out.println(parSelector + " " + fullSelector);
                    tmpEl.setSelector(parSelector + " " + fullSelector);
                    tmpEl.setNumber(elems.indexOf(elem));
                    tmpEl.setAction(action);
                    tmpEl.setParent(parent);
                    foundElements.add(tmpEl);
                }
            }
        }
    }

// Calls parsing method for different types of elements
    public static void parsingPage(String pageName, Element parent, String area, ArrayList<SpecialConditionsElement.AllowedSelector> selectors){
        System.out.println("parsingPage entered!");
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

            System.out.println(selector);
            parsingElements(selector, Constants.action_click, parent, true, false, false, false, null);
        }

        for (String s: terminal){
            if (area == null){
                selector = s;
            } else {
                selector = area + " " + s;
            }

            System.out.println(selector);
            parsingElements(selector, Constants.action_click, parent, false, true, false, false, null);
        }

        for (SpecialConditionsElement el: specialCond){
            if (area == null){
                selector = el.selector;
            } else {
                selector = area + " " + el.selector;
            }

            System.out.println(selector);
            parsingElements(selector, Constants.action_click, parent, false, false, true, false, el);
        }

        if (selectors == null) {
            for (String s : clickableElements) {
                parsingElements(s, Constants.action_click, parent, false, false, false, false, null);
            }

            for (String s : writableElements) {
                parsingElements(s, Constants.action_write, parent, false, false, false, false, null);
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
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("GUIgraph"+graphCounter));

            out.write("graph GUIgraph" + graphCounter + " {\n");
            StringBuilder curStr = new StringBuilder();
            String resStr;
            for (Element e: foundElements) {
                curStr.delete(0, curStr.length());
                curStr.append(e);
                curStr.append(" [label=\"");
                curStr.append(getElement(e).getTagName());
                curStr.append(" ");
                curStr.append(getElement(e).getText());
                curStr.append(" ");
                curStr.append(getElement(e).getAttribute("id"));
                curStr.append(" ");
                curStr.append(getElement(e).getAttribute("title"));
                curStr.append(getElement(e).getAttribute("class"));
                curStr.append(getElement(e).getAttribute("cn"));
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
        for (int i = numberOfElements; i < foundElements.size(); i++) {
            // Logging
            Element elem = foundElements.get(i);
            System.out.println("---------------------------------------------------------------------");
            System.out.println("New parsing:");
            System.out.println(elem);
            if (elem.getParent() == null) {
                System.out.print(elem + " " + getElement(elem).getTagName() + " " + getElement(elem).getAttribute("class") + " ");
                System.out.println(getElement(elem).getAttribute("id") + " Is terminal: " + elem.getTerminal() + " Is displayed: " + getElement(elem).isDisplayed());
            }

            // Processing of non-terminal common elements
            if (!elem.getTerminal() && !elem.getCondTerminal() && !elem.getSpecialCond() && getElement(elem).isDisplayed() && elem != parent) {
                System.out.println("------------- IF");
                if (elem.getParent() != null && !getElement(elem).isDisplayed()) {
                    //                        driver.navigate().refresh();
                    reproduceElementAppearance(elem);
                }

                System.out.println(getElement(elem).getTagName());
                                System.out.print(getElement(elem).getTagName() + " " + getElement(elem).getText() + " " + getElement(elem).getAttribute("id") + " ");
                                System.out.print(getElement(elem).getAttribute("title") + " " + getElement(elem).getAttribute("class") + " " + getElement(elem).getAttribute("cn"));
                                System.out.println(" Is displayed: " + getElement(elem).isDisplayed());

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
                System.out.println("Enter recursion");
                process(pageName, elem, null, null);
                System.out.println("Exit recursion");
                //                    driver.navigate().refresh();
                drawGraph();
            }


            if (elem.getSpecialCond() && !elem.getTerminal() && !elem.getCondTerminal() && getElement(elem).isDisplayed() && elem != parent) {
                if (elem.getParent() != null && !getElement(elem).isDisplayed()) {
//                        driver.navigate().refresh();
                    reproduceElementAppearance(elem);
                }

                if (elem.getSpCondEl().type.equals(Constants.type_search_area)){
                    getElement(elem).click();

                    //todo: wait until page is updated
                    try {
                        Thread.sleep(1500);                 //1000 milliseconds is one second.
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println("Enter recursion");
                    process(pageName, elem, elem.getSpCondEl().area, null);
                    System.out.println("Exit recursion");
                } else if (elem.getSpCondEl().type.equals(Constants.type_search_elements)){
                    getElement(elem).click();

                    //todo: wait until page is updated
                    try {
                        Thread.sleep(1500);                 //1000 milliseconds is one second.
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println("Enter recursion");
                    process(pageName, elem, null, elem.getSpCondEl().allowedSelectors);
                    System.out.println("Exit recursion");
                } else if (elem.getSpCondEl().type.equals(Constants.type_write)){
//                    getElement(elem).sendKeys(elem.getSpCondEl().allowedWrite);
                } else {
                    System.out.println("Unknown type");
                }
            }

            // todo: terminal processing
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
//        login();
        driver.get("http://unit-530.labs.intellij.net:8080/issue/BDP-652#tab=Similar%20Issues");
        //todo: waiting
        try {
            Thread.sleep(5000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        process(pageName, null, null, null);

        driver.close();
    }
}
