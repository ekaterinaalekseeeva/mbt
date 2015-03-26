import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ekaterina.Alekseeva on 11-Mar-15.
 */
public class Parser {
    private static WebDriver driver = new FirefoxDriver();
    private static Pages pages = new Pages();


//    TODO: think about adding other elements (div?)
    private static String[] clickableElements = {
            "a",
//            "a[href]",
            "button",
            "span.checkbox",
            "span[class*='toggle']",
            "span[class*='btn']",
            "td[class*='tabpanel']",
            "input[type='checkbox']",
            "ul.comboboxList > li"};
    private static String[] writableElements = {
//        TODO: write selectors for input properly
            "input",
//            "input button",
            "textarea"};

    public static ArrayList<WebElement> foundWebElements = new ArrayList<WebElement>();
    public static ArrayList<Element> foundElements = new ArrayList<Element>();

//    Checks page in current state, seeks for necessary elements, adds new appeared elements
    public static void parsingElements(String selector, String action, Element parent, boolean terminal){
        List<WebElement> elems = driver.findElements(By.cssSelector(selector));
        for (WebElement elem : elems) {
            if (!foundWebElements.contains(elem)) {
                Element tmpEl = new Element();
                tmpEl.setTerminal(terminal);
                tmpEl.setSelector(selector);
                tmpEl.setNumber(elems.indexOf(elem));
                tmpEl.setAction(action);
                tmpEl.setParent(parent);
                foundElements.add(tmpEl);
                foundWebElements.add(elem);
            }
        }
    }

// Calls parsing method for different types of elements
    public static void parsingPage(String pageName, Element parent){
        System.out.println("parsingPage entered!");
        ArrayList<String> terminal = new ArrayList<String>();
//        System.out.println(pages);
//        System.out.println(pages.pagesList);
        for (Pages.Page p : pages.pagesList){
//            System.out.println(p.name);
            if (p.name.equals(pageName)){
                terminal = p.terminalElementsSelectors;
            }
        }

        for (String selector: terminal){
            System.out.println(selector);
            parsingElements(selector, "click", parent, true);
        }

//        for (Element foundElement : foundElements) {
//            System.out.println(foundElement + " " + foundElement.getTerminal());
//        }

        for (String selector : clickableElements) {
            parsingElements(selector, "click", parent, false);
        }

        for (String selector : writableElements) {
            parsingElements(selector, "write", parent, false);
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
        List<WebElement> elems = driver.findElements(By.cssSelector(e.getSelector()));
        return elems.get(e.getNumber());
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
            if (elem.getAction().equals("click")){
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

    public static void process (String pageName, Element parent) {
        int numberOfElements = foundElements.size();

        parsingPage(pageName, parent);

//        markingElements();

//        for (Element foundElement : foundElements) {
//            System.out.println(foundElement + " " + foundElement.getElement());
//        }

//        System.out.println("webelements!!!");
//        for (WebElement e : foundWebElements){
//            System.out.println(e + e.getTagName() + e.getLocation());
//        }

//        If no new elements were found, exit function
        System.out.println("old size = " + numberOfElements + " new size = " + foundElements.size());
        if (foundElements.size() == numberOfElements){
            return;
        }

//        for (Element elem : foundElements) {
        for (int i = numberOfElements; i < foundElements.size(); i++) {
            // Logging
            Element elem = foundElements.get(i);
            System.out.println("---------------------------------------------------------------------");
            System.out.println("New parsing:");
            System.out.println(elem);
            if (elem.getParent() == null) {
                System.out.println(elem + " " + getElement(elem).getTagName() + " " + getElement(elem).getAttribute("class") + " " +
                        getElement(elem).getAttribute("id") + " Is terminal: " + elem.getTerminal() + " Is displayed: " + getElement(elem).isDisplayed());
            }

            // Processing of non-terminal common elements
            if (!elem.getTerminal() && !elem.getCondTerminal() && !elem.getSpecialCond() && getElement(elem).isDisplayed() && elem != parent) {
                System.out.println("------------- IF");
                System.out.println(getElement(elem).getTagName() + " " + getElement(elem).getText() + " " + getElement(elem).getAttribute("id") + " " +
                        getElement(elem).getAttribute("title") + " " + getElement(elem).getAttribute("class") + " " + getElement(elem).getAttribute("cn") + " Is displayed: " + getElement(elem).isDisplayed());
                if (elem.getParent() != null && !getElement(elem).isDisplayed()) {
//                        driver.navigate().refresh();
                    reproduceElementAppearance(elem);
                }
                if (elem.getAction().equals("click")) {
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
                process(pageName, elem);
                System.out.println("Exit recursion");
//                    driver.navigate().refresh();
            }

            // todo: special conditions processing
        }

        driver.close();
    }

    public static void main(String[] args) {
        String pageName = "FSI";
        driver.get("http://unit-530.labs.intellij.net:8080/issue/BDP-652");
        //todo: waiting
        try {
            Thread.sleep(5000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        process(pageName, null);
    }
}
