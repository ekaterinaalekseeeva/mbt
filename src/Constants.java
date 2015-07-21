/**
 * Created by Ekaterina.Alekseeva on 09-Apr-15.
 */
public interface Constants {
    String action_click = "click";
    String action_write = "write";
    String type_write = "write";
    String type_search_elements = "search elements";
    String type_search_area = "search area";
    String type_alert_accept = "alert accept";
    String type_alert_decline = "alert decline";

    String xpath_selector = "xpath";
    String css_selector = "css";
    class Elements{
        static String[] terminalElements = {
                "button[cn*='goto']",
                "div.contentWrapper > ul.comboboxList > li",
                "div[class*='ring-header__user-menu-item'] button[class*='ring-btn']",
                "div[id='ring-header-youtrack-create'] button[class*='ring-btn']"};

        static String[] ignoredElements = {
                "span.issue-toggler-ico",
                "a.issue-toggler",
                "a.disabled",
                "input[type='hidden']",
                "a.cf-value",
                "button[id*='addFileMenu']",
                "button[id*='addFileMenu'] span",
                "a[id*='attachImageAndEdit']",
                "a[id*='uploadFileSecurely']",
                "a[id*='uploadFile']",
                "span.sb-issue-edit-attach-btn-l",
                "span[label='Log Out']",
                "div[class*='yt-attach-file-dialog__permitted-group-fieldset'] div[class*='combobox'] a[class*='arrow']",
                "li[class*='nomatch']",
                "a[id*='deleteComment']",
                "a[id*='editCommentLink']",
                "div[class*='ring-btn-group'] button[id='id_l.I.tb.editIssueLink'] span[class*='sb-toolbar-ico']",
                "div[class*='ring-btn-group'] button[id='id_l.I.tb.editIssueLink']",
                "div[class*='ring-btn-group'] button[id='id_l.I.tb.deleteIssueLink']"};


        static String[] clickableElements = {
                "a",
//            "a[href]",
                "button",
                "span.checkbox",
                "span[class*='toggle']",
                "span[class*='btn']",
                "td[class*='tabpanel-item']",
                "input[type='checkbox']",
                "ul.comboboxList > li"};

        static String[] writableElements = {
//        TODO: write selectors for input properly
                "input",
//            "input button",
                "textarea"};


    }
}
