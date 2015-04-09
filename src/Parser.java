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
                    tmpEl.setSelector(selector);
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

// Marking terminal (or terminal in special conditions) and requiring special handling elements
//    public static void markingElements () {
//        WebElement element; // web element of Element object
//        String tag; // web element tag name
//        for (Element elem : foundElements){
//            if (!elem.isMarked()){
//                element = getElement(elem);
//                tag = element.getTagName();
//                if ((tag.equals("a") && element.getAttribute("href") != null)||(tag.equals("button") && element.getAttribute("id").contains("print"))){
//                    elem.setTerminal(true);
//                }
//
//    //            TODO: get list of terminal and special elements for current page (e.g. from external file)
//
//                elem.makeMarked();
//            }
//        }
//    }

    public static WebElement getElement(Element e){
//        List<WebElement> elems = driver.findElements(By.cssSelector(e.getSelector()));
//        return elems.get(e.getNumber());
        return e.getElement();
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

    public static void main(String[] args) {
//        System.setProperty("webdriver.chrome.driver", "C:\\SeleniumWD\\chromedriver\\chromedriver.exe");
//        driver =  new ChromeDriver();
        String pageName = "FSI";
        driver.get("http://unit-530.labs.intellij.net:8080/issue/BDP-652#tab=Similar%20Issues");
        //todo: waiting
        try {
            Thread.sleep(5000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

//        driver.findElement(By.cssSelector("a.arrow")).click();
//        List<WebElement> elems = driver.findElements(By.cssSelector("div.contentWrapper > ul.comboboxList > li"));
//        List<WebElement> elems2 = driver.findElements(By.cssSelector("ul.comboboxList > li"));
//
//        for (WebElement el: elems){
//            System.out.println(el);
//        }
//
//        for (WebElement el: elems2){
//            System.out.println(el);
//        }

        process(pageName, null, null, null);
//        WebElement e1 = driver.findElement(By.cssSelector("div[id='ip_41_2596'] a[title*='Priority']"));
//        WebElement ee1 = driver.findElement(By.cssSelector("a[href='/rest/agile/TestAgileShortcuts-0/sprint']"));
//        driver.findElement(By.cssSelector("span[id='id_l.I.ic.it.si.si_41_2596.vi.ip.t.ct.toggleCommentsIco']")).click();
//        WebElement e2 = driver.findElement(By.cssSelector("div[id='ip_41_2596'] a[title*='Priority']"));
//        WebElement ee2 = driver.findElement(By.cssSelector("a[href='/rest/agile/TestAgileShortcuts-0/sprint']"));
//        System.out.println(e1);
//        System.out.println(e2);
//        System.out.println(e1.equals(e2));
//        System.out.println(ee1.equals(ee2));

        driver.close();
    }
}
