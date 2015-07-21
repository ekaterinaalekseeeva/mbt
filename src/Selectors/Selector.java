package Selectors;

import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.WebElement;
import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 7/21/15.
 */
public class Selector{
    /**
     *
     * @param element - element, that needs a CSS Filter
     * @return
     */
    public String getCSSFilter(WebElement element){
        return getFilter(element, true);
    }


    /**
     *
     * @param element - element, that needs a XPath Filter
     * @return
     */
    public String getXPathFilter(WebElement element){
        return getFilter(element, false);
    }

    /**
     *
     * @param element - Web element for CSS or XPATH Filter creation
     * @param css - TRUE if creating CSS FILTER
     * @return - String of CSS filter
     */
    private String getFilter(WebElement element, boolean css){
        StringBuilder filter = new StringBuilder();
        Map<String, String> attrrs = new HashMap<String, String>();
        for(String attr: Attributes.attributes){
            attrrs.put(attr, element.getAttribute(attr));
        }
        if(!css){
            filter.append("//");
        }
        filter.append(element.getTagName());
        //no attributes except href
        int f = 0;
        if(attrrs.get(Attributes.ID) != null && !attrrs.get(Attributes.ID).isEmpty()){
            if(css) {
                filter.append("[id='" + attrrs.get(Attributes.ID) + "']");
            } else {
                filter.append("[@id='" + attrrs.get(Attributes.ID) + "']");
            }
            return filter.toString();
        }
        if (attrrs.get(Attributes.TITLE) != null && !attrrs.get(Attributes.TITLE).isEmpty()) {
            //Dont know how to replace this, sapce for later work
            String s = null;
            if (element.getTagName().equals("a") && attrrs.get(Attributes.CLASS).contains("attribute")) {
                s = attrrs.get(Attributes.TITLE).split(":")[0];

            }
            if(css) {
                if (s != null) {
                    filter.append("[title*='" + s + "']");
                } else {
                    filter.append("[title='" + attrrs.get(Attributes.TITLE) + "']");
                }
            } else{
                filter.append("[");
                if(s != null){
                    filter.append("contains(@title, '"  + s + "')");
                }else{
                    filter.append("@title='" + attrrs.get(Attributes.TITLE) + "'");
                }
            }
            f ++;

        }
        if(attrrs.get(Attributes.CLASS) != null && !attrrs.get(Attributes.CLASS).isEmpty()){
            String[] parts = attrrs.get(Attributes.CLASS).split(" ");
            String s = attrrs.get(Attributes.CLASS);
            for(String part: parts){
                if(!part.isEmpty() && !part.equals("active")){
                    s = part;
                    break;
                }
            }
            if(css) {
                filter.append("[class*='" + s + "']");
            } else{
                if(f == 0){
                    filter.append("[");
                }else{
                    filter.append(" and ");
                }
                filter.append("contains(@class, '" + attrrs.get(Attributes.CLASS) + "')");
            }
            f ++;
        }
        for(String attr: Attributes.attributes){
            if(!attr.equals(Attributes.CLASS) && !attr.equals(Attributes.ID) &&
                    !attr.equals(Attributes.TITLE)){
                if(attr.equals(Attributes.VALUE) && (element.getTagName().equals("li") ||
                        element.getTagName().equals("input"))){
                    continue;
                }
                if(attrrs.get(attr) != null && !attrrs.get(attr).isEmpty()){
                    if(css) {
                        filter.append("[" + attr + "='" + attrrs.get(attr) + "']");
                    } else{
                        if(f == 0){
                            filter.append("[");
                        }else{
                            filter.append(" and ");
                        }
                        filter.append("@" + attr + "='" + attrrs.get(attr)+"'");
                    }
                    f ++;

                }
            }
        }
        if(element.getTagName().equals("a") && f==0){
            String href = element.getAttribute("href");
            String pathname = element.getAttribute("pathname");
            if(href != null && !href.isEmpty()){
                if(css) {
                    filter.append("[href='" + pathname + "'],[href='" + href + "']");
                } else {
                    filter.append("[@href='" + pathname + "' or @href='" + href + "'");
                }
                f ++;
            }
        }
        if(!css){
            filter.append(']');
        }
        return filter.toString();

    }
}
