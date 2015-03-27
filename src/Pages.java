import java.util.ArrayList;

/**
 * Created by Ekaterina.Alekseeva on 19-Mar-15.
 */
public class Pages {
    public ArrayList<Page> pagesList = new ArrayList<Page>();

    public Pages(){
//        ArrayList<Page> pagesList;
        String page = "FSI";
        String url = "localhost/issue/";
        ArrayList<String> termElems = new ArrayList<String>();
        termElems.add("button[cn*='goto']");
        termElems.add("div.contentWrapper > ul.comboboxList > li");
        Page tmpPage = new Page(page, url, termElems);
        System.out.println("new page added:");
        System.out.println(tmpPage.name);
        pagesList.add(tmpPage);
    }

    public class Page {
        public String name;
        public String url;
        public ArrayList<String> terminalElementsSelectors = new ArrayList<String>();
        //todo: specialConditionElements
        public ArrayList<ConditionallyTerminalElement> conditionallyTerminalElements = new ArrayList<ConditionallyTerminalElement>();

        public Page (String pageName, String pageUrl, ArrayList<String> termElems){
            terminalElementsSelectors.add("a[href]");
            terminalElementsSelectors.add("button[id*='print']");
            terminalElementsSelectors.add("span.search-panel__search-ico");
            terminalElementsSelectors.add("span.search-panel__search-btn");

            name = pageName;
            url = pageUrl;
            terminalElementsSelectors.addAll(termElems);
//            for (String sel : terminalElementsSelectors){
//                System.out.println(sel);
//            }
        }
    }
}
