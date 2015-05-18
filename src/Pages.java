import java.util.ArrayList;

/**
 * Created by Ekaterina.Alekseeva on 19-Mar-15.
 */
public class Pages {
    public ArrayList<Page> pagesList = new ArrayList<Page>();

    public Pages(){
        addFSI();
    }

    private void addFSI(){
        String page = "FSI";
        String url = "localhost/issue/";
        ArrayList<String> termElems = new ArrayList<String>();
        termElems.add("button[cn*='goto']");
        termElems.add("div.contentWrapper > ul.comboboxList > li");
        termElems.add("div[class*='ring-header__user-menu-item'] button[class*='ring-btn']");
        termElems.add("div[id='ring-header-youtrack-create'] button[class*='ring-btn']");
//        termElems.add("div[class*='ring-btn-group'] button[id='id_l.I.tb.editIssueLink']");

        ArrayList<String> ignElems = new ArrayList<String>();
        ignElems.add("span.issue-toggler-ico");
        ignElems.add("a.issue-toggler");
        ignElems.add("a.disabled");
        ignElems.add("input[type='hidden']");
        ignElems.add("a.cf-value");
        ignElems.add("button[id='id_l.I.tb.c.ctms.afm.addFileMenu']");
        ignElems.add("button[id='id_l.I.tb.c.ctms.afm.addFileMenu'] span.ring-font-icon");
        ignElems.add("button[id='id_l.I.tb.c.ctms.afm.addFileMenu'] span.icon-paper-clip");
        ignElems.add("button[id='id_l.I.ic.it.c.ac.addFileMenu']");
        ignElems.add("button[id='id_l.I.ic.it.c.ac.addFileMenu'] span.ring-font-icon");
        ignElems.add("button[id='id_l.I.ic.it.c.ac.addFileMenu'] span.icon-paper-clip2");
        ignElems.add("span.sb-issue-edit-attach-btn-l");
        ignElems.add("span[label='Log Out']");
        ignElems.add("div[class*='yt-attach-file-dialog__permitted-group-fieldset'] div[class*='combobox'] a[class*='arrow']");
        ignElems.add("li[class*='nomatch']");
        ignElems.add("a[id*='deleteComment']");
        ignElems.add("div[class*='ring-btn-group'] button[id='id_l.I.tb.editIssueLink']");
//        TODO make it cond-terminal
        ignElems.add("div[class*='ring-btn-group'] button[id='id_l.I.tb.deleteIssueLink']");

        Page tmpPage = new Page(page, url, termElems, ignElems);

        SpecialConditionsElement el = new SpecialConditionsElement();
        el.selector = "span.comments-toggler-ico";
        el.action = "click";
        el.type = "search area";
        el.area = "div.issue-comments";
        tmpPage.specialConditionsElements.add(el);
//        el = new SpecialConditionsElement();
//        el.selector = "a[id*='deleteComment']";
//        el.action = "click";
//        el.type = "alert accept";
//        tmpPage.specialConditionsElements.add(el);

        System.out.println("new page added:");
        System.out.println(tmpPage.name);
        pagesList.add(tmpPage);
    }

    public class Page {
        public String name;
        public String url;
        public ArrayList<String> terminalElementsSelectors = new ArrayList<String>();
        public ArrayList<String> ignoredElementsSelectors = new ArrayList<String>();
        public ArrayList<SpecialConditionsElement> specialConditionsElements = new ArrayList<SpecialConditionsElement>();
        public ArrayList<ConditionallyTerminalElement> conditionallyTerminalElements = new ArrayList<ConditionallyTerminalElement>();

        public Page (String pageName, String pageUrl, ArrayList<String> termElems, ArrayList<String> ignElems){
            terminalElementsSelectors.add("a[href]");
            terminalElementsSelectors.add("button[id*='print']");
            terminalElementsSelectors.add("span.search-panel__search-ico");
            terminalElementsSelectors.add("span.search-panel__search-btn");

            name = pageName;
            url = pageUrl;
            terminalElementsSelectors.addAll(termElems);
            ignoredElementsSelectors.addAll(ignElems);
//            for (String sel : terminalElementsSelectors){
//                System.out.println(sel);
//            }
        }
    }
}
